package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.saltage.model.Connection;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.constant.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class ConnectionDto {

    public ConnectionDto(ConnectionEntity connectionEntity) {
        id = connectionEntity.getId();
        saltedgeConnectionId = connectionEntity.getSaltEdgeConnectionId();
        countryCode = connectionEntity.getCountryCode();

        String requiredInteractiveFieldsNames = connectionEntity.getRequiredInteractiveFieldsNames();
        if(requiredInteractiveFieldsNames == null || requiredInteractiveFieldsNames.isEmpty()) {
            this.requiredInteractiveFieldsNames = new ArrayList<>();
        } else {
            this.requiredInteractiveFieldsNames = Arrays.asList(requiredInteractiveFieldsNames.split(","));
        }
        createdAt = connectionEntity.getCreatedAt() != null
                ? ZonedDateTime.of(connectionEntity.getCreatedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        updatedAt = connectionEntity.getUpdatedAt() != null
                ? ZonedDateTime.of(connectionEntity.getUpdatedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        providerCode = connectionEntity.getProviderCode();
        providerName = connectionEntity.getProviderName();
        providerId = connectionEntity.getProviderId();
        providerLogoUrl = connectionEntity.getProviderLogoUrl();
        status = connectionEntity.getStatus().toString();
        userId = connectionEntity.getUser().getId();
        secret = connectionEntity.getSecret();
        nextRefreshPossibleAt = connectionEntity.getNextRefreshPossibleAt() != null
                ? ZonedDateTime.of(connectionEntity.getNextRefreshPossibleAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        lastSuccessAt = connectionEntity.getLastSuccessAt() != null
                ? ZonedDateTime.of(connectionEntity.getLastSuccessAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        interactive = connectionEntity.getInteractive();
        lastStage = connectionEntity.getLastStageStatus() != null
                ? connectionEntity.getLastStageStatus().toString()
                : null;
        saltedgeConnectionSession = isNeedToCreateSessionDto(connectionEntity)
                ? new SaltedgeSessionDto(connectionEntity)
                : null;
        connectionSessionUrlExpiresAt = connectionEntity.getSessionUrlExpiresAt() != null
                ? ZonedDateTime.of(connectionEntity.getSessionUrlExpiresAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
    }

    public ConnectionDto(Connection connection) {
        saltedgeConnectionId = connection.getId();
        countryCode = connection.getCountry_code();
        createdAt = connection.getCreatedAt() != null
                ? ZonedDateTime.of(connection.getCreatedAt(), ZoneId.of("Z"))
                : null;
        updatedAt = connection.getUpdatedAt() != null
                ? ZonedDateTime.of(connection.getUpdatedAt(), ZoneId.of("Z"))
                : null;
        nextRefreshPossibleAt = connection.getNextRefreshPossibleAt() != null
                ? ZonedDateTime.of(connection.getNextRefreshPossibleAt(), ZoneId.of("Z"))
                : null;
        lastSuccessAt = connection.getLastSuccessAt() != null
                ? ZonedDateTime.of(connection.getLastSuccessAt(), ZoneId.of("Z"))
                : null;
        providerCode = connection.getProviderCode();
        providerName = connection.getProviderName();
        providerId = connection.getProviderId();
        status = connection.getStatus();
        userId = RequestUtil.getUser().getId();
        secret = connection.getSecret();
        interactive = connection.getLastAttempt() != null
                ? connection.getLastAttempt().getInteractive()
                : null;
        lastStage = connection.getLastAttempt() != null
                && connection.getLastAttempt().getLastStage() != null
                && connection.getLastAttempt().getLastStage().getName() != null
                ? connection.getLastAttempt().getLastStage().getName()
                : null;
        requiredInteractiveFieldsNames = new ArrayList<>();
    }

    private Long id;

    private String saltedgeConnectionId;

    private String countryCode;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime updatedAt;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime nextRefreshPossibleAt;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime lastSuccessAt;

    private String providerCode;

    private String providerName;

    private String providerId;

    private String providerLogoUrl;

    private String status;

    private String secret;

    private Boolean interactive;

    private Long userId;

    private String lastStage;

    private List<String> requiredInteractiveFieldsNames;

    private SaltedgeSessionDto saltedgeConnectionSession;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime connectionSessionUrlExpiresAt;

    private boolean isNeedToCreateSessionDto(ConnectionEntity connectionEntity) {
        return connectionEntity.getSessionUrl() != null
                || connectionEntity.getSessionType() != null
                || connectionEntity.getSessionUrlExpiresAt() != null;
    }
}
