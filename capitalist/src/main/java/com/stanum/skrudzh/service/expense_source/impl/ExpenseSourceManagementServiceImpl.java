package com.stanum.skrudzh.service.expense_source.impl;

import com.stanum.skrudzh.controller.form.ExpenseSourceCreationForm;
import com.stanum.skrudzh.controller.form.ExpenseSourceUpdatingForm;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.TransactionableExampleEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.ExpenseSourcesRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceManagementService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceTransactionsService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceValidationService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.RowOrderUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseSourceManagementServiceImpl implements ExpenseSourceManagementService {

    private final RowOrderUtil rowOrderUtil;

    private final ExpenseSourcesRepository expenseSourcesRepository;

    private final ExpenseSourceTransactionsService transactionsService;

    public ExpenseSourceEntity create(UserEntity userEntity,
                                      ExpenseSourceCreationForm.ExpenseSourceCF form) {
        ExpenseSourceEntity expenseSourceEntity = new ExpenseSourceEntity();
        expenseSourceEntity.setUser(userEntity);
        checkAndFillExpenseSourceWithCreationForm(form, expenseSourceEntity);
        save(expenseSourceEntity);
        return expenseSourceEntity;
    }

    public ExpenseSourceEntity createDefault(UserEntity userEntity, boolean isVirtual, String currencyCode) {
        return createDefault(null, userEntity, isVirtual, currencyCode, getDefaultExpenseSourceName());
    }

    @Override
    public ExpenseSourceEntity createDefault(TransactionableExampleEntity template,
                                             UserEntity userEntity,
                                             boolean isVirtual, String currencyCode, String name) {
        ExpenseSourceEntity expenseSourceEntity = new ExpenseSourceEntity();
        expenseSourceEntity.setUser(userEntity);
        expenseSourceEntity.setCurrency(currencyCode);
        expenseSourceEntity.setIsVirtual(isVirtual);
        expenseSourceEntity.setIsDebt(false);
        expenseSourceEntity.setName(name);
        expenseSourceEntity.setIconUrl(null);
        expenseSourceEntity.setAmountCents(BigDecimal.ZERO);
        expenseSourceEntity.setCreditLimitCents(BigDecimal.ZERO);
        rowOrderUtil.setLastPosition(EntityTypeEnum.ExpenseSource, expenseSourceEntity);
        setCreationTimestamps(expenseSourceEntity);
        if(template != null) {
            expenseSourceEntity.setPrototypeKey(template.getPrototypeKey());
        }

        save(expenseSourceEntity);
        return expenseSourceEntity;
    }

    public void update(ExpenseSourceEntity expenseSourceEntity, ExpenseSourceUpdatingForm.ExpenseSourceUF form,
                       boolean isNullAllowed, boolean hasTransactions) {
        checkAndUpdateExpenseSourceWithForm(expenseSourceEntity, form, isNullAllowed, hasTransactions);
        save(expenseSourceEntity);
    }

    public void destroy(ExpenseSourceEntity expenseSourceEntity, boolean destroyAccountConnections, boolean destroyTransactions) {
        if (destroyTransactions) {
            destroyTransactions(expenseSourceEntity);
        }
        expenseSourceEntity.setDeletedAt(TimeUtil.now());
        save(expenseSourceEntity);
    }

    public void syncBalances(ExpenseSourceEntity expenseSourceEntity) {
        BigDecimal balanceFromTransactions = transactionsService.getBalanceFromTransactions(expenseSourceEntity);
        expenseSourceEntity.setAmountCents(balanceFromTransactions);
        save(expenseSourceEntity);
    }

    private ExpenseSourceEntity save(ExpenseSourceEntity expenseSourceEntity) {
        return expenseSourcesRepository.save(expenseSourceEntity);
    }

    private void checkAndFillExpenseSourceWithCreationForm(ExpenseSourceCreationForm.ExpenseSourceCF form, ExpenseSourceEntity expenseSourceEntity) {
        ExpenseSourceValidationService.validateCreationForm(form);
        if (form.getAmountCents() == null || form.getAmountCents().compareTo(0L) > 0) {
            expenseSourceEntity.setAmountCents(BigDecimal.ZERO);
        }
        if (form.getAmountCents() != null && form.getAmountCents().compareTo(0L) <= 0) {
            expenseSourceEntity.setAmountCents(BigDecimal.valueOf(form.getAmountCents()));
        }
        if (form.getMaxFetchInterval() != null) {
            expenseSourceEntity.setMaxFetchInterval(form.getMaxFetchInterval());
        }
        expenseSourceEntity.setCreditLimitCents(form.getCreditLimitCents() != null
                ? BigDecimal.valueOf(form.getCreditLimitCents())
                : BigDecimal.ZERO);
        expenseSourceEntity.setCurrency(form.getCurrency());
        expenseSourceEntity.setIconUrl(form.getIconUrl());
        expenseSourceEntity.setName(form.getName());
        expenseSourceEntity.setCardType(form.getCardType());
        expenseSourceEntity.setPrototypeKey(form.getPrototypeKey());
        if (form.getRowOrderPosition() != null) {
            rowOrderUtil.setRowOrderPosition(EntityTypeEnum.ExpenseSource, form.getRowOrderPosition(), expenseSourceEntity);
        } else {
            rowOrderUtil.setLastPosition(EntityTypeEnum.ExpenseSource, expenseSourceEntity);
        }
        expenseSourceEntity.setIsVirtual(false);
        expenseSourceEntity.setIsDebt(false);
        setCreationTimestamps(expenseSourceEntity);
    }

    private void checkAndUpdateExpenseSourceWithForm(ExpenseSourceEntity expenseSourceEntity,
                                                     ExpenseSourceUpdatingForm.ExpenseSourceUF form,
                                                     boolean patchMethod,
                                                     boolean hasTransactions) {
        ExpenseSourceValidationService.validateUpdatingForm(form, patchMethod);
        boolean isIconChanged = false;
        boolean isNameChanged = false;
        //TODO refacor
        if(patchMethod) {
            if(form.getCurrency() != null && !hasTransactions) {
                expenseSourceEntity.setCurrency(form.getCurrency());
            }
            if (form.getIconUrl() != null && !form.getIconUrl().equals(expenseSourceEntity.getIconUrl())
                    && !form.getIconUrl().isBlank()) {
                expenseSourceEntity.setIconUrl(form.getIconUrl());
                isIconChanged = true;
            }
            if (form.getCardType() != null) {
                expenseSourceEntity.setCardType(form.getCardType());
            }
            if (form.getCreditLimitCents() != null) {
                expenseSourceEntity.setCreditLimitCents(BigDecimal.valueOf(form.getCreditLimitCents()));
            }
            if (form.getName() != null && !form.getName().isBlank() && !form.getName().equals(expenseSourceEntity.getName())) {
                expenseSourceEntity.setName(form.getName());
                isNameChanged = true;
            }
            if (form.getMaxFetchInterval() != null) {
                expenseSourceEntity.setMaxFetchInterval(form.getMaxFetchInterval());
            }
            if(form.getPrototypeKey() != null) {
                expenseSourceEntity.setPrototypeKey(form.getPrototypeKey());
            }
        } else {
            if(form.getCurrency() != null) {
                expenseSourceEntity.setCurrency(form.getCurrency());
            }
            if (form.getIconUrl() != null && !form.getIconUrl().equals(expenseSourceEntity.getIconUrl())
                    && !form.getIconUrl().isBlank()) {
                expenseSourceEntity.setIconUrl(form.getIconUrl());
                isIconChanged = true;
            }
            if (form.getCardType() == null || form.getCardType() != null && !form.getCardType().equals(expenseSourceEntity.getCardType())) {
                expenseSourceEntity.setCardType(form.getCardType());
            }
            if (form.getCreditLimitCents() != null) {
                expenseSourceEntity.setCreditLimitCents(BigDecimal.valueOf(form.getCreditLimitCents()));
            } else {
                expenseSourceEntity.setCreditLimitCents(BigDecimal.ZERO);
            }
            if (form.getName() != null && !form.getName().isBlank() && !form.getName().equals(expenseSourceEntity.getName())) {
                expenseSourceEntity.setName(form.getName());
                isNameChanged = true;
            }
            if (form.getMaxFetchInterval() != null) {
                expenseSourceEntity.setMaxFetchInterval(form.getMaxFetchInterval());
            }
            expenseSourceEntity.setPrototypeKey(form.getPrototypeKey());
        }

        updateTransactions(expenseSourceEntity, isIconChanged || isNameChanged);
        expenseSourceEntity.setUpdatedAt(TimeUtil.now());

        if (form.getAmountCents() != null) {
            BigDecimal amountCentsDifference = expenseSourceEntity.getAmountCents().negate()
                    .add(BigDecimal.valueOf(form.getAmountCents()));
            if (amountCentsDifference.compareTo(BigDecimal.ZERO) != 0) {
                transactionsService.createAmountChangeTransaction(expenseSourceEntity, amountCentsDifference);
            }
        }
    }

    private void updateTransactions(ExpenseSourceEntity expenseSourceEntity, boolean updateTransactions) {
        if (!updateTransactions) return;
        Set<TransactionEntity> allExpenseSourceTransactions = transactionsService.findAllTransactions(expenseSourceEntity);
        allExpenseSourceTransactions.forEach(expenseSourceTransaction -> {
            if (expenseSourceTransaction.getSourceType().equals(EntityTypeEnum.ExpenseSource.name())
                    && expenseSourceTransaction.getSourceId().equals(expenseSourceEntity.getId())) {
                transactionsService
                        .updateSourceTransactionWithNameAndIcon(expenseSourceTransaction, expenseSourceEntity.getName(), expenseSourceEntity.getIconUrl());
            } else {
                transactionsService
                        .updateDestinationTransactionWithNameAndIcon(expenseSourceTransaction, expenseSourceEntity.getName(), expenseSourceEntity.getIconUrl());
            }
        });
    }

    private void destroyTransactions(ExpenseSourceEntity expenseSourceEntity) {
        Set<TransactionEntity> transactionsToDestroy = transactionsService.findAllTransactions(expenseSourceEntity);
        Timestamp now = TimeUtil.now();
        transactionsToDestroy.forEach(transactionEntity -> {
            transactionEntity.setDeletedAt(now);
            transactionsService.afterDestroy(transactionEntity);
            transactionsService.save(transactionEntity);
        });
    }

    private void setCreationTimestamps(ExpenseSourceEntity expenseSourceEntity) {
        Timestamp now = TimeUtil.now();
        expenseSourceEntity.setCreatedAt(now);
        expenseSourceEntity.setUpdatedAt(now);
    }

    private String getDefaultExpenseSourceName() {
        return ResourceBundle.getBundle("messages", RequestUtil.getLocale())
                .getString("activerecord.defaults.models.expense_source.attributes.name.usual");
    }

}
