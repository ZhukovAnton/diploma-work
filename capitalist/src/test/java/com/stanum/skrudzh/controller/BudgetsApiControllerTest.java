package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.form.IncomeSourceCreationForm;
import com.stanum.skrudzh.controller.response.IncomeSourceResponse;
import com.stanum.skrudzh.model.dto.Budget;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class BudgetsApiControllerTest extends IntegrationTest {

    @Autowired
    private BudgetsApiController budgetsApiController;

    @Autowired
    private IncomeSourcesApiController incomeSourcesApiController;

    @Test
    public void shouldGetBudget() {
        Budget budget = budgetsApiController.usersUserIdBudgetGet(user.getId(), "").getBody().getBudget();
        Assert.assertEquals(0L, budget.getActivesAmountCents().longValue());
        Assert.assertEquals(0L, budget.getIncomesAtPeriodCents().longValue());
        Assert.assertEquals(0L, budget.getExpensesAtPeriodCents().longValue());
        Assert.assertEquals(0L, budget.getExpensesAtPeriodCents().longValue());
    }

    @Test
    public void shouldGetBudgetWithIncome() {
        Long incomePlanned = 1000L;
        createIncomeSource("RUB", "My Income Source", incomePlanned);

        Budget budget = budgetsApiController.usersUserIdBudgetGet(user.getId(), "").getBody().getBudget();
        Assert.assertEquals(0L, budget.getActivesAmountCents().longValue());
        Assert.assertEquals(0L, budget.getIncomesAtPeriodCents().longValue());
        Assert.assertEquals(0L, budget.getExpensesAtPeriodCents().longValue());
        Assert.assertEquals(0L, budget.getExpensesAtPeriodCents().longValue());
        Assert.assertEquals(incomePlanned, budget.getIncomesPlannedAtPeriodCents());
    }

    private void createIncomeSource(String currency, String sourceName, Long monthlyCents) {
        IncomeSourceCreationForm form = TestUtils.createIncomeForm(currency, sourceName, monthlyCents);
        ResponseEntity<IncomeSourceResponse> response = incomeSourcesApiController.createIncomeSource(user.getId(), form, null);
    }
}
