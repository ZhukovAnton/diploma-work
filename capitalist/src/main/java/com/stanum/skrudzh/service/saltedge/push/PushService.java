package com.stanum.skrudzh.service.saltedge.push;

import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.model.enums.LastStageStatusEnum;

public interface PushService {

    void sendPushByStage(LastStageStatusEnum stage, ConnectionEntity connection);

    void sendFailPush(String message, ConnectionEntity connection);
}
