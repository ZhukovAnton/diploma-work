package com.stanum.skrudzh.controller.form.saltedge.callback.notify;

import com.stanum.skrudzh.controller.form.saltedge.callback.AbstractCallbackData;
import lombok.Data;
import lombok.ToString;

/**
 * Example:      "data": {
 *                 "connection_id": "111111111111111111",
 *                 "customer_id": "222222222222222222",
 *                 "custom_fields": { "key": "value" },
 *                 "stage": "start"
 *                 }
 */

@Data
@ToString(callSuper = true)
public class NotifyData extends AbstractCallbackData {

    private String stage;
}