package com.stanum.skrudzh.controller.form.saltedge.callback.notify;

import com.stanum.skrudzh.controller.form.saltedge.callback.Meta;
import lombok.Data;

/**
 * SaltEdge documentation: https://docs.saltedge.com/account_information/v5/#callbacks-notify
 * Example: {
 *   "data": {
 *     "connection_id": "111111111111111111",
 *     "customer_id": "222222222222222222",
 *     "custom_fields": { "key": "value" },
 *     "stage": "start"
 *   },
 *   "meta": {
 *     "version": "5",
 *     "time": "2020-09-22T10:58:44Z"
 *   }
 * }
 */

@Data
public class NotifyCallback {
    private NotifyData data;
    private Meta meta;
}
