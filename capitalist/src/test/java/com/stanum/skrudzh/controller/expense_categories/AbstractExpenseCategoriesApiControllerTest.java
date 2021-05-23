package com.stanum.skrudzh.controller.expense_categories;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.BasketsApiController;
import com.stanum.skrudzh.controller.ExpenseCategoriesApiController;
import com.stanum.skrudzh.controller.form.ExpenseCategoryCreationForm;
import com.stanum.skrudzh.controller.form.ExpenseCategoryUpdatingForm;
import com.stanum.skrudzh.controller.response.ExpenseCategoryResponse;
import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.jpa.repository.ExpenseCategoriesRepository;
import com.stanum.skrudzh.model.dto.ExpenseCategories;
import com.stanum.skrudzh.model.dto.ExpenseCategory;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
public abstract class AbstractExpenseCategoriesApiControllerTest extends IntegrationTest {

    @Autowired
    protected ExpenseCategoriesApiController expenseCategoriesApiController;

    @Autowired
    protected ExpenseCategoriesRepository repository;

    @Autowired
    protected BasketsApiController basketsApiController;

    @Mock
    private HttpServletRequest httpRequest;
    @Override
    public void postInit() {
        when(httpRequest.getMethod()).thenReturn("PUT");
    }

    @Test
    public void shouldCreateCategory() {
        ExpenseCategoryCreationForm form =TestUtils.createExpenseCategoryForm(RandomString.make());

        ExpenseCategoryResponse response = createExpenseCategory(form);
        ExpenseCategoryEntity entity = repository.findById(response.getExpenseCategory().getId()).get();
        Assert.assertNotNull(entity);
        Assert.assertEquals(response.getExpenseCategory().getId(), entity.getId());
    }

    @Test
    public void shouldGetCategoryByUser() {
        ExpenseCategoryCreationForm form =TestUtils.createExpenseCategoryForm(RandomString.make());
        createExpenseCategory(form);

        ExpenseCategoryCreationForm form2 =TestUtils.createExpenseCategoryForm(RandomString.make());
        createExpenseCategory(form2).getExpenseCategory();

        ExpenseCategories result = expenseCategoriesApiController.getExpenseCategoriesByUser(user.getId(), false, "").getBody();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getExpenseCategories().size());
        Assert.assertEquals(form.getExpenseCategory().getName(), result.getExpenseCategories().get(0).getName());
    }

    @Test
    public void shouldGetCategoryById() {
        ExpenseCategoryCreationForm form = TestUtils.createExpenseCategoryForm(RandomString.make());

        ExpenseCategoryResponse response = createExpenseCategory(form);
        ExpenseCategoryResponse searchResponse = expenseCategoriesApiController.getExpenseCategoryById(
                response.getExpenseCategory().getId(), "").getBody();

        Assert.assertEquals(response, searchResponse);
    }

    @Test
    public void shouldUpdateExpenseCategory() {
        ExpenseCategoryCreationForm form = TestUtils.createExpenseCategoryForm(RandomString.make());

        ExpenseCategoryResponse response = createExpenseCategory(form);
        ExpenseCategory expenseCategory = expenseCategoriesApiController.getExpenseCategoryById(
                response.getExpenseCategory().getId(), "").getBody().getExpenseCategory();

        Assert.assertNotNull(expenseCategory);

        ExpenseCategoryUpdatingForm upform = new ExpenseCategoryUpdatingForm();
        ExpenseCategoryUpdatingForm.ExpenseCategoryUF uf = upform.new ExpenseCategoryUF();
        uf.setPrototypeKey("prot");
        uf.setMonthlyPlannedCents(100L);
        uf.setName("name");
        upform.setExpenseCategory(uf);
        expenseCategoriesApiController.updateExpenseCategory(expenseCategory.getId(), "", upform,
                request);

        ExpenseCategory expenseCategory2 = expenseCategoriesApiController.getExpenseCategoryById(
                response.getExpenseCategory().getId(), "").getBody().getExpenseCategory();

        Assert.assertEquals("prot", expenseCategory2.getPrototypeKey());

        uf.setPrototypeKey(null);
        expenseCategoriesApiController.updateExpenseCategory(expenseCategory.getId(), "", upform,
                request);

        ExpenseCategory expenseCategory3 = expenseCategoriesApiController.getExpenseCategoryById(
                response.getExpenseCategory().getId(), "").getBody().getExpenseCategory();

        Assert.assertNull(expenseCategory3.getPrototypeKey());

    }

    @Test
    public void shouldSetOrderPositions() {
        ExpenseCategoryCreationForm form1 = TestUtils.createExpenseCategoryForm(RandomString.make());
        ExpenseCategoryCreationForm form2 = TestUtils.createExpenseCategoryForm(RandomString.make());
        ExpenseCategoryCreationForm form3 = TestUtils.createExpenseCategoryForm(RandomString.make());

        List<ExpenseCategory> categories = new ArrayList<>();
        categories.add(createExpenseCategory(form1).getExpenseCategory());
        categories.add(createExpenseCategory(form2).getExpenseCategory());
        categories.add(createExpenseCategory(form3).getExpenseCategory());

        for (ExpenseCategory category : categories) {
            ExpenseCategoryResponse searchResponse = expenseCategoriesApiController.getExpenseCategoryById(
                    category.getId(), "").getBody();
            Assert.assertNotNull(searchResponse.getExpenseCategory().getRowOrder());
        }
    }

    protected abstract ExpenseCategoryResponse createExpenseCategory(ExpenseCategoryCreationForm form);

}
