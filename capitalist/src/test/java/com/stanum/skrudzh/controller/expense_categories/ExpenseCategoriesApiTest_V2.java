package com.stanum.skrudzh.controller.expense_categories;

import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.form.ExpenseCategoryCreationForm;
import com.stanum.skrudzh.controller.response.ExpenseCategoryResponse;
import com.stanum.skrudzh.model.dto.Basket;
import com.stanum.skrudzh.model.dto.ExpenseCategories;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
public class ExpenseCategoriesApiTest_V2 extends AbstractExpenseCategoriesApiControllerTest {

    @Override
    protected ExpenseCategoryResponse createExpenseCategory(ExpenseCategoryCreationForm form) {
        return expenseCategoriesApiController.createExpenseCategoryByUser(user.getId(), "", form).getBody();
    }

    @Test
    public void shouldGetCategoryByUser() {
        Basket basket = basketsApiController.getBasketsForUser(user.getId(), "").getBody().getBaskets().get(0);
        ExpenseCategoryCreationForm form = TestUtils.createExpenseCategoryForm(RandomString.make());
        expenseCategoriesApiController.createExpenseCategory(basket.getId(), "", form).getBody();

        ExpenseCategoryCreationForm form2 = TestUtils.createExpenseCategoryForm(RandomString.make());
        expenseCategoriesApiController.createExpenseCategory(basket.getId(), "", form2).getBody();

        ExpenseCategories result = expenseCategoriesApiController.getExpenseCategoriesByUser(user.getId(), false, "").getBody();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getExpenseCategories().size());
        Assert.assertEquals(form.getExpenseCategory().getName(), result.getExpenseCategories().get(0).getName());
    }

}