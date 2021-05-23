package com.stanum.skrudzh.controller.expense_categories;

import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.form.ExpenseCategoryCreationForm;
import com.stanum.skrudzh.controller.response.ExpenseCategoryResponse;
import com.stanum.skrudzh.model.dto.Basket;
import com.stanum.skrudzh.model.dto.ExpenseCategories;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class ExpenseCategoriesApiTest_V1 extends AbstractExpenseCategoriesApiControllerTest {

    @Override
    protected ExpenseCategoryResponse createExpenseCategory(ExpenseCategoryCreationForm form) {
        return expenseCategoriesApiController.createExpenseCategory(getBasket().getId(), "", form).getBody();
    }

    @Test
    public void shouldGetCategoryByBasket() {
        Basket basket = basketsApiController.getBasketsForUser(user.getId(), "").getBody().getBaskets().get(0);
        ExpenseCategoryCreationForm form = TestUtils.createExpenseCategoryForm(RandomString.make());

        expenseCategoriesApiController.createExpenseCategory(basket.getId(), "", form).getBody();
        ExpenseCategories response = expenseCategoriesApiController.getExpenseCategoriesByBasket(basket.getId(), false, "").getBody();
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getExpenseCategories().size());
        Assert.assertEquals(form.getExpenseCategory().getName(), response.getExpenseCategories().get(0).getName());
    }
}
