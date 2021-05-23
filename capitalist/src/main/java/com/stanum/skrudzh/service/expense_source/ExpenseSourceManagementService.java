package com.stanum.skrudzh.service.expense_source;

import com.stanum.skrudzh.controller.form.ExpenseSourceCreationForm;
import com.stanum.skrudzh.controller.form.ExpenseSourceUpdatingForm;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.jpa.model.TransactionableExampleEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;

public interface ExpenseSourceManagementService {
    ExpenseSourceEntity create(UserEntity userEntity,
                               ExpenseSourceCreationForm.ExpenseSourceCF form);

    ExpenseSourceEntity createDefault(UserEntity userEntity,
                                      boolean isVirtual,
                                      String currencyCode);

    ExpenseSourceEntity createDefault(TransactionableExampleEntity template,
                                      UserEntity userEntity,
                                      boolean isVirtual,
                                      String currencyCode,
                                      String name);

    void update(ExpenseSourceEntity expenseSourceEntity,
                ExpenseSourceUpdatingForm.ExpenseSourceUF form,
                boolean isNullAllowed,
                boolean hasTransactions);

    void destroy(ExpenseSourceEntity expenseSourceEntity,
                 boolean destroyAccountConnections,
                 boolean destroyTransactions);

    void syncBalances(ExpenseSourceEntity expenseSourceEntity);
}
