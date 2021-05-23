package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.form.ExpenseSourceCreationForm;
import com.stanum.skrudzh.controller.form.ExpenseSourceUpdatingForm;
import com.stanum.skrudzh.controller.form.saltedge.AccountConnectionAttributes;
import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.jpa.repository.ExpenseSourcesRepository;
import com.stanum.skrudzh.model.dto.ExpenseSource;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.saltedge.account_connection.AccountConnectionManagementService;
import com.stanum.skrudzh.utils.TimeUtil;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
public class ExpenseSourcesApiControllerTest extends IntegrationTest {

    @Autowired
    private ExpenseSourcesApiController expenseSourcesApiController;

    @Autowired
    private ExpenseSourcesRepository expenseSourcesRepository;

    @Mock
    private HttpServletRequest httpRequest;

    @Autowired
    private AccountConnectionManagementService accountConnectionManagementService;

    @Test
    public void shouldCreateExpenseSourceTest() {
        String name = RandomString.make();
        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("RUB", name, 100L), user.getId()).getBody().getExpenseSource();
        ExpenseSourceEntity expenseSourceEntity = expenseSourcesRepository.findById(expenseSource.getId()).get();
        Assert.assertNotNull(expenseSourceEntity);
        Assert.assertEquals(name, expenseSourceEntity.getName());
    }

    @Test
    public void shouldUpdateExpenseSourceTest() {
        when(httpRequest.getMethod()).thenReturn("PATCH");
        String name = RandomString.make();
        ExpenseSource expenseSource = expenseSourcesApiController
                .usersUserIdExpenseSourcesPost("",
                        TestUtils.createExpenseForm("RUB", name, 100L), user.getId())
                .getBody().getExpenseSource();

        String newName = RandomString.make();
        expenseSourcesApiController.updateExpenseSourceById(expenseSource.getId(), "", updateForm(newName), httpRequest);
        ExpenseSourceEntity expenseSourceEntity = expenseSourcesRepository.findById(expenseSource.getId()).get();
        Assert.assertNotNull(expenseSourceEntity);
        Assert.assertEquals(newName, expenseSourceEntity.getName());
    }

    @Test
    public void shouldUpdateExpenseSourcePrototypeKeyTest() {
        when(httpRequest.getMethod()).thenReturn("PUT");
        String name = RandomString.make();
        ExpenseSource expenseSource = expenseSourcesApiController
                .usersUserIdExpenseSourcesPost("",
                        TestUtils.createExpenseForm("RUB", name, 100L), user.getId())
                .getBody().getExpenseSource();

        String newName = RandomString.make();
        ExpenseSourceUpdatingForm payload = updateForm(newName);

        String prototype = RandomString.make();
        payload.getExpenseSource().setPrototypeKey(prototype);
        expenseSourcesApiController.updateExpenseSourceById(expenseSource.getId(), "", payload, httpRequest);
        ExpenseSourceEntity expenseSourceEntity = expenseSourcesRepository.findById(expenseSource.getId()).get();
        Assert.assertNotNull(expenseSourceEntity);
        Assert.assertEquals(newName, expenseSourceEntity.getName());
        Assert.assertEquals(prototype, expenseSourceEntity.getPrototypeKey());

        payload.getExpenseSource().setPrototypeKey(null);
        expenseSourcesApiController.updateExpenseSourceById(expenseSource.getId(), "", payload, httpRequest);
        ExpenseSourceEntity expenseSourceEntity2 = expenseSourcesRepository.findById(expenseSource.getId()).get();
        Assert.assertNotNull(expenseSourceEntity2);
        Assert.assertNull(expenseSourceEntity2.getPrototypeKey());
    }

    /**
     * Update expense source for userId = 32635, payload = ExpenseSourceUpdatingForm(
     * expenseSource=ExpenseSourceUpdatingForm.ExpenseSourceUF(name=Альфа-банк,
     * iconUrl=https://d1uuj3mi6rzwpm.cloudfront.net/logos/providers/xf/placeholder_global.svg, amountCents=0, creditLimitCents=0,
     * cardType=null, rowOrderPosition=null, maxFetchInterval=null, accountConnectionAttributes=AccountConnectionAttributes(id=69,
     * accountId=null, connectionId=78, destroy=true)))
     */
    @Test
    @Disabled
    public void shouldDisconnectExpenseSourceTest() {
        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesPost(
                "", TestUtils.createExpenseForm("RUB", RandomString.make(), 100L), user.getId())
                .getBody().getExpenseSource();

        AccountConnectionEntity accountConnectionEntity = new AccountConnectionEntity();
        accountConnectionEntity.setCreatedAt(TimeUtil.now());
        accountConnectionEntity.setSourceId(expenseSource.getId());
        accountConnectionEntity.setSourceType(EntityTypeEnum.ExpenseSource);

        accountConnectionManagementService.save(accountConnectionEntity);
        ExpenseSourceEntity savedExpenseSource = expenseSourcesRepository.findById(expenseSource.getId()).get();
        savedExpenseSource.setAccountConnectionEntity(accountConnectionEntity);
        expenseSourcesRepository.save(savedExpenseSource);


        Mockito.reset(httpRequest);
        when(httpRequest.getMethod()).thenReturn("PUT");
        String name = RandomString.make();
        ExpenseSourceUpdatingForm form = new ExpenseSourceUpdatingForm();
        ExpenseSourceUpdatingForm.ExpenseSourceUF cf = form.new ExpenseSourceUF();
        cf.setAmountCents(0L);
        cf.setName(name);
        form.setExpenseSource(cf);

        AccountConnectionAttributes attributes = new AccountConnectionAttributes();
        attributes.setDestroy(true);
        attributes.setId(1L);
        attributes.setConnectionId(1L);
        cf.setAccountConnectionAttributes(attributes);

        expenseSourcesApiController.updateExpenseSourceById(expenseSource.getId(),"", form, httpRequest);

//        ExpenseSourceEntity expenseSourceEntity = expenseSourcesRepository.findById(expenseSource.getId()).get();
//        Assert.assertNotNull(expenseSourceEntity);
    }

    @Test
    public void shouldGetExpenseSourceById() {
        String name = RandomString.make();
        ExpenseSource result = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("RUB", name, 100L), user.getId()).getBody().getExpenseSource();

        ExpenseSource expenseSource = expenseSourcesApiController.getExpenseSourceById(result.getId(), "").getBody().getExpenseSource();
        Assert.assertNotNull(expenseSource);
        Assert.assertEquals(name, expenseSource.getName());
    }

    @Test
    public void shouldGetExpenseSourceWithCurrency() {
        String name = RandomString.make();
        ExpenseSource result1 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("RUB", name, 100L), user.getId()).getBody().getExpenseSource();
        ExpenseSource result2 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("RUB", name, 100L), user.getId()).getBody().getExpenseSource();
        ExpenseSource result3 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("CAD", name, 100L), user.getId()).getBody().getExpenseSource();
        ExpenseSource result4 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("CAD", name, 100L), user.getId()).getBody().getExpenseSource();

        List<ExpenseSource> expenseSources = expenseSourcesApiController.getExpenseSourcesWithCurrency(user.getId(), "CAD", "").getBody().getExpenseSources();
        Assert.assertNotNull(expenseSources);
        Assert.assertEquals(2, expenseSources.size());
    }

    @Test
    public void shouldDeleteExpenseSource() {
        String name = RandomString.make();
        ExpenseSource result1 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("RUB", name, 100L), user.getId()).getBody().getExpenseSource();
        ExpenseSource result2 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("RUB", name, 100L), user.getId()).getBody().getExpenseSource();
        ExpenseSource result3 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("CAD", name, 100L), user.getId()).getBody().getExpenseSource();
        ExpenseSource result4 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("CAD", name, 100L), user.getId()).getBody().getExpenseSource();

        List<ExpenseSource> expenseSources = expenseSourcesApiController.getExpenseSourcesWithCurrency(user.getId(), "CAD", "").getBody().getExpenseSources();
        Assert.assertNotNull(expenseSources);
        Assert.assertEquals(2, expenseSources.size());

        expenseSourcesApiController.expenseSourcesIdDelete(result4.getId(), "", false);
        List<ExpenseSource> expenseSources2 = expenseSourcesApiController.getExpenseSourcesWithCurrency(user.getId(), "CAD", "").getBody().getExpenseSources();
        Assert.assertNotNull(expenseSources2);
        Assert.assertEquals(1, expenseSources2.size());
    }

    @Test
    public void shouldGetFirstExpenseSource() {
        String name = RandomString.make();
        ExpenseSource result1 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("RUB", name, 100L), user.getId()).getBody().getExpenseSource();
        ExpenseSource result2 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("CAD", name, 100L), user.getId()).getBody().getExpenseSource();
        ExpenseSource result3 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("CAD", name, 100L), user.getId()).getBody().getExpenseSource();
        ExpenseSource result4 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", TestUtils.createExpenseForm("CAD", name, 100L), user.getId()).getBody().getExpenseSource();

        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesFirstGet(user.getId(), "CAD", "", false).getBody().getExpenseSource();
        Assert.assertNotNull(expenseSource);
        Assert.assertEquals(result2.getId(), expenseSource.getId());

    }

    @Test
    public void shouldFillProviderCodes() {
        String name = RandomString.make();
        ExpenseSourceCreationForm form = TestUtils.createExpenseForm("RUB", name, 100L);
        form.getExpenseSource().setPrototypeKey("sberbank_ru");
        ExpenseSource result = expenseSourcesApiController.usersUserIdExpenseSourcesPost("", form, user.getId()).getBody().getExpenseSource();

        ExpenseSource expenseSource = expenseSourcesApiController.getExpenseSourceById(result.getId(), "").getBody().getExpenseSource();
        Assert.assertNotNull(expenseSource);
        Assert.assertEquals(name, expenseSource.getName());
        Assert.assertEquals(2, expenseSource.getProviderCodes().size());
    }

    @Test
    public void shouldUpdateExpenseSource_CurrencyTest() {
        when(httpRequest.getMethod()).thenReturn("PUT");
        String name = RandomString.make();
        ExpenseSource expenseSource = expenseSourcesApiController
                .usersUserIdExpenseSourcesPost("",
                        TestUtils.createExpenseForm("RUB", name, 0L), user.getId())
                .getBody().getExpenseSource();

        String newName = RandomString.make();

        ExpenseSourceUpdatingForm payload = updateForm(newName);
        payload.getExpenseSource().setCurrency("USD");

        expenseSourcesApiController.updateExpenseSourceById(expenseSource.getId(), "", payload, httpRequest);
        ExpenseSourceEntity expenseSourceEntity = expenseSourcesRepository.findById(expenseSource.getId()).get();
        Assert.assertNotNull(expenseSourceEntity);
        Assert.assertEquals(newName, expenseSourceEntity.getName());
        Assert.assertEquals("USD", expenseSourceEntity.getCurrency());
    }

    @Test
    public void shouldNOTUpdateExpenseSource_Currency_ifHasTransactionsTest() {
        when(httpRequest.getMethod()).thenReturn("PATCH");
        String name = RandomString.make();
        ExpenseSource expenseSource = expenseSourcesApiController
                .usersUserIdExpenseSourcesPost("",
                        TestUtils.createExpenseForm("RUB", name, 10L), user.getId())
                .getBody().getExpenseSource();

        String newName = RandomString.make();

        ExpenseSourceUpdatingForm payload = updateForm(newName);
        payload.getExpenseSource().setCurrency("USD");

        expenseSourcesApiController.updateExpenseSourceById(expenseSource.getId(), "", payload, httpRequest);
        ExpenseSourceEntity expenseSourceEntity = expenseSourcesRepository.findById(expenseSource.getId()).get();
        Assert.assertNotNull(expenseSourceEntity);
        Assert.assertEquals(newName, expenseSourceEntity.getName());
        Assert.assertEquals("RUB", expenseSourceEntity.getCurrency());
    }

    @Test
    public void shouldFillHasTransactionsAttribute() {
        String name = RandomString.make();
        ExpenseSource result = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", name, 100L), user.getId()).getBody().getExpenseSource();

        ExpenseSource expenseSource = expenseSourcesApiController.getExpenseSourceById(result.getId(), "").getBody().getExpenseSource();
        Assert.assertNotNull(expenseSource);
        Assert.assertEquals(name, expenseSource.getName());
        Assert.assertTrue(expenseSource.getHasTransactions());

        ExpenseSource result2 = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", name, 0L), user.getId()).getBody().getExpenseSource();

        ExpenseSource expenseSource2 = expenseSourcesApiController.getExpenseSourceById(result2.getId(), "").getBody().getExpenseSource();
        Assert.assertNotNull(expenseSource2);
        Assert.assertEquals(name, expenseSource2.getName());
        Assert.assertFalse(expenseSource2.getHasTransactions());
    }

    private ExpenseSourceUpdatingForm updateForm(String name) {
        ExpenseSourceUpdatingForm form = new ExpenseSourceUpdatingForm();
        ExpenseSourceUpdatingForm.ExpenseSourceUF uf = form.new ExpenseSourceUF();
        uf.setAmountCents(20L);
        uf.setName(name);
        form.setExpenseSource(uf);
        return form;
    }
}
