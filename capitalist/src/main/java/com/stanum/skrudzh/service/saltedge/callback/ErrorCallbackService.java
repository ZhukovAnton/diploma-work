package com.stanum.skrudzh.service.saltedge.callback;

import com.stanum.skrudzh.controller.form.saltedge.callback.error.ErrorCallback;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionFinder;
import com.stanum.skrudzh.service.saltedge.push.PushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErrorCallbackService {
    private static final String INVALID_CREDENTIALS_ERROR = "InvalidInteractiveCredentials";

    private final ConnectionFinder connectionFinder;

    private final PushService pushService;

    public void processCallback(ErrorCallback callback) {
        if (callback == null || callback.getData() == null ||
                !INVALID_CREDENTIALS_ERROR.equals(callback.getData().getErrorClass())) {
            log.info("Push is not allowed for error callback");
            return;
        }
        String connectionId = callback.getData().getConnectionId();
        ConnectionEntity connection = connectionFinder.findBySaltEdgeIdOrThrow(connectionId);

        pushService.sendFailPush(callback.getData().getErrorMessage(), connection);
    }
}
