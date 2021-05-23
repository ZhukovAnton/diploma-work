package com.stanum.skrudzh.service.income_source;

import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IncomeSourceCalculationService {

    private final UserUtil userUtil;

    private final ExchangeService exchangeService;

    private final IncomeSourceTransactionsService transactionsService;

    public BigDecimal getGotAtPeriod(IncomeSourceEntity incomeSourceEntity, PeriodEnum period, String currencyCode) {
        Set<TransactionEntity> incomeTransactions = transactionsService.findIncomeTransactionsInPeriod(
                incomeSourceEntity,
                RequestUtil.getBeginningOfPeriod(period),
                RequestUtil.getEndOfPeriod(period));
        if (incomeSourceEntity.getActive() != null) {
            Set<TransactionEntity> positiveProfitTransactions = transactionsService.findPositiveProfitTransactions(
                    incomeSourceEntity.getActive(),
                    RequestUtil.getBeginningOfPeriod(period),
                    RequestUtil.getEndOfPeriod(period));
            incomeTransactions.addAll(positiveProfitTransactions);
        }
        return incomeTransactions
                .stream()
                .map(transactionEntity -> {
                    if (transactionEntity.getProfit() != null) {
                        return exchangeService.exchange(transactionEntity.getAmountCurrency(),
                                currencyCode,
                                transactionEntity.getProfit());
                    } else {
                        return exchangeService.exchange(transactionEntity.getAmountCurrency(),
                                currencyCode,
                                transactionEntity.getAmountCents());
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal incomesGotAtPeriod(UserEntity userEntity, Set<IncomeSourceEntity> incomeSourceEntities, PeriodEnum period) {
        return incomeSourceEntities
                .stream()
                .map(incomeSourceEntity ->
                        getGotAtPeriod(incomeSourceEntity, period, userEntity.getDefaultCurrency())
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPlannedAtPeriod(IncomeSourceEntity incomeSourceEntity, PeriodEnum period) {
        if (incomeSourceEntity.getMonthlyPlannedCents() != null) {
            return incomeSourceEntity.getMonthlyPlannedCents()
                    .multiply(userUtil.getMonthlyMultiplier(period));
        } else {
            return null;
        }
    }

    public BigDecimal incomesPlannedAtPeriod(UserEntity userEntity, Set<IncomeSourceEntity> incomeSourceEntities, PeriodEnum period) {
        return incomeSourceEntities
                .stream()
                .map(incomeSourceEntity -> {
                    var amount = incomeSourceEntity.getMonthlyPlannedCents() != null
                            ? incomeSourceEntity.getMonthlyPlannedCents()
                            : BigDecimal.ZERO;
                    return exchangeService.exchange(
                            incomeSourceEntity.getCurrency(),
                            userEntity.getDefaultCurrency(),
                            amount);
                })
                .map(monthlyPlannedCents -> monthlyPlannedCents.multiply(userUtil.getMonthlyMultiplier(period)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
