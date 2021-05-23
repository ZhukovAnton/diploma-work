package com.stanum.skrudzh.service.financial_assistent;

import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.service.active.ActiveFinder;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryCalculationService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceCalculationService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FinancialAssistantCalculationService {

    private final ExpenseCategoryCalculationService expenseCategoryCalculationService;

    private final IncomeSourceFinder incomeSourceFinder;

    private final ExpenseCategoryFinder expenseCategoryFinder;

    private final ActiveFinder activeFinder;

    private final ExchangeService exchangeService;

    private final IncomeSourceCalculationService incomeSourceCalculationService;

    public long calculateFreeUsersMoneyPerPeriod(UserEntity userEntity, PeriodEnum periodEnum) {
        BigDecimal income = calculateIncome(userEntity);
        BigDecimal costsBase = calculateCostsBase(userEntity);
        BigDecimal alreadyDoneExpenses = calculateAlreadyDoneNotObligatoryExpenses(userEntity);
        BigDecimal plannedSavingPercent = userEntity.getPlannedSavingPercent() != null
                ? userEntity.getPlannedSavingPercent()
                : BigDecimal.ZERO;
        BigDecimal freeIncome = (income.multiply(
                BigDecimal.valueOf(100L).add(plannedSavingPercent.negate())
                        .divide(BigDecimal.valueOf(100L), MathContext.DECIMAL64)))
                .add(costsBase.negate());
        if (periodEnum.equals(PeriodEnum.month))
            return freeIncome.add(alreadyDoneExpenses.negate()).longValue();
        else {
            BigDecimal costsDaily = freeIncome
                    .divide(BigDecimal.valueOf(TimeUtil.amountOfDaysInCurrentMonth()), MathContext.DECIMAL64);
            return costsDaily.multiply(BigDecimal.valueOf(TimeUtil.dayOfCurrentMonth()))
                    .add(alreadyDoneExpenses.negate()).longValue();
        }
    }

    private BigDecimal calculateIncome(UserEntity userEntity) {
        Set<IncomeSourceEntity> usersIncomeSources =
                incomeSourceFinder.findAllByUser(userEntity);
        BigDecimal plannedIncomePerMonth = incomeSourceCalculationService
                .incomesPlannedAtPeriod(userEntity, usersIncomeSources, PeriodEnum.month);
        BigDecimal gotIncomePerMonth = incomeSourceCalculationService
                .incomesGotAtPeriod(userEntity, usersIncomeSources, PeriodEnum.month);
        return gotIncomePerMonth.compareTo(plannedIncomePerMonth) > 0 ? gotIncomePerMonth : plannedIncomePerMonth;
    }

    private BigDecimal calculateCostsBase(UserEntity userEntity) {
        Set<ExpenseCategoryEntity> usersExpenseCategoriesWithPlannedExpenses =
                expenseCategoryFinder.findAllWithPlannedExpenses(userEntity);
        Set<ActiveEntity> usersActivesWithPlannedInvestments =
                activeFinder.findActivesWithMonthlyPlannedPayments(userEntity);
        BigDecimal expenseCategoriesPlannedExpenses = usersExpenseCategoriesWithPlannedExpenses.stream()
                .map(expenseCategoryEntity -> {
                    var amount = expenseCategoryEntity.getMonthlyPlannedCents();
                    return exchangeService.exchange(expenseCategoryEntity.getCurrency(), userEntity.getDefaultCurrency(), amount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal activesPlannedInvestments = usersActivesWithPlannedInvestments.stream()
                .map(activeEntity -> {
                    var amount = activeEntity.getMonthlyPaymentCents();
                    return exchangeService.exchange(activeEntity.getCurrency(), userEntity.getDefaultCurrency(), amount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return expenseCategoriesPlannedExpenses.add(activesPlannedInvestments);
    }

    private BigDecimal calculateAlreadyDoneNotObligatoryExpenses(UserEntity userEntity) {
        Set<ExpenseCategoryEntity> expenseCategoriesWithoutMonthlyPlanned = expenseCategoryFinder.findAllWithoutPlannedExpenses(userEntity);
        return expenseCategoryCalculationService
                .spentAtPeriodWithinCategories(
                        expenseCategoriesWithoutMonthlyPlanned,
                        PeriodEnum.month,
                        userEntity.getDefaultCurrency());
    }
}
