package com.stanum.skrudzh.service.saltedge.connection.impl;

import com.stanum.skrudzh.controller.form.ConnectionCreationForm;
import com.stanum.skrudzh.controller.form.ConnectionRefreshForm;
import com.stanum.skrudzh.controller.form.saltedge.InteractiveParams;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.ConnectionsRepository;
import com.stanum.skrudzh.model.enums.ConnectionStatusEnum;
import com.stanum.skrudzh.model.enums.LastStageStatusEnum;
import com.stanum.skrudzh.model.enums.ProviderStatusEnum;
import com.stanum.skrudzh.saltage.SaltedgeAPI;
import com.stanum.skrudzh.saltage.model.Account;
import com.stanum.skrudzh.saltage.model.Connection;
import com.stanum.skrudzh.saltage.model.Response;
import com.stanum.skrudzh.service.saltedge.SaltEdgeTransactionService;
import com.stanum.skrudzh.service.saltedge.account.AccountFinder;
import com.stanum.skrudzh.service.saltedge.account.AccountManagementService;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionFinder;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionService;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final SaltEdgeTransactionService transactionService;

    private final AccountManagementService accountManagementService;

    private final ConnectionFinder connectionFinder;

    private final AccountFinder accountFinder;

    private final ConnectionsRepository connectionsRepository;

    private final SaltedgeAPI saltedgeAPI;

    public void refreshConnection(ConnectionEntity connectionEntity, ConnectionRefreshForm.ConnectionRF form) {
        log.info("Refresh connection - connectionEntityId={}, form={}", connectionEntity.getId(), form);
        String actualSaltEdgeConnectionId = getActualSaltEdgeConnection(connectionEntity, form);
        fillConnectionWithFormData(connectionEntity, form);
        updateConnectionEntityWithSaltEdgeData(connectionEntity, actualSaltEdgeConnectionId);
        save(connectionEntity);
        if (connectionEntity.getStatus().equals(ConnectionStatusEnum.deleted)
                && connectionEntity.getSaltEdgeConnectionId() == null
                || form != null && form.isRefreshOnlyConnectionData()) {
            log.info("Can't refresh connection id={}", connectionEntity.getId());
            return;
        }

        boolean isNeedToReconnectAccounts = isNeedToReconnectAccounts(actualSaltEdgeConnectionId, connectionEntity.getSaltEdgeConnectionId());
        if (isNeedToReconnectAccounts) {
            reconnectAccounts(connectionEntity);
        }

        if(!connectionEntity.getInteractive()) {
            refreshAccounts(connectionEntity);
            if (LastStageStatusEnum.finish.equals(connectionEntity.getLastStageStatus())) {
                refreshConnectionByStatus(connectionEntity);
            }
        }
    }

    public void interactive(ConnectionEntity connectionEntity, ConnectionRefreshForm.ConnectionRF form) {
        log.info("Interactive connection - connectionEntityId={}, form={}", connectionEntity.getId(), form);
        String actualSaltEdgeConnectionId = getActualSaltEdgeConnection(connectionEntity, form);
        fillConnectionWithFormData(connectionEntity, form);
        try {
            sendSaltEdgeCredentials(actualSaltEdgeConnectionId, form.getInteractiveCredentials());
            log.info("Passwords were sent successfully");
        } catch (Throwable e) {
            log.error("Error while sending SaltEdge credentials", e);
        }
        connectionEntity.setUpdatedAt(TimeUtil.now());
        connectionEntity.setRequiredInteractiveFieldsNames(null);
        save(connectionEntity);
    }

    public void destroyConnection(ConnectionEntity connectionEntity, boolean withTransactions) {
        Set<AccountEntity> accountEntities = accountFinder.findAccountsByConnection(connectionEntity);
        accountEntities.forEach(account -> accountManagementService
                .destroyAccount(account, withTransactions));
        saltedgeAPI.connection.destroy(connectionEntity.getSaltEdgeConnectionId());
        connectionsRepository.delete(connectionEntity);
    }

    public void refreshAccounts(ConnectionEntity connectionEntity) {
        log.info("Refresh accounts for connectionEntityId={}", connectionEntity.getId());
        List<Account> connectionAccounts = saltedgeAPI
                .custom.findAllAccountsWithTransactions(connectionEntity.getSaltEdgeConnectionId());
        accountManagementService.refreshAccounts(connectionAccounts, connectionEntity);
    }

    public ConnectionEntity createConnectionEntity(UserEntity userEntity, ConnectionCreationForm.ConnectionCF form) {
        ConnectionEntity connectionEntity = new ConnectionEntity();
        connectionEntity.setUser(userEntity);
        checkAndFillConnectionWithCreationForm(connectionEntity, form);
        save(connectionEntity);
        return connectionEntity;
    }

    private String getActualSaltEdgeConnection(ConnectionEntity connectionEntity, ConnectionRefreshForm.ConnectionRF form) {
        return form != null && form.getSaltEdgeConnectionId() != null && !form.getSaltEdgeConnectionId().isBlank()
                ? form.getSaltEdgeConnectionId()
                : connectionEntity.getSaltEdgeConnectionId();
    }

    private boolean isNeedToReconnectAccounts(String actualSaltEdgeConnectionId, String saltEdgeConnectionId) {
        return actualSaltEdgeConnectionId != null
                && saltEdgeConnectionId != null
                && !actualSaltEdgeConnectionId.equals(saltEdgeConnectionId);
    }

    private void reconnectAccounts(ConnectionEntity connectionEntity) {
        log.info("Reconnect account for connectionEntityId={}", connectionEntity.getId());
        List<Account> saltEdgeAccounts = saltedgeAPI.custom
                .findAllAccounts(connectionEntity.getSaltEdgeConnectionId());
        accountManagementService.reconnectAccounts(saltEdgeAccounts, connectionEntity);
    }

    private void checkAndFillConnectionWithCreationForm(ConnectionEntity connectionEntity, ConnectionCreationForm.ConnectionCF form) {
        if (connectionFinder.findBySaltEdgeId(form.getSaltEdgeConnectionId()).isPresent()) {
            log.error("Duplicated connection {}", form.getSaltEdgeConnectionId());
            throw new AppException(HttpAppError.DUPLICATED_CONNECTION);
        }
        connectionEntity.setProviderName(form.getProviderName());
        connectionEntity.setProviderCode(form.getProviderCode());
        connectionEntity.setProviderLogoUrl(form.getLogoUrl());
        connectionEntity.setProviderId(form.getProviderId());
        updateConnectionEntityWithSaltEdgeData(connectionEntity, form.getSaltEdgeConnectionId());
        if (form.getSaltedgeConnectionSession() != null) {
            connectionEntity.setSessionUrl(form.getSaltedgeConnectionSession().getUrl());
            connectionEntity.setSessionType(form.getSaltedgeConnectionSession().getType());
            connectionEntity.setSessionUrlExpiresAt(Timestamp.valueOf(form.getSaltedgeConnectionSession().getExpiresAt()));
        }
    }

    public void refreshConnectionByStatus(ConnectionEntity connectionEntity) {
        log.info("Refresh connection by status for connectionEntityId={}, status={}",
                connectionEntity.getId(), connectionEntity.getStatus());
        if (connectionEntity.getStatus().equals(ConnectionStatusEnum.active)) {
            if (isNextRefreshPossible(connectionEntity)) {
                log.info("[Refresh connection] ConnectionEntityId={}", connectionEntity.getId());
                Timestamp start = TimeUtil.now();
                saltedgeAPI.connection.refresh(connectionEntity.getSaltEdgeConnectionId());
                log.info("Refresh took: " + (TimeUtil.now().getTime() - start.getTime()) + "ms");
                updateConnectionEntityWithSaltEdgeData(connectionEntity, connectionEntity.getSaltEdgeConnectionId());
                save(connectionEntity);
                refreshAccounts(connectionEntity);
            }
        } else if (connectionEntity.getStatus().equals(ConnectionStatusEnum.deleted)) {
            log.info("[Destroy Connection] ConnectionEntityId={}", connectionEntity.getId());
            saltedgeAPI.connection.destroy(connectionEntity.getSaltEdgeConnectionId());
            transactionService.destroyConnectionTransactions(connectionEntity);
            connectionEntity.setStatus(ConnectionStatusEnum.deleted);
            connectionEntity.setUpdatedAt(TimeUtil.now());
            save(connectionEntity);
        }
    }

    private boolean isNextRefreshPossible(ConnectionEntity connectionEntity) {
        return connectionEntity.getNextRefreshPossibleAt() != null
                && connectionEntity.getNextRefreshPossibleAt().before(TimeUtil.now())
                && Boolean.FALSE.equals(connectionEntity.getInteractive());
    }

    private void fillConnectionWithFormData(ConnectionEntity connectionEntity, ConnectionRefreshForm.ConnectionRF form) {
        if (form == null || form.getSaltedgeConnectionSession() == null) return;
        connectionEntity.setSessionUrl(form.getSaltedgeConnectionSession().getUrl());
        connectionEntity.setSessionUrlExpiresAt(Timestamp.valueOf(form.getSaltedgeConnectionSession().getExpiresAt()));
        connectionEntity.setSessionType(form.getSaltedgeConnectionSession().getType());
    }

    private void updateConnectionEntityWithSaltEdgeData(ConnectionEntity connectionEntity, String actualSaltEdgeConnectionId) {
        log.info("Update connection with SaltEdge data, connectionEntityId={}, actualSaltEdgeConnectionId={}",
                connectionEntity.getId(), actualSaltEdgeConnectionId);
        Optional<Connection> connection = getConnectionFromSaltEdgeApi(actualSaltEdgeConnectionId);
        connectionEntity.setUpdatedAt(TimeUtil.now());
        if (connection.isEmpty() || connection.get().getStatus().equals("deleted")) {
            connectionEntity.setSaltEdgeConnectionId(null);
            connectionEntity.setStatus(ConnectionStatusEnum.deleted);
            save(connectionEntity);
            return;
        }
        fillConnectionEntityWithSaltEdgeConnection(connectionEntity, connection.get());
    }

    private Optional<Connection> getConnectionFromSaltEdgeApi(String saltEdgeConnectionId) {
        Response<Connection> saltEdgeConnectionResponse = saltedgeAPI.connection.show(saltEdgeConnectionId);
        log.info("SaltEdge connection response: {}", saltEdgeConnectionResponse);
        if (!saltEdgeConnectionResponse.getData().isEmpty()) {
            return Optional.of(saltEdgeConnectionResponse.getData().get(0));
        } else {
            return Optional.empty();
        }
    }

    private void sendSaltEdgeCredentials(String saltEdgeConnectionId, List<InteractiveParams> params) {
        Objects.requireNonNull(saltEdgeConnectionId, "Null SaltEdge connection id");
        Response<Connection> interactive = saltedgeAPI.connection.interactive(saltEdgeConnectionId, params);
        log.info("Interactive response: {}", interactive);
    }

    private void fillConnectionEntityWithSaltEdgeConnection(ConnectionEntity connectionEntity, Connection saltEdgeConnection) {
        if (connectionEntity.getSaltEdgeConnectionId() == null
                || connectionEntity.getSaltEdgeConnectionId() != null
                && !connectionEntity.getSaltEdgeConnectionId().equals(saltEdgeConnection.getId())) {
            connectionEntity.setSaltEdgeConnectionId(saltEdgeConnection.getId());
        }
        connectionEntity.setCountryCode(saltEdgeConnection.getCountry_code());
        connectionEntity.setCustomerId(saltEdgeConnection.getCustomerId());
        connectionEntity.setSecret(saltEdgeConnection.getSecret());
        connectionEntity
                .setStatus(
                        getConnectionStatusFromProviderStatus(
                                ProviderStatusEnum.valueOf(saltEdgeConnection.getStatus())));
        connectionEntity.setCreatedAt(Timestamp.valueOf(saltEdgeConnection.getCreatedAt()));
        connectionEntity.setNextRefreshPossibleAt(saltEdgeConnection.getNextRefreshPossibleAt() != null
                ? Timestamp.valueOf(saltEdgeConnection.getNextRefreshPossibleAt())
                : null);
        connectionEntity.setLastSuccessAt(saltEdgeConnection.getLastSuccessAt() != null
                ? Timestamp.valueOf(saltEdgeConnection.getLastSuccessAt())
                : null);
        connectionEntity.setInteractive(saltEdgeConnection.getLastAttempt() != null
                ? saltEdgeConnection.getLastAttempt().getInteractive()
                : null);

        LastStageStatusEnum lastStageStatus = getLastStageStatus(saltEdgeConnection);

        log.info("Change last status {} --> {} for connectionEntityId={}", connectionEntity.getLastStageStatus(), lastStageStatus, connectionEntity.getId());
        connectionEntity.setLastStageStatus(lastStageStatus);
    }

    private LastStageStatusEnum getLastStageStatus(Connection saltEdgeConnection) {
        return saltEdgeConnection.getLastAttempt() != null
                && saltEdgeConnection.getLastAttempt().getLastStage() != null
                && saltEdgeConnection.getLastAttempt().getLastStage().getName() != null
                ? LastStageStatusEnum.valueOf(saltEdgeConnection.getLastAttempt().getLastStage().getName())
                : null;
    }

    private ConnectionStatusEnum getConnectionStatusFromProviderStatus(ProviderStatusEnum providerStatus) {
        switch (providerStatus) {
            case active:
                return ConnectionStatusEnum.active;
            case inactive:
                return ConnectionStatusEnum.inactive;
            default:
                return ConnectionStatusEnum.deleted;
        }
    }

    public void save(ConnectionEntity connectionEntity) {
        connectionsRepository.save(connectionEntity);
    }
}
