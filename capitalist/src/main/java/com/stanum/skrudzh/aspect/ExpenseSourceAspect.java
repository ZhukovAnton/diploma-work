package com.stanum.skrudzh.aspect;

import com.stanum.skrudzh.controller.form.ExpenseSourceCreationForm;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceManagementService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceTransactionsService;
import com.stanum.skrudzh.service.saltedge.account_connection.AccountConnectionManagementService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Aspect
@Component
@RequiredArgsConstructor
public class ExpenseSourceAspect {

    private final ExpenseSourceFinder expenseSourceFinder;

    private final ExpenseSourceManagementService expenseSourceManagementService;

    private final ExpenseSourceTransactionsService transactionsService;

    private final AccountConnectionManagementService accountConnectionManagementService;

    @AfterReturning(value = "execution(* com.stanum.skrudzh.service.expense_source.ExpenseSourceManagementService.create(..)) && args(.., form)",
            returning = "expenseSourceEntity")
    public void afterExpenseSourceCreation(ExpenseSourceEntity expenseSourceEntity, ExpenseSourceCreationForm.ExpenseSourceCF form) {
        boolean isTransactionChangeable = !(form.getAccountConnectionAttributes() != null
                && form.getAccountConnectionAttributes().getAccountId() != null);
        if (form.getAmountCents() != null && form.getAmountCents().compareTo(0L) > 0 && isTransactionChangeable) {
            ExpenseSourceEntity virtualExpenseSource = expenseSourceFinder.findFirstByParams(expenseSourceEntity.getUser(),
                    true, expenseSourceEntity.getCurrency())
                    .orElseGet(() -> expenseSourceManagementService.createDefault(expenseSourceEntity.getUser(),
                            true, expenseSourceEntity.getCurrency()));
            transactionsService
                    .createInitialTransaction(
                            virtualExpenseSource,
                            expenseSourceEntity,
                            BigDecimal.valueOf(form.getAmountCents()),
                            isTransactionChangeable);
        }
    }

    @Before(value = "execution(* com.stanum.skrudzh.service.expense_source.ExpenseSourceManagementService.destroy(..)) && args(expenseSource, destroyAccountConnections, ..)", argNames = "expenseSource,destroyAccountConnections")
    public void beforeExpenseSourceDestruction(ExpenseSourceEntity expenseSource, boolean destroyAccountConnections) {
        if (destroyAccountConnections && expenseSource.getAccountConnectionEntity() != null) {
            accountConnectionManagementService
                    .destroyAccountConnection(
                            expenseSource.getAccountConnectionEntity(),
                            expenseSource);
        }
    }

}