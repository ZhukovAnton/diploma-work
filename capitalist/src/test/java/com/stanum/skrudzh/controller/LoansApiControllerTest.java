package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.form.BorrowUpdatingForm;
import com.stanum.skrudzh.controller.form.LoanCreationForm;
import com.stanum.skrudzh.controller.form.LoanUpdatingForm;
import com.stanum.skrudzh.controller.form.attributes.BorrowingTransactionAttributes;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.jpa.repository.BorrowRepository;
import com.stanum.skrudzh.model.dto.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class LoansApiControllerTest extends IntegrationTest {

    @Autowired
    private LoansApiController loansApiController;

    @Autowired
    private TransactionsApiController transactionsApiController;

    @Autowired
    private IncomeSourcesApiController incomeSourcesApiController;

    @Autowired
    private ExpenseSourcesApiController expenseSourcesApiController;

    @Autowired
    private BorrowRepository borrowRepository;

    @Test
    public void shouldCreateLoan() {
        String loanName = "Loan name";
        Borrow borrow = loansApiController.createLoan(user.getId(), "", TestUtils.createLoanForm(loanName)).getBody().getLoan();

        BorrowEntity entity = borrowRepository.findById(borrow.getId()).get();
        Assert.assertNotNull(entity);
        Assert.assertEquals(loanName, entity.getName());
    }

    @Test
    public void shouldUpdateLoan() {
        String loanName = "Currrent Loan name";
        Borrow borrow = loansApiController.createLoan(user.getId(), "", TestUtils.createLoanForm(loanName)).getBody().getLoan();

        String updateName = "New Loan name";
        loansApiController.updateLoanById(borrow.getId(), "", updateForm(updateName));

        BorrowEntity entity = borrowRepository.findById(borrow.getId()).get();
        Assert.assertNotNull(entity);
        Assert.assertEquals(updateName, entity.getName());
    }

    @Test
    public void shouldGetLoanById() {
        String loanName = "Loan name2";
        Borrow borrow = loansApiController.createLoan(user.getId(), "", TestUtils.createLoanForm(loanName)).getBody().getLoan();

        Borrow result = loansApiController.getLoanById(borrow.getId(), "").getBody().getLoan();

        Assert.assertNotNull(result);
        Assert.assertEquals(loanName, result.getName());
    }

    @Test
    public void shouldGetLoansByUser() {
        String loanName = "Loan name4";
        String loanName2 = "Loan name5";
        Borrow borrow = loansApiController.createLoan(user.getId(), "", TestUtils.createLoanForm(loanName)).getBody().getLoan();
        Borrow borrow2 = loansApiController.createLoan(user.getId(), "", TestUtils.createLoanForm(loanName2)).getBody().getLoan();

        Loans result = loansApiController.indexLoansByUserId(user.getId(), "").getBody();

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getLoans().size());
    }

    @Test
    public void shouldDestroyLoan() {
        String loanName = "Loan name2";
        Borrow borrow = loansApiController.createLoan(user.getId(), "", TestUtils.createLoanForm(loanName)).getBody().getLoan();

        loansApiController.destroyLoan(borrow.getId(), "", false);

        BorrowEntity entity = borrowRepository.findById(borrow.getId()).get();
        Assert.assertNotNull(entity);
        Assert.assertEquals(loanName, entity.getName());
        Assert.assertNotNull(entity.getDeletedAt());
    }

    @Test
    public void shouldCreateLoanAndBindTransaction() {
        String loanName = "Loan name2";
        Transaction transaction = createTransaction();

        LoanCreationForm form = TestUtils.createLoanForm(loanName);
        BorrowingTransactionAttributes attributes = new BorrowingTransactionAttributes();
        attributes.setId(transaction.getId());
        form.getLoan().setBorrowingTransactionAttributes(attributes);

        Borrow borrow = loansApiController.createLoan(user.getId(), "", form).getBody().getLoan();

        Borrow loan = loansApiController.getLoanById(borrow.getId(), "").getBody().getLoan();
        Assert.assertNotNull(loanName);
        Assert.assertEquals(loanName, loan.getName());
        Assert.assertNotNull(loan.getBorrowedAt());
        Assert.assertEquals(transaction.getId(), loan.getBorrowingTransactionId());
        Assert.assertNotNull(loan.getPayday());
    }

    @Test
    public void shouldThrowException__ifTransactionNotFound() {
        String loanName = "Loan name3";
        Transaction transaction = createTransaction();

        LoanCreationForm form = TestUtils.createLoanForm(loanName);
        BorrowingTransactionAttributes attributes = new BorrowingTransactionAttributes();
        attributes.setId(9999L);
        form.getLoan().setBorrowingTransactionAttributes(attributes);

        AppException appException = assertThrows(
                AppException.class,
                () -> loansApiController.createLoan(user.getId(), "", form).getBody().getLoan()
        );
    }

    private Transaction createTransaction() {
        IncomeSource incomeSource = incomeSourcesApiController.createIncomeSource(user.getId(),
                TestUtils.createIncomeForm("RUB", "Income Source tr(Loan test)", 1000L),
                "").getBody().getIncomeSource();

        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", "Expense Source(Loan test)", 100L),
                user.getId()).getBody().getExpenseSource();

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTransactionForm(50L, incomeSource.getId(), expenseSource.getId())).getBody().getTransaction();

        return transaction;
    }

    private LoanUpdatingForm updateForm(String name) {
        LoanUpdatingForm form = new LoanUpdatingForm();
        BorrowUpdatingForm uf = new BorrowUpdatingForm();
        uf.setName(name);
        uf.setAmountCents(90L);
        uf.setBorrowedAt(LocalDateTime.now());
        form.setLoan(uf);
        return form;
    }
}