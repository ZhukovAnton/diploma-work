package com.stanum.skrudzh.service.budget;

import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.Budget;
import com.stanum.skrudzh.model.dto.Currency;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.service.active.ActiveCalculationService;
import com.stanum.skrudzh.service.basket.BasketFinder;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryCalculationService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceCalculationService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceCalculationService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetCalculationService {

    private final UserUtil userUtil;

    private final CurrencyService currencyService;

    private final BasketFinder basketFinder;

    private final IncomeSourceFinder incomeSourceFinder;

    private final IncomeSourceCalculationService incomeSourceCalculationService;

    private final ExpenseSourceFinder expenseSourceFinder;

    private final ExpenseSourceCalculationService expenseSourceCalculationService;

    private final ExpenseCategoryCalculationService expenseCategoryCalculationService;

    private final ActiveCalculationService activeCalculationService;

    public Budget calculateUsersBudget(Long userId) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        Budget budget = new Budget();
        budget.setId(userEntity.getId());

        Currency currency = currencyService.getCurrencyByIsoCode(userEntity.getDefaultCurrency());
        budget.setCurrency(currency);

        Set<IncomeSourceEntity> incomeSourceEntities = incomeSourceFinder.findAllByUser(userEntity);
        budget.setIncomesAtPeriodCents(incomeSourceCalculationService
                .incomesGotAtPeriod(userEntity, incomeSourceEntities, userEntity.getDefaultPeriod()).longValue());
        budget.setIncomesPlannedAtPeriodCents(incomeSourceCalculationService
                .incomesPlannedAtPeriod(userEntity, incomeSourceEntities, userEntity.getDefaultPeriod()).longValue());

        Set<ExpenseSourceEntity> realExpenseSourceEntities = expenseSourceFinder
                .findExpenseSourcesByUserAndIsVirtual(userEntity, false);
        budget.setExpenseSourcesAmountCents(expenseSourceCalculationService
                .expenseSourcesSummaryBalance(realExpenseSourceEntities, userEntity.getDefaultCurrency()).longValue());

        BasketEntity safeBasket = basketFinder.findBasketByUserAndType(userEntity, BasketTypeEnum.safe);
        BasketEntity riskBasket = basketFinder.findBasketByUserAndType(userEntity, BasketTypeEnum.risk);
        budget.setSafeActivesAmountCents(activeCalculationService.activesSummaryBalance(safeBasket).longValue());
        budget.setRiskActivesAmountCents(activeCalculationService.activesSummaryBalance(riskBasket).longValue());
        //May be will be better to add in BigDecimal type
        budget.setActivesAmountCents(budget.getRiskActivesAmountCents() + budget.getSafeActivesAmountCents());
        budget.setExpensesAtPeriodCents(expenseCategoryCalculationService
                .expensesAtPeriod(userEntity, userEntity.getDefaultPeriod()).longValue());
        budget.setExpensesPlannedAtPeriodCents(expenseCategoryCalculationService
                .expensesPlannedAtPeriod(userEntity).longValue());
        return budget;
    }
}
