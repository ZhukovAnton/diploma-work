package com.stanum.skrudzh.service.saltedge.push.impl;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.model.enums.LastStageStatusEnum;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import com.stanum.skrudzh.service.reminder.ApnsPayloadHelper;
import com.stanum.skrudzh.service.reminder.NotificationService;
import com.stanum.skrudzh.service.reminder.PushEvent;
import com.stanum.skrudzh.service.saltedge.account.AccountFinder;
import com.stanum.skrudzh.service.saltedge.account_connection.AccountConnectionFinder;
import com.stanum.skrudzh.service.saltedge.push.PushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PushServiceImpl implements PushService {

    private final AccountFinder accountFinder;

    private final AccountConnectionFinder accountConnectionFinder;

    private final ExpenseSourceFinder expenseSourceFinder;

    private final NotificationService notificationService;

    public void sendPushByStage(LastStageStatusEnum stage, ConnectionEntity connection) {
        log.info("Send push stage={}, connectionId={}", stage, connection.getId());
        PushEvent event = convertStage(stage);

        String notificationPayload = ApnsPayloadHelper.buildPush(
                event,
                getExpenseSourcesProps(connection),
                connection.getProviderName());
        notificationService.send(connection.getUser().getDeviceToken(), notificationPayload);
    }

    public void sendFailPush(String message, ConnectionEntity connection) {
        log.info("Send fail push message={}, connectionId={}", message, connection.getId());
        String notificationPayload = ApnsPayloadHelper.buildPush(
                PushEvent.SALTEDGE_FAILURE,
                getExpenseSourcesProps(connection),
                connection.getProviderName(),
                message);
        notificationService.send(connection.getUser().getDeviceToken(), notificationPayload);
    }

    private Map<String, Object> getExpenseSourcesProps(ConnectionEntity connection) {
        Set<ExpenseSourceEntity> expenseSources = getExpenseSources(connection);
        List<Long> expenseSourceIds = new ArrayList<>();
        List<String> expenseSourceNames = new ArrayList<>();

        for (ExpenseSourceEntity expenseSource : expenseSources) {
            expenseSourceIds.add(expenseSource.getId());
            expenseSourceNames.add(expenseSource.getName());
        }
        Map<String, Object> customProperties = new LinkedHashMap<>();
        customProperties.put("expense_source_ids", expenseSourceIds);
        customProperties.put("expense_source_names", expenseSourceNames);
        return customProperties;
    }

    private PushEvent convertStage(LastStageStatusEnum stage) {
        switch (stage) {
            case interactive:
                return PushEvent.SALTEDGE_INTERACTIVE;
            case fetch_accounts:
                return PushEvent.SALTEDGE_FETCH_ACCOUNTS;
            case fetch_recent:
                return PushEvent.SALTEDGE_FETCH_RECENT;
            case fetch_full:
                return PushEvent.SALTEDGE_FETCH_FULL;
            case finish:
                return PushEvent.SALTEDGE_FINISH;
            default: {
                log.error("Can't find push event for stage {}", stage);
                throw new AppException(HttpAppError.NOT_FOUND, "Can't find push event for stage " + stage);
            }
        }
    }

    private Set<ExpenseSourceEntity> getExpenseSources(ConnectionEntity connection) {
        Set<ExpenseSourceEntity> expenseSources = new HashSet<>();
        Set<AccountEntity> accounts = accountFinder.findAccountsByConnection(connection);

        Set<AccountConnectionEntity> accountConnectionEntities = new HashSet<>();
        for (AccountEntity accountEntity : accounts) {
            accountConnectionFinder
                    .findByAccount(accountEntity)
                    .ifPresent(accountConnectionEntities::add);
        }

        for (AccountConnectionEntity accountConnectionEntity : accountConnectionEntities) {
            expenseSources.addAll(expenseSourceFinder.findByAccountConnection(accountConnectionEntity));
        }
        return expenseSources;
    }
}
