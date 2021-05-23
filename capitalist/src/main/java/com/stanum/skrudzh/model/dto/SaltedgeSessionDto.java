package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.model.enums.SaltedgeSessionTypeEnum;
import com.stanum.skrudzh.utils.constant.Constants;
import lombok.Data;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class SaltedgeSessionDto {

    public SaltedgeSessionDto(ConnectionEntity connectionEntity) {
        url = connectionEntity.getSessionUrl();
        type = connectionEntity.getSessionType();
        expiresAt = connectionEntity.getSessionUrlExpiresAt() != null
                ? ZonedDateTime.of(connectionEntity.getSessionUrlExpiresAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
    }

    private String url;

    private SaltedgeSessionTypeEnum type;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime expiresAt;
}
