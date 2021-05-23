package com.stanum.skrudzh.service.expense_source;

import com.stanum.skrudzh.controller.form.ExpenseSourceCreationForm;
import com.stanum.skrudzh.controller.form.ExpenseSourceUpdatingForm;
import com.stanum.skrudzh.controller.form.saltedge.AccountConnectionAttributes;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.TransactionPurposeEnum;
import com.stanum.skrudzh.service.saltedge.account_connection.AccountConnectionManagementService;
import com.stanum.skrudzh.service.transaction.TransactionFinder;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.RowOrderUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseSourceRequestService {

    @Value("${threebaskets.admin-id}")
    private long adminId;

    private final UserUtil userUtil;

    private final RowOrderUtil rowOrderUtil;

    private final AccountConnectionManagementService accountConnectionManagementService;

    private final ExpenseSourceManagementService expenseSourceManagementService;

    private final ExpenseSourceTransactionsService expenseSourceTransactionsService;

    private final ExpenseSourceFinder expenseSourceFinder;

    private final TransactionFinder transactionFinder;

    private final TransactionRepository transactionRepository;

    public Set<ExpenseSourceEntity> getExpenseSources(Long userId, Boolean isVirtual, String currencyCode) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        return currencyCode == null
                ? expenseSourceFinder.findExpenseSourcesByUserAndIsVirtual(userEntity, isVirtual)
                : expenseSourceFinder.findExpenseSourcesByUserAndIsVirtualAndCurrency(userEntity, isVirtual, currencyCode);
    }

    public ExpenseSourceEntity getFirstExpenseSource(Long userId, boolean isVirtual, String currencyCode) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        return expenseSourceFinder.findFirstByParams(userEntity, isVirtual, currencyCode).
                orElseGet(() -> expenseSourceManagementService.createDefault(userEntity, isVirtual, currencyCode));
    }

    public ExpenseSourceEntity createExpenseSource(Long userId, ExpenseSourceCreationForm.ExpenseSourceCF form) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        ExpenseSourceEntity expenseSourceEntity =
                expenseSourceManagementService.create(userEntity, form);
        accountConnectionManagementService
                .updateOrCreateAccountConnection(expenseSourceEntity, form.getAccountConnectionAttributes());
        return expenseSourceEntity;
    }

    public ExpenseSourceEntity getExpenseSourceById(Long expenseSourceId) {
        ExpenseSourceEntity expenseSourceEntity = expenseSourceFinder.findById(expenseSourceId);
        userUtil.checkRightAccess(expenseSourceEntity.getUser().getId());
        return expenseSourceEntity;
    }

    public void updateExpenseSourceWithForm(Long id, ExpenseSourceUpdatingForm.ExpenseSourceUF form, String httpMethod) {
        ExpenseSourceEntity expenseSourceEntity = getExpenseSourceById(id);
        if (httpMethod.equals(HttpMethod.PUT.name())) {
            if (form.getRowOrderPosition() != null) {
                rowOrderUtil.updateRowOrderPosition(EntityTypeEnum.ExpenseSource, form.getRowOrderPosition(), expenseSourceEntity);
            }

            boolean hasTransactions = expenseSourceTransactionsService.hasTransactions(id, EntityTypeEnum.ExpenseSource.name());
            if (isNeedToUpdateOrCreateAccountConnection(expenseSourceEntity, form.getAccountConnectionAttributes())) {
                accountConnectionManagementService
                        .updateOrCreateAccountConnection(expenseSourceEntity, form.getAccountConnectionAttributes());
            }
            expenseSourceManagementService.update(expenseSourceEntity, form, false, hasTransactions);
        } else if (httpMethod.equals(HttpMethod.PATCH.name())) {
            if (form.getRowOrderPosition() != null) {
                rowOrderUtil.updateRowOrderPosition(EntityTypeEnum.ExpenseSource, form.getRowOrderPosition(), expenseSourceEntity);
            } else if (isNeedToUpdateOrCreateAccountConnection(expenseSourceEntity, form.getAccountConnectionAttributes())) {
                accountConnectionManagementService
                        .updateOrCreateAccountConnection(expenseSourceEntity, form.getAccountConnectionAttributes());
            } else {
                boolean hasTransactions = expenseSourceTransactionsService.hasTransactions(id, EntityTypeEnum.ExpenseSource.name());
                expenseSourceManagementService.update(expenseSourceEntity, form, true, hasTransactions);
            }
        }

    }

    public void destroyExpenseSource(Long id) {
        ExpenseSourceEntity expenseSourceEntity = getExpenseSourceById(id);
        expenseSourceManagementService.destroy(expenseSourceEntity, false, false);
    }

    private boolean isNeedToUpdateOrCreateAccountConnection(ExpenseSourceEntity expenseSourceEntity,
                                                           AccountConnectionAttributes attributes) {
        return attributes != null && (isNeedToUpdateAccount(expenseSourceEntity, attributes)
                || isNeedToUpdateConnection(expenseSourceEntity, attributes));
    }

    private boolean isNeedToUpdateAccount(ExpenseSourceEntity expenseSourceEntity, AccountConnectionAttributes attributes) {
        AccountConnectionEntity accountConnection = expenseSourceEntity.getAccountConnectionEntity();
        return (Boolean.TRUE.equals(attributes.getDestroy())) || (attributes.getAccountId() != null
                && (accountConnection == null
                || accountConnection.getAccountEntity() == null
                || !accountConnection.getAccountEntity().getId().equals(attributes.getAccountId())
                || accountConnection.getAccountEntity().getId().equals(attributes.getAccountId()) && Boolean.TRUE.equals(attributes.getDestroy())));
    }

    private boolean isNeedToUpdateConnection(ExpenseSourceEntity expenseSourceEntity, AccountConnectionAttributes attributes) {
        AccountConnectionEntity accountConnection = expenseSourceEntity.getAccountConnectionEntity();
        return attributes.getConnectionId() != null
                && (accountConnection == null
                || accountConnection.getConnectionEntity() == null
                || !accountConnection.getConnectionEntity().getId().equals(attributes.getConnectionId()));
    }

    public void syncBalances(Long id) {
        if (!RequestUtil.getUser().getId().equals(adminId)) return;
        ExpenseSourceEntity expenseSourceEntity = expenseSourceFinder.findById(id);
        expenseSourceManagementService.syncBalances(expenseSourceEntity);
    }

    public long getAmountOfNotActualBalances() {
        if (!RequestUtil.getUser().getId().equals(adminId)) throw new AppException(HttpAppError.ACCESS_DENIED);
        Set<ExpenseSourceEntity> allExpenseSources = expenseSourceFinder.findAllActual();
        AtomicLong amountOfAllBroken = new AtomicLong(0L);
        Map<UserEntity, List<String>> logPerUser = new HashMap<>();
        allExpenseSources.forEach(expenseSourceEntity -> {
            if (expenseSourceEntity.getUser() == null) return;
            BigDecimal balanceFromTransactions = BigDecimal.ZERO;
            Set<TransactionEntity> allTransactions =
                    expenseSourceTransactionsService.findAllTransactions(expenseSourceEntity);
            for (TransactionEntity transactionEntity : allTransactions) {
                if (isSourceTransaction(transactionEntity, expenseSourceEntity)) {
                    balanceFromTransactions = balanceFromTransactions.add(transactionEntity.getAmountCents().negate());
                } else {
                    balanceFromTransactions = balanceFromTransactions.add(transactionEntity.getConvertedAmountCents());
                }
            }
            if (!balanceFromTransactions.equals(expenseSourceEntity.getAmountCents())) {
                amountOfAllBroken.getAndIncrement();
                String logMessage = "ExpenseSource id: " + expenseSourceEntity.getId()
                        + " name: " + expenseSourceEntity.getName()
                        + ", Balances (expenseSource/transactions): " + expenseSourceEntity.getAmountCents() + "/" + balanceFromTransactions;
                if (logPerUser.containsKey(expenseSourceEntity.getUser())) {
                    List<String> alreadyAddLogs = logPerUser.get(expenseSourceEntity.getUser());
                    alreadyAddLogs.add(logMessage);
                } else {
                    List<String> logs = new ArrayList<>();
                    logs.add(logMessage);
                    logPerUser.put(expenseSourceEntity.getUser(), logs);
                }
            }
        });
        return amountOfAllBroken.get();
    }

    public void actualiseExpenseSourceCreationPurposes() {
        if (!RequestUtil.getUser().getId().equals(adminId)) throw new AppException(HttpAppError.ACCESS_DENIED);
        Set<TransactionEntity> creationTransactions = transactionFinder.findAllExpenseSourceCreationTransactions();
        creationTransactions.forEach(transactionEntity -> {
            transactionEntity.setTransactionPurpose(TransactionPurposeEnum.creation);
            transactionRepository.save(transactionEntity);
        });
    }

    private boolean isSourceTransaction(TransactionEntity transactionEntity, ExpenseSourceEntity expenseSourceEntity) {
        return transactionEntity.getSourceType().equals(EntityTypeEnum.ExpenseSource.name())
                && expenseSourceEntity.getId().equals(transactionEntity.getSourceId());
    }
}
