package com.stanum.skrudzh.controller.form.saltedge.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class AbstractCallbackData {
    @JsonProperty("connection_id")
    private String connectionId;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("custom_fields")
    private Map<String, String> customFields;
}
