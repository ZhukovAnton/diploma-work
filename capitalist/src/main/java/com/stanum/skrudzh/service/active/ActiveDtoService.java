package com.stanum.skrudzh.service.active;

import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.jpa.model.ReminderEntity;
import com.stanum.skrudzh.model.dto.Active;
import com.stanum.skrudzh.model.dto.Actives;
import com.stanum.skrudzh.model.dto.Currency;
import com.stanum.skrudzh.model.dto.Reminder;
import com.stanum.skrudzh.model.enums.RemindableTypeEnum;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.income_source.IncomeSourceDtoService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.service.reminder.ReminderService;
import com.stanum.skrudzh.service.saltedge.account_connection.AccountConnectionDtoService;
import com.stanum.skrudzh.service.saltedge.connectable.ConnectableCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActiveDtoService {

    private final ReminderService reminderService;

    private final ActiveCalculationService calculationService;

    private final ConnectableCalculationService connectableCalculationService;

    private final CurrencyService currencyService;

    private final IncomeSourceDtoService incomeSourceDtoService;

    private final AccountConnectionDtoService accountConnectionDtoService;

    private final IncomeSourceFinder incomeSourcesFinder;

    private final ActiveManagementService managementService;

    public Actives createActiveDtoList(Set<ActiveEntity> activeEntities) {
        return new Actives(
                activeEntities.stream()
                        .map(this::createActiveDto)
                        .collect(Collectors.toList())
        );
    }

    public Active createActiveDto(ActiveEntity activeEntity) {
        Active activeDto = new Active(activeEntity);
        ReminderEntity reminderEntity = reminderService.findBySourceIdAndType(activeEntity.getId(), RemindableTypeEnum.Active);
        IncomeSourceEntity incomeSourceEntity = incomeSourcesFinder.findIncomeSourceByActive(activeEntity);
        Currency currency = currencyService.getCurrencyByIsoCode(activeEntity.getCurrency());
        BigDecimal paymentAtPeriod = calculationService.getPaymentAtDefaultPeriod(activeEntity);
        BigDecimal spentAtPeriod = calculationService.getSpentAtDefaultPeriod(activeEntity);
        BigDecimal boughtAtPeriod = calculationService.getBoughtAtDefaultPeriod(activeEntity);
        BigDecimal fullSaleProfit = calculationService.getFullSaleProfitAndInvested(activeEntity).getFirst();
        BigDecimal investedAtPeriod = BigDecimal.ZERO;
        if (boughtAtPeriod != null) investedAtPeriod = investedAtPeriod.add(boughtAtPeriod);
        if (spentAtPeriod != null) investedAtPeriod = investedAtPeriod.add(spentAtPeriod);
        Timestamp fetchFromDateTimestamp = connectableCalculationService.calculateFetchFromDate(activeEntity);
        ZonedDateTime fetchFromDate = fetchFromDateTimestamp != null
                ? ZonedDateTime.of(fetchFromDateTimestamp.toLocalDateTime(), ZoneId.of("Z"))
                : null;
        activeDto.setReminder(reminderEntity != null
                ? new Reminder(reminderEntity)
                : null);
        activeDto.setIncomeSource(incomeSourceEntity != null
                ? incomeSourceDtoService.createIncomeSourceResponse(incomeSourceEntity)
                : null);
        activeDto.setCurrency(currency);
        activeDto.setPaymentCentsAtPeriod(paymentAtPeriod != null
                ? paymentAtPeriod.longValue()
                : null);
        activeDto.setSpentAtPeriodCents(spentAtPeriod != null
                ? spentAtPeriod.longValue()
                : 0L);
        activeDto.setBoughtAtPeriodCents(boughtAtPeriod != null
                ? boughtAtPeriod.longValue()
                : 0L);
        activeDto.setFullSaleProfit(fullSaleProfit.longValue());
        activeDto.setInvestedAtPeriodCents(investedAtPeriod != null
                ? investedAtPeriod.longValue()
                : 0L);
        activeDto.setFetchFromDate(fetchFromDate);
        if (activeEntity.getAccountConnectionEntity() != null) {
            activeDto.setSaltEdgeAccountConnection(accountConnectionDtoService
                    .createAccountConnectionDto(activeEntity.getAccountConnectionEntity()));
        }
        activeDto.setActiveOpen(managementService.isActiveOpen(activeEntity));
        return activeDto;
    }

}
