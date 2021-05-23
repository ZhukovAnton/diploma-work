package com.stanum.skrudzh.controller.form.saltedge.callback.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stanum.skrudzh.controller.form.saltedge.callback.AbstractCallbackData;
import lombok.Data;
import lombok.ToString;

/**
 *  "data": {
 *     "connection_id": "111111111111111111",
 *     "customer_id": "222222222222222222",
 *     "custom_fields": { "key": "value" },
 *     "error_class": "InvalidCredentials",
 *     "error_message": "Invalid credentials."
 *   }
 */
@Data
@ToString(callSuper = true)
public class ErrorData extends AbstractCallbackData {

    @JsonProperty("error_class")
    private String errorClass;

    @JsonProperty("error_message")
    private String errorMessage;
}
