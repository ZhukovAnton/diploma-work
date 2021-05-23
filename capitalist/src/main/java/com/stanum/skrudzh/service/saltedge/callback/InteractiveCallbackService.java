package com.stanum.skrudzh.service.saltedge.callback;

import com.stanum.skrudzh.controller.form.saltedge.callback.interactive.InteractiveCallback;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.model.enums.LastStageStatusEnum;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionFinder;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionService;
import com.stanum.skrudzh.service.saltedge.push.PushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InteractiveCallbackService {

    private final ConnectionFinder connectionFinder;

    private final ConnectionService connectionService;

    private final PushService pushService;

    public void processCallback(InteractiveCallback callback) {
        if(callback == null || callback.getData() == null) {
            return;
        }
        String connectionId = callback.getData().getConnectionId();
        ConnectionEntity connection = connectionFinder.findBySaltEdgeIdOrThrow(connectionId);

        List<String> interactiveFieldsNames = callback.getData().getInteractiveFieldsNames();
        connection.setRequiredInteractiveFieldsNames(StringUtils.collectionToCommaDelimitedString(interactiveFieldsNames));
        connectionService.save(connection);

        pushService.sendPushByStage(LastStageStatusEnum.interactive, connection);
    }
}
