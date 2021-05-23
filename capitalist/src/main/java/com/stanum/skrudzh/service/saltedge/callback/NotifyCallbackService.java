package com.stanum.skrudzh.service.saltedge.callback;

import com.stanum.skrudzh.controller.form.saltedge.callback.notify.NotifyCallback;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.model.enums.LastStageStatusEnum;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionFinder;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionService;
import com.stanum.skrudzh.service.saltedge.push.PushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotifyCallbackService {
    private static final List<LastStageStatusEnum> ALLOW_STAGES = Arrays.asList(
            LastStageStatusEnum.fetch_accounts,
            LastStageStatusEnum.fetch_recent,
            LastStageStatusEnum.fetch_full,
            LastStageStatusEnum.finish
    );

    private final ConnectionService connectionService;

    private final ConnectionFinder connectionFinder;

    private final PushService pushService;

    public void processCallback(NotifyCallback saltedgeCallback) {
        LastStageStatusEnum stage = parseStage(saltedgeCallback.getData().getStage());
        String connectionId = saltedgeCallback.getData().getConnectionId();
        ConnectionEntity connection = connectionFinder.findBySaltEdgeIdOrThrow(connectionId);

        log.info("Update connection status to [{}], SaltEdgeConnectionId={}", stage, connection.getId());
        connection.setLastStageStatus(stage);
        connectionService.save(connection);

        if (!ALLOW_STAGES.contains(stage)) {
            log.warn("Stage {} is not allowed for push events", stage);
            return;
        }

        if(stage == LastStageStatusEnum.finish) {
            log.info("Process finish stage");
            connectionService.refreshAccounts(connection);

            //TODO remove
            connectionService.refreshConnectionByStatus(connection);
        }
        pushService.sendPushByStage(stage, connection);
    }

    private LastStageStatusEnum parseStage(String saltedgeState) {
        LastStageStatusEnum stage;
        try {
            stage = LastStageStatusEnum.valueOf(saltedgeState);
        } catch (Exception e) {
            log.error("Can't define stage {}", saltedgeState);
            throw new AppException(HttpAppError.INVALID_PAYLOAD, "Can't define stage for " + saltedgeState);
        }
        return stage;
    }

}
