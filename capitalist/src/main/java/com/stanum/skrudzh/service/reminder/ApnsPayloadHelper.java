package com.stanum.skrudzh.service.reminder;

import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;

import java.util.Map;

public class ApnsPayloadHelper {

    public static String buildPush(PushEvent pushEvent, Map<String, Object> customProps, String... alertArguments) {
        ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
        payloadBuilder.setLocalizedAlertMessage(pushEvent.getKey(), alertArguments);
        payloadBuilder.setCategoryName(pushEvent.getCategory());
        if(pushEvent.getCategory() != null && !pushEvent.getCategory().isEmpty()) {
            payloadBuilder.setThreadId(pushEvent.getThreadId());
        }
        if (customProps != null && !customProps.isEmpty()) {
            for (Map.Entry<String, Object> entry : customProps.entrySet()) {
                payloadBuilder.addCustomProperty(entry.getKey(), entry.getValue());
            }
        }
        payloadBuilder.setBadgeNumber(1);
        payloadBuilder.setSound("default");
        return payloadBuilder.buildWithDefaultMaximumLength();
    }

    public static String buildPush(PushEvent pushEvent, String... alertArguments) {
        return buildPush(pushEvent, null, alertArguments);
    }
}