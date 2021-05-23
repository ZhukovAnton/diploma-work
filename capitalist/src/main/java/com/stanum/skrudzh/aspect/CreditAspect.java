package com.stanum.skrudzh.aspect;

import com.stanum.skrudzh.controller.form.CreditCreationForm;
import com.stanum.skrudzh.jpa.model.CreditEntity;
import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.credit.CreditManagementService;
import com.stanum.skrudzh.service.credit.CreditTransactionsService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryManagementService;
import com.stanum.skrudzh.service.order.OrderService;
import com.stanum.skrudzh.service.reminder.ReminderService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import com.stanum.skrudzh.model.enums.OrderType;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CreditAspect {

    private final CreditManagementService creditManagementService;

    private final ReminderService reminderService;

    private final ExpenseCategoryManagementService expenseCategoryManagementService;

    private final CreditTransactionsService creditTransactionsService;

    private final ExpenseCategoryFinder expenseCategoryFinder;

    private final OrderService orderService;

    @AfterReturning(value = "execution(* com.stanum.skrudzh.service.credit.CreditManagementService.createCreditWithCreationForm(..)) && args(userEntity, form)",
            returning = "creditEntity")
    public void afterCreditCreation(CreditEntity creditEntity, UserEntity userEntity, CreditCreationForm.CreditCF form) {
        if (form.getCreditingTransactionAttributes() != null) {
            if(form.getCreditingTransactionAttributes().getId() != null) {
                creditManagementService.bindCreditingTransaction(creditEntity, form.getCreditingTransactionAttributes());
            } else {
                creditManagementService.createCreditingTransaction(creditEntity, form.getCreditingTransactionAttributes());
            }
        }
        if (form.getReminderAttributes() != null && form.getReminderAttributes().getId() != null) {
            reminderService.saveUpdatedEntity(form.getReminderAttributes(), creditEntity);
        } else {
            reminderService.saveCreatedEntity(form.getReminderAttributes(), creditEntity);
        }
        expenseCategoryManagementService.createExpenseCategoryForCredit(creditEntity);
        if(RequestUtil.hasGlobalSorting()) {
            orderService.updateOrder(creditEntity.getUser(),
                    OrderType.CREDIT_BORROW,
                    EntityTypeEnum.Credit,
                    creditEntity.getId(),
                    form.getRowOrderPosition());
        }
    }

    @Before(value = "execution(* com.stanum.skrudzh.service.credit.CreditManagementService.destroyCredit(..)) && args(creditEntity, isNeedToDeleteTransactions)")
    public void beforeCreditDestruction(CreditEntity creditEntity, boolean isNeedToDeleteTransactions) {
        TransactionEntity creditTransaction = creditTransactionsService.getCreditTransaction(creditEntity);
        creditTransaction.setDeletedAt(TimeUtil.now());
        creditTransactionsService.afterDestroy(creditTransaction);
        creditTransactionsService.save(creditTransaction);

        ExpenseCategoryEntity creditExpenseCategory = expenseCategoryFinder.findByCredit(creditEntity);
        expenseCategoryManagementService.destroyExpenseCategory(creditExpenseCategory, isNeedToDeleteTransactions);
    }

}