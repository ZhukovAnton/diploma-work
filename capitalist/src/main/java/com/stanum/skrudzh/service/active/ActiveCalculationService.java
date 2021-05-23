package com.stanum.skrudzh.service.active;

import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ActiveCalculationService {

    private final UserUtil userUtil;

    private final ActiveTransactionsService transactionsService;

    private final ExchangeService exchangeService;

    private final ActiveFinder activeFinder;

    public BigDecimal getPaymentAtDefaultPeriod(ActiveEntity activeEntity) {
        if (activeEntity.getMonthlyPaymentCents() == null) return null;
        else {
            return userUtil.getMonthlyMultiplier(activeEntity.getBasketEntity().getUser().getDefaultPeriod())
                    .multiply(activeEntity.getMonthlyPaymentCents());
        }
    }

    public BigDecimal getSpentAtDefaultPeriod(ActiveEntity activeEntity) {
        UserEntity userEntity = activeEntity.getUser() != null ? activeEntity.getUser() : activeEntity.getBasketEntity().getUser();
        Timestamp from = RequestUtil.getBeginningOfDefaultPeriod(userEntity);
        Timestamp till = RequestUtil.getEndOfDefaultPeriod(userEntity);
        return transactionsService.getExpenseTransactionsWithParams(activeEntity, from, till)
                .stream()
                .map(TransactionEntity::getConvertedAmountCents)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getBoughtAtDefaultPeriod(ActiveEntity activeEntity) {
        UserEntity userEntity = activeEntity.getUser() != null ? activeEntity.getUser() : activeEntity.getBasketEntity().getUser();
        Timestamp from = RequestUtil.getBeginningOfDefaultPeriod(userEntity);
        Timestamp till = RequestUtil.getEndOfDefaultPeriod(userEntity);
        return transactionsService.getBuyTransactionsWithParams(activeEntity, from, till)
                .stream()
                .map(TransactionEntity::getConvertedAmountCents)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getProfit(ActiveEntity activeEntity, TransactionEntity transactionEntity) {
        BigDecimal sellCost = transactionEntity.getAmountCents();
        if (activeEntity.getCostCents().compareTo(sellCost) < 0) return null;
        Pair<BigDecimal, BigDecimal> fullSaleProfitAndInvestedAmount = getFullSaleProfitAndInvested(activeEntity);
        BigDecimal fullSaleProfit = fullSaleProfitAndInvestedAmount.getFirst();
        BigDecimal investedInBalance = fullSaleProfitAndInvestedAmount.getSecond();
        BigDecimal balance = activeEntity.getCostCents();
        if (balance.compareTo(sellCost) >= 0
                && investedInBalance.compareTo(sellCost) < 0 && investedInBalance.add(fullSaleProfit).compareTo(sellCost) >= 0) {
            return sellCost.add(investedInBalance.negate());
        } else if (balance.compareTo(sellCost) == 0) {
            return fullSaleProfit;
        } else {
            return null;
        }
    }

    //TODO: improve with data storage
    public Pair<BigDecimal, BigDecimal> getFullSaleProfitAndInvested(ActiveEntity activeEntity) {
        Set<TransactionEntity> allActiveTransactions = transactionsService.getAllOrderedByGotAtTransactions(activeEntity);
        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal invested = BigDecimal.ZERO;
        BigDecimal fullSaleProfit = BigDecimal.ZERO;
        for (TransactionEntity activeTransaction : allActiveTransactions) {
            if (transactionsService.isInvestTransaction(activeTransaction)) {
                balance = balance.add(activeTransaction.getConvertedAmountCents());
                invested = invested.add(activeTransaction.getConvertedAmountCents());
            }
            if (transactionsService.isRefundIncTransaction(activeTransaction)) {
                balance = balance.add(activeTransaction.getConvertedAmountCents());
                fullSaleProfit = balance.add(invested.negate());
            }
            if (transactionsService.isRefundDecTransaction(activeTransaction)) {
                balance = balance.add(activeTransaction.getAmountCents().negate());
                fullSaleProfit = balance.add(invested.negate());
            }
            if (transactionsService.isSaleTransaction(activeTransaction)) {
                BigDecimal sellAmount = activeTransaction.getAmountCents();
                if (invested.compareTo(sellAmount) >= 0 && balance.compareTo(sellAmount) > 0) {
                    invested = invested.add(sellAmount.negate());
                } else if (invested.compareTo(sellAmount) < 0) {
                    BigDecimal notEnough = sellAmount.add(invested.negate());
                    //notEnough amount from invested is taken from fullSaleProfit,
                    // so notEnough value is profit of sale transaction
                    invested = BigDecimal.ZERO;
                    fullSaleProfit = fullSaleProfit.add(notEnough.negate());
                } else if (invested.compareTo(sellAmount) >= 0 && balance.compareTo(sellAmount) <= 0) {
                    if (balance.compareTo(sellAmount) == 0) {
                        fullSaleProfit = BigDecimal.ZERO;
                    }
                    invested = invested.add(sellAmount.negate());
                }
                balance = balance.add(sellAmount.negate());
            }
        }
        return Pair.of(fullSaleProfit, invested);
    }

    public BigDecimal calculateMonthlyIncome(ActiveEntity activeEntity) {
        switch (activeEntity.getPlannedIncomeType()) {
            case monthly_income:
                return activeEntity.getMonthlyPlannedIncomeCents();
            case annual_percents:
                if (activeEntity.getAnnualIncomePercent() != null) {
                    BigDecimal income = activeEntity.getCostCents().multiply(BigDecimal.valueOf(activeEntity.getAnnualIncomePercent()));
                    income = income.divide(BigDecimal.valueOf(10000L), RoundingMode.CEILING);
                    income = income.divide(BigDecimal.valueOf(12L), RoundingMode.CEILING);
                    return income;
                }
            default:
                return BigDecimal.ZERO;
        }
    }

    public BigDecimal activesSummaryBalance(BasketEntity basketEntity) {
        return activeFinder.findAllActivesByBasket(basketEntity.getId()).stream()
                .map(activeEntity -> {
                    var amount = activeEntity.getCostCents();
                    return exchangeService.exchange(activeEntity.getCurrency(),
                            basketEntity.getUser().getDefaultCurrency(),
                            amount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
