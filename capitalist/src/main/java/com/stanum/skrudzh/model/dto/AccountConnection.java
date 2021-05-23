package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.utils.constant.Constants;
import lombok.Data;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class AccountConnection {

    private Long id;

    private Account account;

    private Long sourceId;

    private String sourceType;

    private ConnectionDto connection;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    public AccountConnection(AccountConnectionEntity accountConnectionEntity) {
        id = accountConnectionEntity.getId();
        sourceId = accountConnectionEntity.getSourceId();
        sourceType = accountConnectionEntity.getSourceType().name();
        createdAt = accountConnectionEntity.getCreatedAt() != null
                ? ZonedDateTime.of(accountConnectionEntity.getCreatedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
    }

}
