package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.form.BorrowUpdatingForm;
import com.stanum.skrudzh.controller.form.DebtCreationForm;
import com.stanum.skrudzh.controller.form.DebtUpdatingForm;
import com.stanum.skrudzh.controller.form.attributes.BorrowingTransactionAttributes;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.jpa.repository.BorrowRepository;
import com.stanum.skrudzh.model.dto.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class DebtsApiControllerTest extends IntegrationTest {

    @Autowired
    private DebtsApiController debtsApiController;

    @Autowired
    private TransactionsApiController transactionsApiController;

    @Autowired
    private IncomeSourcesApiController incomeSourcesApiController;

    @Autowired
    private ExpenseSourcesApiController expenseSourcesApiController;

    @Autowired
    private BorrowRepository borrowRepository;

    @Test
    public void shouldCreateDebt() {
        String name = "My credit";
        Borrow borrow = debtsApiController.usersUserIdDebtsPost(user.getId(), "", TestUtils.createDebtForm(name)).getBody().getDebt();

        BorrowEntity entity = borrowRepository.findById(borrow.getId()).get();
        Assert.assertNotNull(entity);
        Assert.assertEquals(name, entity.getName());
    }

    @Test
    public void shouldUpdateDebt() {
        String name = "My credit";
        Borrow borrow = debtsApiController.usersUserIdDebtsPost(user.getId(), "", TestUtils.createDebtForm(name)).getBody().getDebt();

        String newName = "Updated credit";
        debtsApiController.debtsIdPatch(borrow.getId(), "", updateForm(newName));

        BorrowEntity entity = borrowRepository.findById(borrow.getId()).get();
        Assert.assertNotNull(entity);
        Assert.assertEquals(newName, entity.getName());
    }

    @Test
    public void shouldFindById() {
        String name = "My credit2";
        Borrow borrow = debtsApiController.usersUserIdDebtsPost(user.getId(), "", TestUtils.createDebtForm(name)).getBody().getDebt();

        Borrow updatedBorrow = debtsApiController.debtsIdGet(borrow.getId(), "").getBody().getDebt();
        Assert.assertNotNull(updatedBorrow);
        Assert.assertEquals(name, updatedBorrow.getName());
    }

    @Test
    public void shouldFindByUser() {
        String name = "My credit3";
        Borrow borrow = debtsApiController.usersUserIdDebtsPost(user.getId(), "", TestUtils.createDebtForm(name)).getBody().getDebt();

        Debts debts = debtsApiController.indexUsersDebts(user.getId(), "").getBody();
        Assert.assertNotNull(debts);
        List<Borrow> borrows = debts.getDebts();
        Assert.assertEquals(1, borrows.size());
    }

    @Test
    public void shouldCreateDebtAndBindTransaction() {
        Transaction transaction = createTransaction();

        String name = "My credit5";
        DebtCreationForm form = TestUtils.createDebtForm(name);

        BorrowingTransactionAttributes transactionAttributes = new BorrowingTransactionAttributes();
        transactionAttributes.setId(transaction.getId());
        form.getDebt().setBorrowingTransactionAttributes(transactionAttributes);

        Borrow borrow = debtsApiController.usersUserIdDebtsPost(user.getId(), "", form).getBody().getDebt();

        Debts debts = debtsApiController.indexUsersDebts(user.getId(), "").getBody();
        Assert.assertNotNull(debts);
        List<Borrow> borrows = debts.getDebts();
        Assert.assertEquals(1, borrows.size());

        Borrow entity = borrows.get(0);
        Assert.assertNotNull(entity.getBorrowedAt());
        Assert.assertEquals(transaction.getId(), entity.getBorrowingTransactionId());
        Assert.assertNotNull(entity.getPayday());
    }

    @Test
    public void shouldThrowException_ifTransactionNotFound() {
        Transaction transaction = createTransaction();

        String name = "My credit5";
        DebtCreationForm form = TestUtils.createDebtForm(name);

        BorrowingTransactionAttributes transactionAttributes = new BorrowingTransactionAttributes();
        transactionAttributes.setId(9999L);
        form.getDebt().setBorrowingTransactionAttributes(transactionAttributes);

        AppException appException = assertThrows(
                AppException.class,
                () -> debtsApiController.usersUserIdDebtsPost(user.getId(), "", form)
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

    private DebtUpdatingForm updateForm(String name) {
        DebtUpdatingForm form = new DebtUpdatingForm();
        BorrowUpdatingForm updatingForm = new BorrowUpdatingForm();
        updatingForm.setAmountCents(90L);
        updatingForm.setName(name);
        form.debt(updatingForm);
        return form;
    }
}