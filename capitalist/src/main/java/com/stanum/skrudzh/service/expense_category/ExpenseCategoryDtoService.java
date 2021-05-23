package com.stanum.skrudzh.service.expense_category;

import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.jpa.model.ReminderEntity;
import com.stanum.skrudzh.model.dto.ExpenseCategories;
import com.stanum.skrudzh.model.dto.ExpenseCategory;
import com.stanum.skrudzh.model.dto.Reminder;
import com.stanum.skrudzh.model.enums.RemindableTypeEnum;
import com.stanum.skrudzh.service.borrow.BorrowManagementService;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.reminder.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryDtoService {

    private final ExpenseCategoryCalculationService calculationService;

    private final CurrencyService currencyService;

    private final ReminderService reminderService;

    private final BorrowManagementService borrowManagementService;

    public ExpenseCategories createExpenseCategoriesResponse(Set<ExpenseCategoryEntity> expenseCategoryEntities) {
        return new ExpenseCategories(
                expenseCategoryEntities.stream()
                        .map(this::createExpenseCategoryResponse)
                        .collect(Collectors.toList()));
    }

    public ExpenseCategory createExpenseCategoryResponse(ExpenseCategoryEntity expenseCategoryEntity) {
        ExpenseCategory expenseCategory = new ExpenseCategory(expenseCategoryEntity);
        BigDecimal plannedAtPeriod = calculationService.getPlannedAtDefaultPeriod(expenseCategoryEntity);
        expenseCategory.setCurrency(currencyService
                .getCurrencyByIsoCode(expenseCategoryEntity.getCurrency()));
        expenseCategory.setPlannedCentsAtPeriod(plannedAtPeriod != null ? plannedAtPeriod.longValue() : null);
        expenseCategory.setSpentCentsAtPeriod(calculationService.spentAtDefaultPeriod(expenseCategoryEntity).longValue());
        ReminderEntity reminderEntity = reminderService.findBySourceIdAndType(expenseCategoryEntity.getId(), RemindableTypeEnum.ExpenseCategory);
        expenseCategory.setReminder(reminderEntity != null ? new Reminder(reminderEntity) : null);
        if (expenseCategoryEntity.getIsBorrow()) {
            expenseCategory.setWaitingLoans(borrowManagementService
                    .getWaitingLoans(expenseCategoryEntity.getBasket().getUser(), expenseCategoryEntity.getCurrency()).getLoans());
        }
        return expenseCategory;
    }


}
