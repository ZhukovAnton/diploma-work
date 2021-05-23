package com.stanum.skrudzh.service.saltedge.callback;

import com.eatthepath.pushy.apns.ApnsClient;
import com.stanum.skrudzh.controller.form.saltedge.callback.error.ErrorCallback;
import com.stanum.skrudzh.controller.form.saltedge.callback.error.ErrorData;
import com.stanum.skrudzh.controller.form.saltedge.callback.interactive.InteractiveCallback;
import com.stanum.skrudzh.controller.form.saltedge.callback.interactive.InteractiveData;
import com.stanum.skrudzh.controller.form.saltedge.callback.notify.NotifyCallback;
import com.stanum.skrudzh.controller.form.saltedge.callback.notify.NotifyData;
import com.stanum.skrudzh.controller.saltedge.AbstractSaltedgeTest;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.repository.ConnectionsRepository;
import com.stanum.skrudzh.model.enums.LastStageStatusEnum;
import com.stanum.skrudzh.service.reminder.NotificationService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class SaltedgeCallbackServiceTest extends AbstractSaltedgeTest {

    @Autowired
    private NotifyCallbackService notifyCallbackService;

    @Autowired
    private ErrorCallbackService errorCallbackService;

    @Autowired
    private InteractiveCallbackService interactiveCallbackService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ConnectionsRepository connectionsRepository;

    @Mock
    private ApnsClient apnsClient;

    @BeforeEach
    public void init() throws Exception {
        super.init();
        notificationService.setApnsClient(apnsClient);
    }

    @Test
    public void shouldThrowException_ifIncorrectStage() {
        NotifyCallback callback = new NotifyCallback();
        NotifyData data = new NotifyData();
        data.setConnectionId("123");
        data.setCustomerId("123");
        data.setStage("wrong stage");
        callback.setData(data);

        AppException appException = assertThrows(
                AppException.class,
                () -> notifyCallbackService.processCallback(callback)
        );
        Assert.assertEquals("Can't define stage for wrong stage", appException.getMessage());

    }

    @Test
    public void shouldThrowException_ifCantFindConnection() {
        NotifyCallback callback = new NotifyCallback();
        NotifyData data = new NotifyData();
        data.setConnectionId("12345");
        data.setCustomerId("123");
        data.setStage("finish");
        callback.setData(data);

        AppException appException = assertThrows(
                AppException.class,
                () -> notifyCallbackService.processCallback(callback)
        );
        Assert.assertEquals("SaltEdge connection id 12345 not found", appException.getMessage());
    }

    @Test
    public void shouldChangeConnectionStatus() {
        NotifyCallback callback = new NotifyCallback();
        NotifyData data = new NotifyData();
        data.setConnectionId(TEST_CONNECTION_ID);
        data.setCustomerId("312205856634570961");
        data.setStage("finish");
        callback.setData(data);

        notifyCallbackService.processCallback(callback);

        ConnectionEntity connectionEntity = connectionsRepository.findBySaltEdgeConnectionId(TEST_CONNECTION_ID).get();
        Assert.assertTrue(LastStageStatusEnum.finish == connectionEntity.getLastStageStatus());
    }

    @Test
    public void shouldSendPushNotification() {
        NotifyCallback callback = new NotifyCallback();
        NotifyData data = new NotifyData();
        data.setConnectionId(TEST_CONNECTION_ID);
        data.setCustomerId("312205856634570961");
        data.setStage("finish");
        callback.setData(data);

        notifyCallbackService.processCallback(callback);

        Mockito.verify(apnsClient, Mockito.times(1)).sendNotification(any());
    }

    @Test
    public void shouldSendErrorPush() {
        ErrorCallback callback = new ErrorCallback();
        ErrorData data = new ErrorData();
        data.setConnectionId(TEST_CONNECTION_ID);
        data.setCustomerId("312205856634570961");
        data.setErrorMessage("Wrong sms");
        data.setErrorClass("InvalidInteractiveCredentials");
        callback.setData(data);

        errorCallbackService.processCallback(callback);

        Mockito.verify(apnsClient, Mockito.times(1)).sendNotification(any());
    }

    @Test
    public void shouldNotSendErrorPush_ifNotInvalidCredentials() {
        ErrorCallback callback = new ErrorCallback();
        ErrorData data = new ErrorData();
        data.setConnectionId(TEST_CONNECTION_ID);
        data.setCustomerId("312205856634570961");
        data.setErrorMessage("Wrong sms");
        data.setErrorClass("Another class");
        callback.setData(data);

        errorCallbackService.processCallback(callback);

        Mockito.verify(apnsClient, Mockito.times(0)).sendNotification(any());
    }

    @Test
    public void shouldSendInteractivePush() {
        InteractiveCallback callback = new InteractiveCallback();
        InteractiveData data = new InteractiveData();
        data.setConnectionId(TEST_CONNECTION_ID);
        data.setCustomerId("312205856634570961");
        data.setInteractiveFieldsNames(Arrays.asList("sms"));
        callback.setData(data);

        interactiveCallbackService.processCallback(callback);

        Mockito.verify(apnsClient, Mockito.times(1)).sendNotification(any());
    }

}
