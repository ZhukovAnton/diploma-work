package com.stanum.skrudzh.controller.form.saltedge;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.stanum.skrudzh.model.enums.SaltedgeSessionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaltedgeSession {

    @JsonProperty("url")
    private String url;

    @JsonProperty("type")
    private SaltedgeSessionTypeEnum type;

    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;

}
