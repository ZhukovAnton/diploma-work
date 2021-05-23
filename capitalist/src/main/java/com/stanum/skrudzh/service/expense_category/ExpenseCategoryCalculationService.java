package com.stanum.skrudzh.service.expense_category;

import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.service.basket.BasketCalculationService;
import com.stanum.skrudzh.service.basket.BasketFinder;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryCalculationService {

    private final UserUtil userUtil;

    private final ExpenseCategoryTransactionsService transactionsService;

    private final ExchangeService exchangeService;

    private final BasketCalculationService basketCalculationService;

    private final BasketFinder basketFinder;

    public BigDecimal getPlannedAtDefaultPeriod(ExpenseCategoryEntity expenseCategoryEntity) {
        return expenseCategoryEntity.getMonthlyPlannedCents() != null
                ? expenseCategoryEntity.getMonthlyPlannedCents()
                .multiply(userUtil.getMonthlyMultiplier(expenseCategoryEntity.getBasket().getUser().getDefaultPeriod()))
                : null;
    }

    public BigDecimal spentAtDefaultPeriod(ExpenseCategoryEntity expenseCategoryEntity) {
        Set<TransactionEntity> transactionEntities = transactionsService.findTransactionsAsDestinationWithinUserPeriod(expenseCategoryEntity);
        return transactionEntities.stream()
                .map(TransactionEntity::getConvertedAmountCents)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal spentAtPeriodWithinCategories(Set<ExpenseCategoryEntity> expenseCategoryEntities, PeriodEnum period, String currencyCode) {
        return expenseCategoryEntities.stream()
                .flatMap(expenseCategoryEntity -> transactionsService.getTransactionsInPeriod(expenseCategoryEntity, period).stream())
                .map(transactionEntity -> exchangeService
                        .exchange(transactionEntity.getConvertedAmountCurrency(),
                                currencyCode,
                                transactionEntity.getConvertedAmountCents()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal expensesAtPeriod(UserEntity userEntity, PeriodEnum period) {
        Set<BasketEntity> basketEntities = basketFinder.findBasketsByUserId(userEntity.getId());
        return basketEntities.stream()
                .map(basketEntity -> basketCalculationService.getSpentAtPeriod(basketEntity, period, true))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal expensesPlannedAtPeriod(UserEntity userEntity) {
        Set<BasketEntity> basketEntities = basketFinder.findBasketsByUserId(userEntity.getId());
        return basketEntities.stream()
                .map(basketCalculationService::getPlannedAtPeriod)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
