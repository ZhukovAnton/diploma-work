package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.WebCredentials;
import lombok.Data;

@Data
public class WebCredentialsResponse {
    private final WebCredentials webcredentials = new WebCredentials();
}
