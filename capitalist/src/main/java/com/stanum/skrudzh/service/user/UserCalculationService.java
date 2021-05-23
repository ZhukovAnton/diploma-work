package com.stanum.skrudzh.service.user;

import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import com.stanum.skrudzh.service.income_source.IncomeSourceCalculationService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserCalculationService {

    private final IncomeSourceFinder incomeSourceFinder;

    private final IncomeSourceCalculationService incomeSourceCalculationService;

    private final ExchangeService exchangeService;

    public BigDecimal calculateMonthlyPlannedSavings(UserEntity userEntity) {
        if (userEntity.getPlannedSavingPercent() == null) return null;
        Set<IncomeSourceEntity> realIncomeSources = incomeSourceFinder.findAllByUser(userEntity);
        return realIncomeSources.stream()
                .map(incomeSourceEntity -> {
                    var amount = incomeSourceEntity.getMonthlyPlannedCents() != null
                            ? incomeSourceEntity.getMonthlyPlannedCents()
                            : BigDecimal.ZERO;
                    exchangeService.exchange(incomeSourceEntity.getCurrency(), userEntity.getDefaultCurrency(), amount);
                    return incomeSourceCalculationService.getPlannedAtPeriod(incomeSourceEntity, PeriodEnum.month);
                })
                .filter(Objects::nonNull)
                .map(plannedIncomeAtMonth -> plannedIncomeAtMonth
                        .multiply(userEntity.getPlannedSavingPercent())
                        .divide(BigDecimal.valueOf(100L), MathContext.DECIMAL64))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
