package com.stanum.skrudzh.service.income_source;

import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.jpa.model.ReminderEntity;
import com.stanum.skrudzh.model.dto.Currency;
import com.stanum.skrudzh.model.dto.IncomeSource;
import com.stanum.skrudzh.model.dto.IncomeSources;
import com.stanum.skrudzh.model.dto.Reminder;
import com.stanum.skrudzh.model.enums.RemindableTypeEnum;
import com.stanum.skrudzh.service.borrow.BorrowManagementService;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.reminder.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeSourceDtoService {

    private final IncomeSourceCalculationService calculationService;

    private final ReminderService reminderService;

    private final BorrowManagementService borrowManagementService;

    public IncomeSources createIncomeSourcesResponse(List<IncomeSourceEntity> incomeSourceEntities) {
        return new IncomeSources(
                incomeSourceEntities.stream()
                        .map(this::createIncomeSourceResponse)
                        .collect(Collectors.toList())
        );
    }

    public IncomeSource createIncomeSourceResponse(IncomeSourceEntity incomeSourceEntity) {
        IncomeSource incomeSource = new IncomeSource(incomeSourceEntity);
        Currency currency = CurrencyService.getCurrencyByIsoCode(incomeSourceEntity.getCurrency());
        BigDecimal plannedAtPeriod = calculationService
                .getPlannedAtPeriod(incomeSourceEntity, incomeSourceEntity.getUser().getDefaultPeriod());
        incomeSource.setCurrency(currency);
        ReminderEntity reminderEntity = reminderService
                .findBySourceIdAndType(incomeSourceEntity.getId(), RemindableTypeEnum.IncomeSource);
        incomeSource.setReminder(reminderEntity != null ? new Reminder(reminderEntity) : null);
        incomeSource.setPlannedCentsAtPeriod(plannedAtPeriod != null ? plannedAtPeriod.longValue() : null);
        incomeSource.setGotCentsAtPeriod(calculationService
                .getGotAtPeriod(incomeSourceEntity,
                        incomeSourceEntity.getUser().getDefaultPeriod(),
                        incomeSourceEntity.getCurrency()).longValue());
        if (incomeSourceEntity.getIsBorrow()) {
            incomeSource.setWaitingDebts(borrowManagementService
                    .getWaitingDebts(incomeSourceEntity.getUser(), incomeSourceEntity.getCurrency()).getDebts());
        }
        return incomeSource;
    }


}
