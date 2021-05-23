package com.stanum.skrudzh.service.credit;

import com.stanum.skrudzh.controller.form.CreditCreationForm;
import com.stanum.skrudzh.controller.form.CreditUpdatingForm;
import com.stanum.skrudzh.controller.form.attributes.CreditingTransactionAttributes;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.CreditRepository;
import com.stanum.skrudzh.model.enums.RemindableTypeEnum;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryManagementService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceManagementService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceManagementService;
import com.stanum.skrudzh.service.reminder.ReminderService;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditManagementService {

    private final CreditTypesService creditTypesService;

    private final IncomeSourceManagementService incomeSourceManagementService;

    private final ExpenseSourceFinder expenseSourceFinder;

    private final ExpenseSourceManagementService expenseSourceManagementService;

    private final ExpenseCategoryFinder expenseCategoryFinder;

    private final ExpenseCategoryManagementService expenseCategoryManagementService;

    private final CreditTransactionsService creditTransactionsService;

    private final CreditValidationService validationService;

    private final CreditCalculationService calculationService;

    private final ReminderService reminderService;

    private final CreditRepository creditRepository;

    private final IncomeSourceFinder incomeSourceFinder;

    public CreditEntity createCreditWithCreationForm(UserEntity userEntity, CreditCreationForm.CreditCF form) {
        CreditEntity creditEntity = new CreditEntity();
        creditEntity.setUser(userEntity);
        checkAndFillCreditWithCreationForm(creditEntity, form);
        updateIsPaid(creditEntity);
        save(creditEntity);
        return creditEntity;
    }

    public void updateCredit(CreditEntity creditEntity, CreditUpdatingForm.CreditUF form) {
        checkAndUpdateCreditWithUpdatingForm(creditEntity, form);
        updateIsPaid(creditEntity);
        save(creditEntity);
    }

    public void destroyCredit(CreditEntity creditEntity, boolean isNeedToDeleteTransactions) {
        creditEntity.setDeletedAt(TimeUtil.now());
        save(creditEntity);
    }

    public void updateIsPaid(CreditEntity creditEntity) {
        creditEntity.setIsPaid(calculationService.paidAmount(creditEntity).compareTo(creditEntity.getReturnAmountCents()) >= 0);
        save(creditEntity);
    }

    public void save(CreditEntity creditEntity) {
        creditRepository.save(creditEntity);
    }

    public String notificationLocKey() {
        return "CREDIT_NOTIFICATION_MESSAGE_KEY";
    }

    public String[] notificationLocArgs(CreditEntity creditEntity) {
        ReminderEntity reminder = reminderService.findBySourceIdAndType(creditEntity.getId(), RemindableTypeEnum.Credit);
        if (reminder != null) {
            return new String[]{creditEntity.getName(), reminder.getMessage() != null ? reminder.getMessage() : ""};
        } else {
            return new String[]{creditEntity.getName(), ""};
        }
    }

    private void checkAndFillCreditWithCreationForm(CreditEntity creditEntity, CreditCreationForm.CreditCF form) {
        validationService.validateCreationForm(form);
        CreditTypeEntity creditTypeEntity = creditTypesService.getCreditTypeById(form.getCreditTypeId());
        creditEntity.setAlreadyPaidCents(form.getAlreadyPaidCents() != null
                ? BigDecimal.valueOf(form.getAlreadyPaidCents())
                : BigDecimal.ZERO);
        creditEntity.setAmountCents(BigDecimal.valueOf(form.getAmountCents()));
        creditEntity.setReturnAmountCents(BigDecimal.valueOf(form.getReturnAmountCents()));
        creditEntity.setMonthlyPaymentCents(form.getMonthlyPaymentCents() != null
                ? BigDecimal.valueOf(form.getMonthlyPaymentCents())
                : null);
        creditEntity.setCreditTypeEntity(creditTypeEntity);
        creditEntity.setCurrency(form.getCurrency() != null
                ? form.getCurrency()
                : creditEntity.getUser().getDefaultCurrency());
        creditEntity.setGotAt(form.getGotAt() != null
                ? Timestamp.valueOf(form.getGotAt())
                : TimeUtil.now());
        creditEntity.setIconUrl(form.getIconUrl());
        creditEntity.setName(form.getName());
        creditEntity.setPeriod(form.getPeriod());
        setCreationTimestamps(creditEntity);
    }

    private void setCreationTimestamps(CreditEntity creditEntity) {
        Timestamp now = TimeUtil.now();
        creditEntity.setCreatedAt(now);
        creditEntity.setUpdatedAt(now);
    }

    public void createCreditingTransaction(CreditEntity creditEntity, CreditingTransactionAttributes attributes) {
        log.info("Create crediting transaction for creditEntityId = {}, attributes={}", creditEntity.getId(), attributes);
        IncomeSourceEntity borrowOrReturnIncomeSource = incomeSourceFinder.findBorrowIncomeSource(creditEntity.getUser(),
                creditEntity.getCurrency())
                .orElseGet(() -> incomeSourceManagementService.createBorrowIncomeSource(creditEntity.getUser(),
                        creditEntity.getCurrency()));

        ExpenseSourceEntity destinationExpenseSource;
        if (attributes.getDestinationId() != null) {
            destinationExpenseSource = expenseSourceFinder.findById(attributes.getDestinationId());
        } else {
            destinationExpenseSource = expenseSourceFinder.findFirstByParams(creditEntity.getUser(),
                    true, creditEntity.getCurrency())
                    .orElseGet(() -> expenseSourceManagementService.createDefault(creditEntity.getUser(),
                            true, creditEntity.getCurrency()));
        }
        creditTransactionsService.createCreditingTransaction(creditEntity, borrowOrReturnIncomeSource, destinationExpenseSource);
    }

    public void bindCreditingTransaction(CreditEntity creditEntity, CreditingTransactionAttributes attributes) {
        creditTransactionsService.bindCreditingTransaction(creditEntity, attributes.getId());
    }

    private void checkAndUpdateCreditWithUpdatingForm(CreditEntity creditEntity, CreditUpdatingForm.CreditUF form) {
        validationService.validateUpdatingForm(form);
        boolean isMonthlyAmountChanged = false;
        boolean isAmountChanged = false;
        boolean isGotAtChanged = false;
        boolean isIconChanged = false;
        boolean isNameChanged = false;
        if (form.getMonthlyPaymentCents() != null && !form.getMonthlyPaymentCents().equals(creditEntity.getMonthlyPaymentCents().longValue())) {
            creditEntity.setMonthlyPaymentCents(BigDecimal.valueOf(form.getMonthlyPaymentCents()));
            isMonthlyAmountChanged = true;
        }
        if (form.getAmountCents() != null && !form.getAmountCents().equals(creditEntity.getAmountCents().longValue())) {
            creditEntity.setAmountCents(BigDecimal.valueOf(form.getAmountCents()));
            isAmountChanged = true;
        }
        if (form.getReturnAmountCents() != null) {
            creditEntity.setReturnAmountCents(BigDecimal.valueOf(form.getReturnAmountCents()));
        }
        if (form.getGotAt() != null && !Timestamp.valueOf(form.getGotAt()).equals(creditEntity.getGotAt())) {
            creditEntity.setGotAt(Timestamp.valueOf(form.getGotAt()));
            isGotAtChanged = true;
        }
        if (form.getName() != null && !form.getName().isBlank() && !form.getName().equals(creditEntity.getName())) {
            creditEntity.setName(form.getName());
            isNameChanged = true;
        }
        if (form.getIconUrl() != null && !form.getIconUrl().equals(creditEntity.getIconUrl())) {
            creditEntity.setIconUrl(form.getIconUrl());
            isIconChanged = true;
        }
        if (form.getPeriod() != null) {
            creditEntity.setPeriod(form.getPeriod());
        }
        if (form.getReminderAttributes() != null) {
            if (form.getReminderAttributes().getId() != null) {
                reminderService.saveUpdatedEntity(form.getReminderAttributes(), creditEntity);
            } else {
                reminderService.saveCreatedEntity(form.getReminderAttributes(), creditEntity);
            }

        }
        updateExpenseCategory(creditEntity, isNameChanged || isIconChanged || isMonthlyAmountChanged);
        creditTransactionsService.updateCreditTransaction(creditEntity, isGotAtChanged || isAmountChanged);
    }

    private void updateExpenseCategory(CreditEntity creditEntity, boolean isNeedToUpdate) {
        if (!isNeedToUpdate) return;
        ExpenseCategoryEntity creditExpenseCategory = expenseCategoryFinder.findByCredit(creditEntity);
        creditExpenseCategory.setName(creditEntity.getName());
        creditExpenseCategory.setIconUrl(creditEntity.getIconUrl());
        creditExpenseCategory.setMonthlyPlannedCents(creditEntity.getMonthlyPaymentCents());
        expenseCategoryManagementService.afterUpdate(creditExpenseCategory, true);
        expenseCategoryManagementService.save(creditExpenseCategory);
    }

}
