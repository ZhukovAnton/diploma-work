package com.stanum.skrudzh.service.reminder;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.google.common.io.CharSource;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.model.enums.RemindableTypeEnum;
import com.stanum.skrudzh.service.active.ActiveFinder;
import com.stanum.skrudzh.service.active.ActiveManagementService;
import com.stanum.skrudzh.service.borrow.BorrowFinder;
import com.stanum.skrudzh.service.borrow.BorrowManagementService;
import com.stanum.skrudzh.service.credit.CreditFinder;
import com.stanum.skrudzh.service.credit.CreditManagementService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryManagementService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceManagementService;
import com.stanum.skrudzh.utils.constant.Constants;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.ReaderInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableAsync
@EnableScheduling
@Slf4j
public class NotificationService {
    @Value("${threebaskets.apns.bundle-id}")
    private String APNS_BUNDLE_ID;

    @Value("${threebaskets.apns.key}")
    private String APNS_KEY;

    @Value("${threebaskets.apns.key-id}")
    private String APNS_KEY_ID;

    @Value("${threebaskets.apns.team-id}")
    private String APNS_TEAM_ID;

    @Value("${threebaskets.apns.env}")
    private String APNS_ENV;

    @Value("${threebaskets.apns.enabled}")
    private boolean apnsEnabled;

    private final IncomeSourceFinder incomeSourceFinder;

    private final IncomeSourceManagementService incomeSourceManagementService;

    private final ExpenseCategoryFinder expenseCategoryFinder;

    private final ExpenseCategoryManagementService expenseCategoryManagementService;

    private final CreditFinder creditFinder;

    private final CreditManagementService creditManagementService;

    private final BorrowFinder borrowFinder;

    private final BorrowManagementService borrowManagementService;

    private final ActiveFinder activeFinder;

    private final ActiveManagementService activeManagementService;

    private final ReminderService reminderService;

    private ApnsClient apnsClient;

    @PostConstruct
    public void init() {
        if(!apnsEnabled) {
            log.info("Apns client disabled");
            return;
        }
        if (Constants.ENVIRONMENT_PROD.equals(APNS_ENV)) {
            initClient(ApnsClientBuilder.PRODUCTION_APNS_HOST);
        } else {
            initClient(ApnsClientBuilder.DEVELOPMENT_APNS_HOST);
        }
    }

    public void initClient(String host) {
        log.info("Set apns client host {}", host);
        try {
            ApnsClientBuilder apnsClientBuilder = new ApnsClientBuilder()
                    .setSigningKey(ApnsSigningKey.loadFromInputStream(
                            new ReaderInputStream(CharSource.wrap(APNS_KEY).openStream()),
                            APNS_TEAM_ID,
                            APNS_KEY_ID
                    ));
            apnsClientBuilder.setApnsServer(host);
            apnsClient = apnsClientBuilder.build();
        } catch (Exception e) {
            log.error("Error while init ApnsClient", e);
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void sendNotifications() {
        Timestamp beginningOfMinute = Timestamp.valueOf(TimeUtil.beginningOfMinute());
        List<ReminderEntity> remindersForSending = reminderService.findRemindersByStartDate(beginningOfMinute);
        if (remindersForSending.size() > 0) {
            remindersForSending.forEach(reminderEntity -> {
                Object source = getReminderSource(reminderEntity);
                if (source == null) return;
                if (reminderService.isShouldRemind(reminderEntity, source)) {
                    sendNotification(reminderEntity, source);
                    if (reminderEntity.getRecurrenceRule() != null && !reminderEntity.getRecurrenceRule().isBlank()) {
                        reminderService.updateStartDateByRecurrenceRule(reminderEntity);
                    }
                }
            });
        }
    }

    private void sendNotification(ReminderEntity reminderEntity, Object source) {
        UserEntity user = getReminderUser(source);
        if (user.getDeviceToken() == null || user.getDeviceToken().isBlank()) {
            return;
        }

        ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
        payloadBuilder.setLocalizedAlertMessage(notificationLocKey(source), notificationLocArgs(source));
        if (reminderEntity.getRemindableType() != null) {
            payloadBuilder.setCategoryName(reminderEntity.getRemindableType());
            payloadBuilder.setThreadId(reminderEntity.getRemindableType());
            if (reminderEntity.getRemindableId() != null) {
                payloadBuilder.addCustomProperty("remindable_id", reminderEntity.getRemindableId());
                payloadBuilder.addCustomProperty("remindable_type", reminderEntity.getRemindableType());
            }
        }
        payloadBuilder.setBadgeNumber(1);
        payloadBuilder.setSound("default");
        String notificationPayload = payloadBuilder.buildWithDefaultMaximumLength();
        send(user.getDeviceToken(), notificationPayload);
    }

    public void send(String deviceToken, String notificationPayload) {
        if(deviceToken == null) {
            log.warn("Device token = null, payload: {}", notificationPayload);
            return;
        }
        log.info("Send push notification: deviceToken = {}, payload = {}", deviceToken, notificationPayload);
        SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(
                deviceToken,
                APNS_BUNDLE_ID,
                notificationPayload);

        apnsClient.sendNotification(pushNotification);
    }

    private String notificationLocKey(Object source) {
        if (source instanceof IncomeSourceEntity) {
            return incomeSourceManagementService.notificationLocKey();
        } else if (source instanceof ExpenseCategoryEntity) {
            return expenseCategoryManagementService.notificationLocKey();
        } else if (source instanceof ActiveEntity) {
            return activeManagementService.notificationLocKey();
        } else if (source instanceof CreditEntity) {
            return creditManagementService.notificationLocKey();
        } else if (source instanceof BorrowEntity) {
            return borrowManagementService.notificationLocKey((BorrowEntity) source);
        } else {
            return "DEFAULT_REMINDER_MESSAGE_KEY";
        }
    }

    private String[] notificationLocArgs(Object source) {
        if (source instanceof IncomeSourceEntity) {
            return incomeSourceManagementService.notificationLocArgs((IncomeSourceEntity) source);
        } else if (source instanceof ExpenseCategoryEntity) {
            return expenseCategoryManagementService.notificationLocArgs((ExpenseCategoryEntity) source);
        } else if (source instanceof ActiveEntity) {
            return activeManagementService.notificationLocArgs((ActiveEntity) source);
        } else if (source instanceof CreditEntity) {
            return creditManagementService.notificationLocArgs((CreditEntity) source);
        } else if (source instanceof BorrowEntity) {
            return borrowManagementService.notificationLocArgs((BorrowEntity) source);
        } else {
            return new String[]{};
        }
    }

    private Object getReminderSource(ReminderEntity reminderEntity) {
        try {
            if (reminderEntity.getRemindableType().equals(RemindableTypeEnum.IncomeSource.name())) {
                return incomeSourceFinder.findById(reminderEntity.getRemindableId());
            } else if (reminderEntity.getRemindableType().equals(RemindableTypeEnum.ExpenseCategory.name())) {
                return expenseCategoryFinder.findById(reminderEntity.getRemindableId());
            } else if (reminderEntity.getRemindableType().equals(RemindableTypeEnum.Active.name())) {
                return activeFinder.findById(reminderEntity.getRemindableId());
            } else if (reminderEntity.getRemindableType().equals(RemindableTypeEnum.Credit.name())) {
                return creditFinder.findById(reminderEntity.getRemindableId());
            } else {
                return borrowFinder.findById(reminderEntity.getRemindableId());
            }
        } catch (AppException e) {
            log.warn(e.getMessage() + ". Source may be was deleted.");
            return null;
        }
    }

    private UserEntity getReminderUser(Object source) {
        if (source instanceof IncomeSourceEntity) {
            return ((IncomeSourceEntity) source).getUser();
        } else if (source instanceof ExpenseCategoryEntity) {
            return ((ExpenseCategoryEntity) source).getBasket().getUser();
        } else if (source instanceof ActiveEntity) {
            return ((ActiveEntity) source).getBasketEntity().getUser();
        } else if (source instanceof CreditEntity) {
            return ((CreditEntity) source).getUser();
        } else {
            return ((BorrowEntity) source).getUser();
        }
    }

    public void setApnsClient(ApnsClient apnsClient) {
        this.apnsClient = apnsClient;
    }
}
