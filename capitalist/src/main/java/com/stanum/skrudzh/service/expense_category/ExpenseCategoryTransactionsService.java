package com.stanum.skrudzh.service.expense_category;

import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.service.TransactionBase;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Set;

@Service
public class ExpenseCategoryTransactionsService extends TransactionBase {

    @Autowired
    public ExpenseCategoryTransactionsService(EntityUtil entityUtil,
                                              UserUtil userUtil,
                                              CurrencyService currencyService,
                                              TransactionRepository transactionRepository) {
        super(transactionRepository, entityUtil, userUtil, currencyService);
    }

    public Set<TransactionEntity> getExpenseTransactionsForExpenseCategory(ExpenseCategoryEntity expenseCategoryEntity) {
        return transactionRepository.getTransactionsForExpenseCategory(expenseCategoryEntity.getId());
    }

    public Set<TransactionEntity> getTransactionsInPeriod(ExpenseCategoryEntity expenseCategoryEntity, PeriodEnum period) {
        Timestamp from = RequestUtil.getBeginningOfPeriod(period);
        Timestamp till = RequestUtil.getEndOfPeriod(period);
        return transactionRepository
                .getExpensesAsDestinationWithinPeriod(
                        expenseCategoryEntity.getId(),
                        EntityTypeEnum.ExpenseCategory.name(),
                        from,
                        till);
    }

    public Set<TransactionEntity> findTransactionsAsDestinationWithinUserPeriod(ExpenseCategoryEntity destination) {
        UserEntity user = destination.getUser() != null ? destination.getUser() : destination.getBasket().getUser();
        String destinationType = EntityTypeEnum.ExpenseCategory.name();
        Long destinationId = destination.getId();
        Timestamp from = RequestUtil.getBeginningOfDefaultPeriod(user);
        Timestamp to = RequestUtil.getEndOfDefaultPeriod(user);
        return transactionRepository
                .getExpensesAsDestinationWithinPeriod(
                        destinationId,
                        destinationType,
                        from,
                        to);
    }


}
