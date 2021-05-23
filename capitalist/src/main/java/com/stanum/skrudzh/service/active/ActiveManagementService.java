package com.stanum.skrudzh.service.active;

import com.stanum.skrudzh.controller.form.ActiveCreationForm;
import com.stanum.skrudzh.controller.form.ActiveUpdatingForm;
import com.stanum.skrudzh.controller.form.attributes.ActiveTransactionAttributes;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.ActiveRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.PlannedIncomeTypeEnum;
import com.stanum.skrudzh.model.enums.RemindableTypeEnum;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceManagementService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceManagementService;
import com.stanum.skrudzh.service.order.OrderService;
import com.stanum.skrudzh.service.reminder.ReminderService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.RowOrderUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import com.stanum.skrudzh.model.enums.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ActiveManagementService {

    private final ActiveRepository activeRepository;

    private final RowOrderUtil rowOrderUtil;

    private final ReminderService reminderService;

    private final ActiveTypesService activeTypesService;

    private final IncomeSourceManagementService incomeSourceManagementService;

    private final IncomeSourceFinder incomeSourceFinder;

    private final ExpenseSourceManagementService expenseSourceManagementService;

    private final ActiveTransactionsService transactionService;

    private final ActiveValidationService validationService;

    private final ActiveCalculationService calculationService;

    private final ExpenseSourceFinder expenseSourceFinder;

    private final OrderService orderService;

    public ActiveEntity createActiveEntityWithCreationForm(UserEntity userEntity, BasketEntity basketEntity, ActiveCreationForm.ActiveCF form) {
        ActiveEntity activeEntity = new ActiveEntity();
        activeEntity.setBasketEntity(basketEntity);
        activeEntity.setUser(userEntity);
        checkAndFillWithCreationForm(activeEntity, form);
        save(activeEntity);
        return activeEntity;
    }

    public void updateActiveWithUpdatingForm(ActiveEntity activeEntity, ActiveUpdatingForm.ActiveUF form) {
        if (form.getRowOrderPosition() != null) {
            if(!RequestUtil.hasGlobalSorting()) {
                rowOrderUtil.updateRowOrderPosition(EntityTypeEnum.Active, form.getRowOrderPosition(), activeEntity);
            } else if(RequestUtil.hasGlobalSorting()) {
                orderService.updateOrder(RequestUtil.getUser(),
                        OrderType.ACTIVE_BORROW,
                        EntityTypeEnum.Active,
                        activeEntity.getId(),
                        form.getRowOrderPosition()
                        );
            }
        } else {
            checkAndUpdateActiveEntityWithUpdatingForm(activeEntity, form);
        }
        save(activeEntity);
    }

    public void destroyActive(ActiveEntity activeEntity, boolean destroyWithTransactions) {
        if (destroyWithTransactions) {
            destroyTransactions(activeEntity);
        }
        activeEntity.setDeletedAt(TimeUtil.now());
        incomeSourceManagementService
                .destroyActiveIncomeSource(activeEntity, destroyWithTransactions);
        save(activeEntity);
    }

    //TODO: move into property
    public String notificationLocKey() {
        return "ACTIVE_NOTIFICATION_MESSAGE_KEY";
    }

    public String[] notificationLocArgs(ActiveEntity activeEntity) {
        ReminderEntity reminder = reminderService.findBySourceIdAndType(activeEntity.getId(), RemindableTypeEnum.Active);
        if (reminder != null) {
            return new String[]{activeEntity.getName(), reminder.getMessage() != null ? reminder.getMessage() : ""};
        } else {
            return new String[]{activeEntity.getName(), ""};
        }
    }

    public boolean isActiveOpen(ActiveEntity activeEntity) {
        return activeEntity.getCostCents().compareTo(BigDecimal.ZERO) > 0
                || (activeEntity.getCostCents().compareTo(BigDecimal.ZERO) <= 0
                && transactionService.getSaleTransactions(activeEntity).isEmpty());
    }

    public void save(ActiveEntity activeEntity) {
        activeRepository.save(activeEntity);
    }

    private void checkAndFillWithCreationForm(ActiveEntity activeEntity, ActiveCreationForm.ActiveCF form) {
        validationService.validateCreationForm(form);
        if (form.getActiveTypeId() != null) {
            activeEntity.setActiveTypeEntity(activeTypesService.getActiveTypeById(form.getActiveTypeId()));
        }
        if (form.getCostCents() == null || form.getCostCents().compareTo(0L) > 0) {
            activeEntity.setCostCents(BigDecimal.ZERO);
        }
        if (form.getCostCents() != null && form.getCostCents().compareTo(0L) <= 0) {
            activeEntity.setCostCents(BigDecimal.valueOf(form.getCostCents()));
        }
        activeEntity.setAlreadyPaidCents(form.getAlreadyPaidCents() != null
                ? BigDecimal.valueOf(form.getAlreadyPaidCents())
                : BigDecimal.ZERO);
        activeEntity.setGoalAmountCents(form.getGoalAmountCents() != null
                ? BigDecimal.valueOf(form.getGoalAmountCents())
                : null);
        activeEntity.setMonthlyPaymentCents(form.getMonthlyPaymentCents() != null
                ? BigDecimal.valueOf(form.getMonthlyPaymentCents())
                : null);
        activeEntity.setMonthlyPlannedIncomeCents(form.getMonthlyPlannedIncomeCents() != null
                ? BigDecimal.valueOf(form.getMonthlyPlannedIncomeCents())
                : null);
        activeEntity.setCurrency(form.getCurrency() != null
                ? form.getCurrency()
                : activeEntity.getBasketEntity().getUser().getDefaultCurrency());
        activeEntity.setIsIncomePlanned(form.getIsIncomePlanned() != null
                ? form.getIsIncomePlanned()
                : false);
        activeEntity.setPlannedIncomeType(form.getPlannedIncomeType() != null
                ? form.getPlannedIncomeType()
                : PlannedIncomeTypeEnum.monthly_income);
        activeEntity.setName(form.getName());
        activeEntity.setIconUrl(form.getIconUrl());
        activeEntity.setAnnualIncomePercent(form.getAnnualIncomePercent());
        if(!RequestUtil.hasGlobalSorting()) {
            if (form.getRowOrderPosition() != null) {
                rowOrderUtil.setRowOrderPosition(EntityTypeEnum.Active, form.getRowOrderPosition(), activeEntity);
            } else {
                rowOrderUtil.setLastPosition(EntityTypeEnum.Active, activeEntity);
            }
        }
        setCreationTimestamps(activeEntity);
    }

    private void checkAndUpdateActiveEntityWithUpdatingForm(ActiveEntity activeEntity, ActiveUpdatingForm.ActiveUF form) {
        validationService.validateUpdatingForm(form);
        boolean isNameChanged = false;
        boolean isIconUrlChanged = false;
        if (form.getAnnualIncomePercent() != null) activeEntity.setAnnualIncomePercent(form.getAnnualIncomePercent());
        if (form.getCurrency() != null) activeEntity.setCurrency(form.getCurrency());
        if (form.getGoalAmountCents() != null)
            activeEntity.setGoalAmountCents(BigDecimal.valueOf(form.getGoalAmountCents()));
        if (form.getIsIncomePlanned() != null) activeEntity.setIsIncomePlanned(form.getIsIncomePlanned());
        if (form.getMonthlyPaymentCents() != null)
            activeEntity.setMonthlyPaymentCents(BigDecimal.valueOf(form.getMonthlyPaymentCents()));
        else
            activeEntity.setMonthlyPaymentCents(null);
        if (form.getMonthlyPlannedIncomeCents() != null)
            activeEntity.setMonthlyPlannedIncomeCents(BigDecimal.valueOf(form.getMonthlyPlannedIncomeCents()));
        if (form.getPlannedIncomeType() != null) activeEntity.setPlannedIncomeType(form.getPlannedIncomeType());
        if (form.getIconUrl() != null && !form.getIconUrl().equals(activeEntity.getIconUrl())) {
            activeEntity.setIconUrl(form.getIconUrl());
            isIconUrlChanged = true;
        }
        if (form.getMaxFetchInterval() != null) activeEntity.setMaxFetchInterval(form.getMaxFetchInterval());
        if (form.getReminderAttributes() != null) {
            if (form.getReminderAttributes().getId() != null) {
                reminderService.saveUpdatedEntity(form.getReminderAttributes(), activeEntity);
            } else {
                reminderService.saveCreatedEntity(form.getReminderAttributes(), activeEntity);
            }

        }
        if (form.getName() != null && !form.getName().isBlank() && !form.getName().equals(activeEntity.getName())) {
            activeEntity.setName(form.getName());
            isNameChanged = true;
        }
        activeEntity.setUpdatedAt(TimeUtil.now());
        afterUpdate(activeEntity, form.getCostCents(), isNameChanged || isIconUrlChanged);
    }

    public void createIncomeSource(ActiveEntity activeEntity) {
        IncomeSourceEntity incomeSourceEntity = incomeSourceManagementService.createIncomeSourceByActive(activeEntity);
        incomeSourceEntity.setMonthlyPlannedCents(calculationService
                .calculateMonthlyIncome(activeEntity));
        incomeSourceManagementService.save(incomeSourceEntity);
    }

    public void createAlreadyPaidTransactions(ActiveEntity activeEntity) {
        if (activeEntity.getAlreadyPaidCents() == null
                || activeEntity.getAlreadyPaidCents().compareTo(BigDecimal.ZERO) <= 0) return;

        ExpenseSourceEntity virtualExpenseSource = expenseSourceFinder.findFirstByParams(activeEntity.getBasketEntity().getUser(),
                true, activeEntity.getCurrency())
                .orElseGet(() -> expenseSourceManagementService.createDefault(activeEntity.getBasketEntity().getUser(),
                        true, activeEntity.getCurrency()));

        transactionService.createExpenseTransactionForActive(activeEntity, virtualExpenseSource);
    }

    public void bindActiveTransaction(ActiveEntity activeEntity, ActiveTransactionAttributes attributes) {
        transactionService.bindTransactionForActive(activeEntity, attributes.getId());
    }

    public void createInitialCostChange(ActiveEntity activeEntity, ActiveCreationForm.ActiveCF form) {
        if (form.getCostCents() != null && form.getCostCents().compareTo(0L) > 0) {
            transactionService.createCostChangeTransaction(activeEntity,
                    BigDecimal.valueOf(form.getCostCents()),
                    form.getActiveTransactionAttributes(),
                    true);

        }
    }

    private void afterUpdate(ActiveEntity activeEntity, Long newCostCentsFromForm, boolean isNeedToUpdateTransactions) {
        BigDecimal newCostCents = newCostCentsFromForm != null
                ? BigDecimal.valueOf(newCostCentsFromForm)
                : activeEntity.getCostCents();
        BigDecimal costDifference = activeEntity.getCostCents().negate()
                .add(newCostCents);
        IncomeSourceEntity activeIncomeSource = incomeSourceFinder.findIncomeSourceByActive(activeEntity);
        if (costDifference.compareTo(BigDecimal.ZERO) != 0) {
            transactionService.createCostChangeTransaction(activeEntity, costDifference, null, false);
        }
        if (activeIncomeSource != null) {
            incomeSourceManagementService
                    .updateIncomeSourceByActive(
                            activeIncomeSource,
                            activeEntity,
                            calculationService.calculateMonthlyIncome(activeEntity));
        }
        if (isNeedToUpdateTransactions) {
            updateTransactions(activeEntity);
        }
    }

    private void updateTransactions(ActiveEntity activeEntity) {
        Set<TransactionEntity> allActivesTransactions = transactionService.getAllOrderedByGotAtTransactions(activeEntity);
        allActivesTransactions.forEach(activeTransaction -> {
            if (activeTransaction.getSourceType().equals(EntityTypeEnum.Active.name())) {
                transactionService
                        .updateSourceTransactionWithNameAndIcon(activeTransaction, activeEntity.getName(), activeEntity.getIconUrl());
            } else {
                transactionService
                        .updateDestinationTransactionWithNameAndIcon(activeTransaction, activeEntity.getName(), activeEntity.getIconUrl());
            }

        });
    }

    private void destroyTransactions(ActiveEntity activeEntity) {
        Set<TransactionEntity> activeTransactions = transactionService.getAllOrderedByGotAtTransactions(activeEntity);
        Timestamp now = TimeUtil.now();
        activeTransactions.forEach(transactionEntity -> {
            transactionEntity.setDeletedAt(now);
            transactionService.afterDestroy(transactionEntity);
            transactionService.save(transactionEntity);
        });
    }

    private void setCreationTimestamps(ActiveEntity activeEntity) {
        Timestamp now = TimeUtil.now();
        activeEntity.setCreatedAt(now);
        activeEntity.setUpdatedAt(now);
    }

}
