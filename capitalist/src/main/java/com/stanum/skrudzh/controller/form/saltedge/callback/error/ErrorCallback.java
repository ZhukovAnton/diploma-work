package com.stanum.skrudzh.controller.form.saltedge.callback.error;

import com.stanum.skrudzh.controller.form.saltedge.callback.Meta;
import lombok.Data;

/**
 * https://docs.saltedge.com/account_information/v5/#callbacks-failure
 *
 * {
 *   "data": {
 *     "connection_id": "111111111111111111",
 *     "customer_id": "222222222222222222",
 *     "custom_fields": { "key": "value" },
 *     "error_class": "InvalidCredentials",
 *     "error_message": "Invalid credentials."
 *   },
 *   "meta": {
 *     "version": "5",
 *     "time": "2020-10-30T14:33:57.725Z"
 *   }
 * }
 */
@Data
public class ErrorCallback {
    private ErrorData data;
    private Meta meta;
}
