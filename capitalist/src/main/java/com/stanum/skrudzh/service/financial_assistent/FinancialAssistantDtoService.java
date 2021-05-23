package com.stanum.skrudzh.service.financial_assistent;

import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.FreeMoney;
import com.stanum.skrudzh.model.dto.PlannedIncomeSaving;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.user.UserCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinancialAssistantDtoService {

    private final CurrencyService currencyService;

    private final UserCalculationService userCalculationService;

    public FreeMoney createFreeMoneyDto(UserEntity userEntity, long freeAmount) {
        FreeMoney freeMoney = new FreeMoney();
        freeMoney.setAmountCents(freeAmount);
        freeMoney.setCurrency(currencyService.getCurrencyByIsoCode(userEntity.getDefaultCurrency()));
        return freeMoney;
    }

    public PlannedIncomeSaving createPlannedIncomeSavingDto(UserEntity userEntity) {
        BigDecimal percent = userEntity.getPlannedSavingPercent();
        BigDecimal monthlyPlannedSaving = userCalculationService.calculateMonthlyPlannedSavings(userEntity);
        BigDecimal yearlyPlannedSaving = monthlyPlannedSaving != null
                ? monthlyPlannedSaving.multiply(BigDecimal.valueOf(12L))
                : null;

        PlannedIncomeSaving plannedIncomeSaving = new PlannedIncomeSaving();
        plannedIncomeSaving.setPercent(percent != null ? percent.longValue() : null);
        plannedIncomeSaving.setMonthlySavingCents(monthlyPlannedSaving != null ? monthlyPlannedSaving.longValue() : null);
        plannedIncomeSaving.setYearlySavingCents(yearlyPlannedSaving != null ? yearlyPlannedSaving.longValue() : null);
        return plannedIncomeSaving;
    }

}
