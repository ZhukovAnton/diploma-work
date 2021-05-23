package com.stanum.skrudzh.controller.form.saltedge.callback.interactive;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stanum.skrudzh.controller.form.saltedge.callback.AbstractCallbackData;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * "data": {
 *     "connection_id": "111111111111111111",
 *     "customer_id": "222222222222222222",
 *     "custom_fields": { "key": "value" },
 *     "stage": "interactive",
 *     "html": "<p>Please select accounts from the list:</p>",
 *     "session_expires_at": "2020-07-06T11:27:10Z",
 *     "interactive_fields_names": ["accounts"],
 *     "interactive_fields_options": {
 *       "accounts": [
 *         {
 *           "name":           "account1",
 *           "english_name":   "My checking account",
 *           "localized_name": "My checking account",
 *           "option_value":   "service_acc_guid1",
 *           "selected":       false
 *         },
 *         {
 *           "name":           "account2",
 *           "english_name":   "My savings account",
 *           "localized_name": "My savings account",
 *           "option_value":   "service_acc_guid2",
 *           "selected":       false
 *         }
 *       ]
 *     }
 *   }
 */
@Data
@ToString(callSuper = true)
public class InteractiveData extends AbstractCallbackData {

    private String stage;

    private String html;

    @JsonProperty("interactive_fields_names")
    private List<String> interactiveFieldsNames;


}






















