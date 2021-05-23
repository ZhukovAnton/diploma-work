package com.stanum.skrudzh.service.credit;

import com.stanum.skrudzh.jpa.model.CreditEntity;
import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.jpa.model.ReminderEntity;
import com.stanum.skrudzh.model.dto.*;
import com.stanum.skrudzh.model.enums.RemindableTypeEnum;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryDtoService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.reminder.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditDtoService {

    private final ReminderService reminderService;

    private final ExpenseCategoryFinder expenseCategoryFinder;

    private final CurrencyService currencyService;

    private final CreditTransactionsService transactionsService;

    private final CreditCalculationService calculationService;

    private final ExpenseCategoryDtoService expenseCategoryDtoService;

    public Credits createCreditsResponse(Set<CreditEntity> creditEntities) {
        return new Credits(creditEntities.stream().map(this::createCreditResponse).collect(Collectors.toList()));
    }

    public Credit createCreditResponse(CreditEntity creditEntity) {
        Credit creditResponse = new Credit(creditEntity);
        BigDecimal paidAmount = calculationService.paidAmount(creditEntity);
        ExpenseCategoryEntity creditExpenseCategory = expenseCategoryFinder.findByCreditWithDeleted(creditEntity);
        ReminderEntity reminderEntity = reminderService.findBySourceIdAndType(creditEntity.getId(), RemindableTypeEnum.Credit);
        Reminder reminder = reminderEntity != null ? new Reminder(reminderEntity) : null;
        Currency currency = currencyService.getCurrencyByIsoCode(creditEntity.getCurrency());

        creditResponse.setPaidAmountCents(paidAmount.longValue());
        creditResponse.setAmountLeftCents(creditEntity.getReturnAmountCents().add(paidAmount.negate()).longValue());
        creditResponse.setExpenseCategory(creditExpenseCategory != null
                ? expenseCategoryDtoService.createExpenseCategoryResponse(creditExpenseCategory)
                : null);
        creditResponse.setExpenseCategoryId(creditExpenseCategory != null
                ? creditExpenseCategory.getId()
                : null);
        creditResponse.setCreditingTransactionId(transactionsService.getCreditTransaction(creditEntity).getId());
        creditResponse.setReminder(reminder);
        creditResponse.setCreditType(new CreditType(creditEntity.getCreditTypeEntity()));
        creditResponse.setCurrency(currency);
        return creditResponse;
    }

}
