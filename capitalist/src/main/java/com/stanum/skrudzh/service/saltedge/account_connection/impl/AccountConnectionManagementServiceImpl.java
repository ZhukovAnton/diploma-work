package com.stanum.skrudzh.service.saltedge.account_connection.impl;

import com.stanum.skrudzh.controller.form.saltedge.AccountConnectionAttributes;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.model.base.Connectable;
import com.stanum.skrudzh.jpa.repository.AccountConnectionRepository;
import com.stanum.skrudzh.model.enums.LastStageStatusEnum;
import com.stanum.skrudzh.saltage.SaltedgeAPI;
import com.stanum.skrudzh.saltage.model.Transaction;
import com.stanum.skrudzh.service.saltedge.SaltEdgeTransactionService;
import com.stanum.skrudzh.service.saltedge.account.AccountFinder;
import com.stanum.skrudzh.service.saltedge.account_connection.AccountConnectionManagementService;
import com.stanum.skrudzh.service.saltedge.connectable.ConnectableCalculationService;
import com.stanum.skrudzh.service.saltedge.connectable.ConnectableFinder;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionFinder;
import com.stanum.skrudzh.utils.LoggerUtil;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountConnectionManagementServiceImpl implements AccountConnectionManagementService {

    private final EntityUtil entityUtil;

    private final AccountConnectionRepository accountConnectionRepository;

    private final AccountFinder accountFinder;

    private final SaltEdgeTransactionService saltEdgeTransactionService;

    private final SaltedgeAPI saltEdgeAPI;

    private final ConnectionFinder connectionFinder;

    private final ConnectableCalculationService connectableCalculationService;

    private final ConnectableFinder connectableFinder;

    public void updateOrCreateAccountConnection(Connectable connectable, AccountConnectionAttributes accountAttributes) {
        if (accountAttributes == null) return;
        if (accountAttributes.getDestroy() != null && accountAttributes.getDestroy()) {
            log.info("Try to destroy account connection");
            if (connectable.getAccountConnectionEntity() != null) {
                AccountConnectionEntity accountConnectionToDestroy = connectable.getAccountConnectionEntity();
                destroyAccountConnection(accountConnectionToDestroy);
            }
            return;
        }
        if (accountAttributes.getConnectionId() != null) {
            processConnection(accountAttributes, connectable);
        }
        if (accountAttributes.getAccountId() != null) {
            processAccount(accountAttributes, connectable);
        }
    }

    public void updateAccountConnectionWithAccount(
            AccountConnectionEntity accountConnectionEntity,
            AccountEntity accountEntity,
            List<Transaction> transactionsFromSaltEdge) {
        accountConnectionEntity.setAccountEntity(accountEntity);
        save(accountConnectionEntity);
        Connectable connectable = connectableFinder.find(
                accountConnectionEntity.getSourceId(),
                accountConnectionEntity.getSourceType());
        connectable.setBalance(accountEntity.getBalance());
        entityUtil.save(connectable);
        updateTransactions(accountConnectionEntity, transactionsFromSaltEdge, connectable);
    }

    public void save(AccountConnectionEntity accountConnectionEntity) {
        accountConnectionRepository.save(accountConnectionEntity);
    }

    public void destroyAccountConnection(AccountConnectionEntity accountConnectionEntity) {
        destroyAccountConnection(accountConnectionEntity,
                connectableFinder.find(
                        accountConnectionEntity.getSourceId(),
                        accountConnectionEntity.getSourceType()));
    }

    public void destroyAccountConnection(AccountConnectionEntity accountConnectionEntity, Connectable connectable) {
        log.info("Destroy Account Connection with id={}, connectableId={}", accountConnectionEntity.getId(), connectable.getId());
        beforeDestroy(connectable);
        accountConnectionRepository.delete(accountConnectionEntity);
    }

    private void beforeDestroy(Connectable connectable) {
        if (connectable instanceof ActiveEntity) {
            ActiveEntity activeEntity = (ActiveEntity) connectable;
            activeEntity.setAccountConnectionEntity(null);
            entityUtil.save(activeEntity);
        } else {
            ExpenseSourceEntity expenseSourceEntity = (ExpenseSourceEntity) connectable;
            expenseSourceEntity.setAccountConnectionEntity(null);
            entityUtil.save(expenseSourceEntity);
        }
    }

    private void processConnection(AccountConnectionAttributes attributes, Connectable connectable) {
        AccountConnectionEntity accountConnection = connectable.getAccountConnectionEntity();
        if (accountConnection != null
                && accountConnection.getConnectionEntity() != null
                && attributes.getConnectionId()
                .equals(accountConnection
                        .getConnectionEntity()
                        .getId())) return;

        ConnectionEntity connectionEntity = connectionFinder.findById(attributes.getConnectionId());
        if (accountConnection != null) {
            updateAccountConnectionWithConnection(accountConnection, connectionEntity);
        } else {
            AccountConnectionEntity accountConnectionEntity = createAccountConnectionWithConnection(connectionEntity, connectable);
            save(accountConnectionEntity);
            connectable.setAccountConnectionEntity(accountConnectionEntity);
            entityUtil.save(connectable);
        }
    }

    private void processAccount(AccountConnectionAttributes attributes, Connectable connectable) {
        AccountConnectionEntity accountConnection = connectable.getAccountConnectionEntity();
        if (accountConnection != null
                && accountConnection.getAccountEntity() != null
                && attributes.getAccountId()
                .equals(accountConnection.getAccountEntity().getId())) return;

        AccountEntity accountEntity = accountFinder
                .findByApiId(attributes.getAccountId());
        if (accountConnection != null) {
            updateAccountConnectionWithAccount(accountConnection, accountEntity);
        } else {
            accountConnection = createAccountConnectionWithAccount(accountEntity, connectable);
            save(accountConnection);
            connectable.setAccountConnectionEntity(accountConnection);
            connectable.setBalance(accountEntity.getBalance());
            entityUtil.save(connectable);
            updateTransactions(accountConnection, connectable);
        }
    }

    private AccountConnectionEntity createAccountConnectionWithConnection(ConnectionEntity connectionEntity, Connectable connectable) {
        AccountConnectionEntity accountConnectionEntity = new AccountConnectionEntity();
        accountConnectionEntity.setConnectionEntity(connectionEntity);
        accountConnectionEntity.setSourceId(connectable.getId());
        accountConnectionEntity.setSourceType(connectable.getEntityType());
        return accountConnectionEntity;
    }

    private AccountConnectionEntity createAccountConnectionWithAccount(AccountEntity accountEntity, Connectable connectable) {
        AccountConnectionEntity accountConnectionEntity = new AccountConnectionEntity();
        accountConnectionEntity.setAccountEntity(accountEntity);
        accountConnectionEntity.setConnectionEntity(accountEntity.getConnectionEntity());
        accountConnectionEntity.setSourceId(connectable.getId());
        accountConnectionEntity.setSourceType(connectable.getEntityType());
        return accountConnectionEntity;
    }

    private void updateAccountConnectionWithConnection(AccountConnectionEntity accountConnectionEntity, ConnectionEntity connectionEntity) {
        accountConnectionEntity.setConnectionEntity(connectionEntity);
        save(accountConnectionEntity);
    }

    private void updateAccountConnectionWithAccount(AccountConnectionEntity accountConnectionEntity, AccountEntity accountEntity) {
        List<Transaction> saltEdgeTransactions =
                saltEdgeAPI.custom
                        .findAllTransactions(accountEntity.getConnectionEntity().getSaltEdgeConnectionId(),
                                accountEntity.getAccountId());
        updateAccountConnectionWithAccount(accountConnectionEntity, accountEntity, saltEdgeTransactions);
    }

    private void updateTransactions(AccountConnectionEntity accountConnectionEntity, Connectable connectable) {
        AccountEntity accountEntity = accountConnectionEntity.getAccountEntity();
        List<Transaction> saltEdgeTransactions =
                saltEdgeAPI.custom
                        .findAllTransactions(accountEntity.getConnectionEntity().getSaltEdgeConnectionId(),
                                accountEntity.getAccountId());
        updateTransactions(accountConnectionEntity, saltEdgeTransactions, connectable);
    }

    private void updateTransactions(AccountConnectionEntity accountConnectionEntity,
                                    List<Transaction> saltEdgeTransactions,
                                    Connectable connectable) {
        log.info("Update {} transactions for accountConnectionEntityId={} :{}",
                saltEdgeTransactions.size(), accountConnectionEntity.getId(), LoggerUtil.printTrs(saltEdgeTransactions));
        LocalDateTime fetchFromDate = LocalDateTime
                .ofInstant(
                        connectableCalculationService.calculateFetchFromDate(connectable).toInstant(),
                        ZoneId.of("Z"));
        log.info("Fetch from date {} for connectableId={}, type={}", fetchFromDate, connectable.getId(), connectable.getEntityType());
        if (!saltEdgeTransactions.isEmpty()) {
            List<Transaction> actualTransactions =
                    saltEdgeTransactions.stream()
                            .filter(transaction -> transaction.getCreatedAt().isAfter(fetchFromDate))
                            .collect(Collectors.toList());
            saltEdgeTransactionService.refreshTransactions(actualTransactions, accountConnectionEntity);
            if (accountConnectionEntity.getConnectionEntity().getLastStageStatus()
                .equals(LastStageStatusEnum.finish)) saltEdgeTransactionService.synchronizeBalances(connectable);
        }
    }


}
