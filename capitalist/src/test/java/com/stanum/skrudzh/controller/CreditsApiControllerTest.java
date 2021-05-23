package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.form.CreditCreationForm;
import com.stanum.skrudzh.controller.form.CreditUpdatingForm;
import com.stanum.skrudzh.controller.form.attributes.CreditingTransactionAttributes;
import com.stanum.skrudzh.controller.response.CreditResponse;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.jpa.model.CreditEntity;
import com.stanum.skrudzh.jpa.repository.CreditRepository;
import com.stanum.skrudzh.model.dto.Credit;
import com.stanum.skrudzh.model.dto.ExpenseSource;
import com.stanum.skrudzh.model.dto.IncomeSource;
import com.stanum.skrudzh.model.dto.Transaction;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CreditsApiControllerTest extends IntegrationTest {

    @Autowired
    private CreditsApiController creditsApiController;

    @Autowired
    private TransactionsApiController transactionsApiController;

    @Autowired
    private IncomeSourcesApiController incomeSourcesApiController;

    @Autowired
    private ExpenseSourcesApiController expenseSourcesApiController;

    @Autowired
    private CreditRepository creditRepository;

    @Test
    public void shouldCreateCredit() {
        String name = "My Credit";
        CreditCreationForm form = TestUtils.createCreditForm(name);
        CreditResponse creditResponse = creditsApiController.createCreditByUser(user.getId(), "", form).getBody();
        Assert.assertNotNull(creditResponse);
        Assert.assertEquals(creditResponse.getCredit().getName(), name);
    }

    @Test
    public void shouldUpdateCredit() {
        String name = "My Credit";
        CreditCreationForm form = TestUtils.createCreditForm(name);
        Credit credit = creditsApiController.createCreditByUser(user.getId(), "", form).getBody().getCredit();

        String newName = "New Name";
        creditsApiController.updateCreditById(credit.getId(), "", updateForm(newName, 50L));

        CreditEntity updatedCredit = creditRepository.findById(credit.getId()).get();

        Assert.assertNotNull(updatedCredit);
        Assert.assertEquals(updatedCredit.getName(), newName);
        Assert.assertEquals(updatedCredit.getAmountCents().longValue(), 50L);
    }

    @Test
    public void shouldFindByUser() {
        String name = "Search Credit";
        String name2 = "Search Credit2";
        CreditCreationForm form = TestUtils.createCreditForm(name);
        CreditCreationForm form2 = TestUtils.createCreditForm(name2);
        creditsApiController.createCreditByUser(user.getId(), "", form);
        creditsApiController.createCreditByUser(user.getId(), "", form2);

        List<Credit> credits = creditsApiController.indexUserCredits(user.getId(), "").getBody().getCredits();
        Assert.assertEquals(2, credits.size());
    }

    @Test
    public void shouldFindCreditById() {
        String name = "My Credit 3";
        CreditCreationForm form = TestUtils.createCreditForm(name);
        Credit credit = creditsApiController.createCreditByUser(user.getId(), "", form).getBody().getCredit();

        CreditResponse response = creditsApiController.creditsIdGet(credit.getId(), "").getBody();
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getCredit());
    }

    @Test
    public void shouldDestroyCredit() {
        String name = "My Credit 3";
        CreditCreationForm form = TestUtils.createCreditForm(name);
        Credit credit = creditsApiController.createCreditByUser(user.getId(), "", form).getBody().getCredit();

        CreditResponse response = creditsApiController.creditsIdGet(credit.getId(), "").getBody();
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getCredit());
        Assert.assertNull(response.getCredit().getDeletedAt());

        creditsApiController.destroyCreditById(credit.getId(), "", false);

        CreditResponse destroyedCredit = creditsApiController.creditsIdGet(credit.getId(), "").getBody();
        Assert.assertNotNull(destroyedCredit.getCredit().getDeletedAt());
    }

    @Test
    public void shouldCreateCreditAndBindTransaction() {
        Transaction transaction = createTransaction();

        String name = "My Credit 10";
        CreditCreationForm form = TestUtils.createCreditForm(name);

        CreditingTransactionAttributes attributes = new CreditingTransactionAttributes();
        attributes.setId(transaction.getId());
        form.getCredit().setCreditingTransactionAttributes(attributes);

        Credit credit = creditsApiController.createCreditByUser(user.getId(), "", form).getBody().getCredit();

        Credit response = creditsApiController.creditsIdGet(credit.getId(), "").getBody().getCredit();
        Assert.assertNotNull(response);

        Transaction bindedTransaction = transactionsApiController.getTransactionById(transaction.getId(), "").getBody().getTransaction();
        Assert.assertNotNull(bindedTransaction.getCredit());
        Assert.assertEquals(credit.getId(), bindedTransaction.getCredit().getId());
    }

    @Test
    public void shouldThrowNotFoundExceptionIfTransactionNotFound() {
        String name = "My Credit 10";
        CreditCreationForm form = TestUtils.createCreditForm(name);

        CreditingTransactionAttributes attributes = new CreditingTransactionAttributes();
        attributes.setId(9999L);
        form.getCredit().setCreditingTransactionAttributes(attributes);

        AppException appException = assertThrows(
                AppException.class,
                () -> creditsApiController.createCreditByUser(user.getId(), "", form)
        );
    }

    private Transaction createTransaction() {
        IncomeSource incomeSource = incomeSourcesApiController.createIncomeSource(user.getId(),
                TestUtils.createIncomeForm("RUB", "Income Source tr", 1000L),
                "").getBody().getIncomeSource();

        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", "Expense Source", 100L),
                user.getId()).getBody().getExpenseSource();

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTransactionForm(50L, incomeSource.getId(), expenseSource.getId())).getBody().getTransaction();

        return transaction;
    }

    private CreditUpdatingForm updateForm(String name, Long amountCents) {
        CreditUpdatingForm form = new CreditUpdatingForm();
        CreditUpdatingForm.CreditUF uf = form.new CreditUF();
        uf.setAmountCents(amountCents);
        uf.setName(name);
        form.setCredit(uf);
        return form;
    }
}