package com.stanum.skrudzh.service.basket;

import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.service.active.ActiveFinder;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketCalculationService {

    private final ExpenseCategoryFinder expenseCategoryFinder;

    private final ActiveFinder activeFinder;

    private final BasketTransactionsService transactionsService;

    private final ExchangeService exchangeService;

    private final UserUtil userUtil;

    public BigDecimal getSpentAtPeriod(BasketEntity basketEntity, PeriodEnum period, boolean withVirtual) {
        Set<TransactionEntity> transactionExpenseAtPeriod;
        if (basketEntity.getBasketType().equals(BasketTypeEnum.joy)) {
            transactionExpenseAtPeriod = transactionsService
                    .getExpenseCategoriesTransactionsAtPeriod(basketEntity, period, withVirtual);
        } else {
            transactionExpenseAtPeriod = transactionsService
                    .getActiveTransactionsAtPeriod(basketEntity, period);
        }
        return transactionExpenseAtPeriod.stream()
                .map(transactionEntity ->
                        transactionsService.getUserAmountByTransaction(transactionEntity, basketEntity.getUser()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPlannedAtPeriod(BasketEntity basketEntity) {
        Set<BigDecimal> plannedAtPeriod = expenseCategoriesMonthlyPlanned(basketEntity);
        plannedAtPeriod.addAll(activesMonthlyPlannedPayments(basketEntity));
        return plannedAtPeriod.stream()
                .map(bigDecimal ->
                        bigDecimal.multiply(userUtil.getMonthlyMultiplier(basketEntity.getUser().getDefaultPeriod()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<BigDecimal> expenseCategoriesMonthlyPlanned(BasketEntity basketEntity) {
        Set<ExpenseCategoryEntity> expenseCategoriesEntities = expenseCategoryFinder
                .findByBasketWithMonthlyCents(basketEntity);
        return expenseCategoriesEntities.stream()
                .map(expenseCategoryEntity -> {
                    if (expenseCategoryEntity.getMonthlyPlannedCents() == null) {
                        return BigDecimal.ZERO;
                    } else if (expenseCategoryEntity.getCurrency().equals(basketEntity.getUser().getDefaultCurrency())) {
                        return expenseCategoryEntity.getMonthlyPlannedCents();
                    } else {
                        return exchangeService
                                .exchange(expenseCategoryEntity.getCurrency(),
                                        basketEntity.getUser().getDefaultCurrency(),
                                        expenseCategoryEntity.getMonthlyPlannedCents());
                    }
                })
                .collect(Collectors.toSet());
    }

    private Set<BigDecimal> activesMonthlyPlannedPayments(BasketEntity basketEntity) {
        Set<ActiveEntity> activeEntities = activeFinder
                .findActivesWithMonthlyPlannedPayments(basketEntity);
        return activeEntities.stream()
                .map(activeEntity -> {
                    if (activeEntity.getMonthlyPaymentCents() == null) {
                        return BigDecimal.ZERO;
                    } else if (activeEntity.getCurrency().equals(basketEntity.getUser().getDefaultCurrency())) {
                        return activeEntity.getMonthlyPaymentCents();
                    } else {
                        return exchangeService
                                .exchange(activeEntity.getCurrency(),
                                        basketEntity.getUser().getDefaultCurrency(),
                                        activeEntity.getMonthlyPaymentCents());
                    }
                })
                .collect(Collectors.toSet());
    }


}
