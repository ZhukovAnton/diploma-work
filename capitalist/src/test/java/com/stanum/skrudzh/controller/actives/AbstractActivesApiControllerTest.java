package com.stanum.skrudzh.controller.actives;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.ActivesApiController;
import com.stanum.skrudzh.controller.ExpenseSourcesApiController;
import com.stanum.skrudzh.controller.IncomeSourcesApiController;
import com.stanum.skrudzh.controller.TransactionsApiController;
import com.stanum.skrudzh.controller.form.ActiveCreationForm;
import com.stanum.skrudzh.controller.form.ActiveUpdatingForm;
import com.stanum.skrudzh.controller.form.attributes.ActiveTransactionAttributes;
import com.stanum.skrudzh.controller.response.ActiveResponse;
import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.repository.ActiveRepository;
import com.stanum.skrudzh.model.dto.*;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public abstract class AbstractActivesApiControllerTest extends IntegrationTest {

    @Autowired
    protected ActivesApiController activesApiController;

    @Autowired
    protected ActiveRepository activeRepository;

    @Autowired
    private TransactionsApiController transactionsApiController;

    @Autowired
    private IncomeSourcesApiController incomeSourcesApiController;

    @Autowired
    private ExpenseSourcesApiController expenseSourcesApiController;

    @Test
    public void shouldCreateActive() {
        String name = RandomString.make();
        ActiveCreationForm form = TestUtils.createActiveForm(name);
        ActiveResponse response = createActive(form);
        Assert.assertNotNull(response);
        Assert.assertEquals(name, response.getActive().getName());
    }

    @Test
    @Disabled
    public void shouldCreateActiveOrder() {
        String name1 = RandomString.make();
        String name2 = RandomString.make();
        String name3 = RandomString.make();
        ActiveCreationForm form1 = TestUtils.createActiveForm(name1);
        ActiveCreationForm form2 = TestUtils.createActiveForm(name2);
        ActiveCreationForm form3 = TestUtils.createActiveForm(name3);
        ActiveResponse response = createActive(form1);
        ActiveResponse response2 = createActive(form2);
        ActiveResponse response3 = createActive(form3);

        List<Active> actives = activesApiController.getActivesByUser(user.getId(), "").getBody().getActives();

        for(Active active : actives) {
            Assert.assertNotNull(active.getRowOrder());
        }
    }

    @Test
    public void shouldUpdateActive() {
        String name = RandomString.make();
        ActiveCreationForm form = TestUtils.createActiveForm(name);
        ActiveResponse response = createActive(form);

        String newName = RandomString.make();
        ActiveUpdatingForm updatingForm = updateForm(newName);

        activesApiController.activesIdPatch(response.getActive().getId(), "", updatingForm).getBody();

        ActiveEntity savedActive = activeRepository.findById(response.getActive().getId()).get();
        Assert.assertNotNull(savedActive);
        Assert.assertEquals(newName, savedActive.getName());
    }

    @Test
    public void shouldDeleteActive() {
        String name = RandomString.make();
        ActiveCreationForm form = TestUtils.createActiveForm(name);
        ActiveResponse response = activesApiController.basketsBasketIdActivesPost(getBasket().getId(), "", form).getBody();

        activesApiController.deleteActiveById(response.getActive().getId(), "", false);

        Optional<ActiveEntity> savedActive = activeRepository.findById(response.getActive().getId());
        Assert.assertNotNull(savedActive.get().getDeletedAt());
    }

    @Test
    public void shouldGetActiveById() {
        String name = RandomString.make();
        ActiveCreationForm form = TestUtils.createActiveForm(name);
        ActiveResponse response = activesApiController.basketsBasketIdActivesPost(getBasket().getId(), "", form).getBody();

        Active active = activesApiController.getActiveById(response.getActive().getId(), "").getBody().getActive();
        Assert.assertNotNull(active);
        Assert.assertEquals(name, active.getName());
    }

    @Test
    public void shouldCreateActiveAndBindTransaction() {
        Transaction transaction = createTransaction();

        String name = RandomString.make();
        ActiveCreationForm form = TestUtils.createActiveForm(name);

        ActiveTransactionAttributes attributes = new ActiveTransactionAttributes();
        attributes.setId(transaction.getId());
        form.getActive().setActiveTransactionAttributes(attributes);
        ActiveResponse response = createActive(form);

        Optional<ActiveEntity> byId = activeRepository.findById(response.getActive().getId());

        Actives actives = activesApiController.getActivesByUser(user.getId(), "").getBody();
        Assert.assertNotNull(actives);
        Assert.assertEquals(1, actives.getActives().size());
    }

    private Transaction createTransaction() {
        IncomeSource incomeSource = incomeSourcesApiController.createIncomeSource(user.getId(),
                TestUtils.createIncomeForm("RUB", RandomString.make(), 1000L),
                "").getBody().getIncomeSource();

        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", RandomString.make(), 100L),
                user.getId()).getBody().getExpenseSource();

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTransactionForm(50L, incomeSource.getId(), expenseSource.getId())).getBody().getTransaction();

        return transaction;
    }

    private ActiveUpdatingForm updateForm(String name) {
        ActiveUpdatingForm form = new ActiveUpdatingForm();
        ActiveUpdatingForm.ActiveUF activeUF = form.new ActiveUF();
        activeUF.setName(name);
        form.setActive(activeUF);
        return form;
    }

    protected abstract ActiveResponse createActive(ActiveCreationForm form);

    protected abstract void setIosBuild();

}