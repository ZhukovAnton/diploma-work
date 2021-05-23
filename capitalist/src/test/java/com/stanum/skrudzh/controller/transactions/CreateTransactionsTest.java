package com.stanum.skrudzh.controller.transactions;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.config.Limits;
import com.stanum.skrudzh.controller.*;
import com.stanum.skrudzh.controller.form.*;
import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.repository.CreditRepository;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.dto.*;
import com.stanum.skrudzh.model.enums.TransactionTypeEnum;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.transaction.TransactionFinder;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.Mockito.when;

@SpringBootTest
public class CreateTransactionsTest extends IntegrationTest {

    @Autowired
    private TransactionsApiController transactionsApiController;

    @Autowired
    private IncomeSourcesApiController incomeSourcesApiController;

    @Autowired
    private ExpenseSourcesApiController expenseSourcesApiController;

    @Autowired
    private DebtsApiController debtsApiController;

    @Autowired
    private BudgetsApiController budgetsApiController;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ExpenseCategoriesApiController expenseCategoriesApiController;

    @Autowired
    private ActivesApiController activesApiController;

    @Autowired
    private LoansApiController loansApiController;

    @Autowired
    private CreditsApiController creditsApiController;

    @Autowired
    private TransactionFinder transactionFinder;

    @Autowired
    private EntityUtil entityUtil;

    @Autowired
    private ExpenseCategoryFinder expenseCategoryFinder;

    @MockBean
    private Limits limits;

    @Autowired
    private CreditRepository creditRepository;

    @BeforeEach
    public void init() {
        when(limits.getTransactionLimit()).thenReturn(10);
        when(limits.getAssetsLimit()).thenReturn(10);
        super.init();
    }

    @Test
    //Income Source -> Expense Source
    public void shouldCreate_IncomeSource_ExpenseSource_transaction() {
        IncomeSource incomeSource = incomeSourcesApiController.createIncomeSource(user.getId(),
                TestUtils.createIncomeForm("RUB", RandomString.make(), 1000L),
                "").getBody().getIncomeSource();

        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", RandomString.make(), 100L),
                user.getId()).getBody().getExpenseSource();

        transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId()));

        ExpenseSource updatedSource = expenseSourcesApiController.getExpenseSourceById(expenseSource.getId(), "")
                .getBody().getExpenseSource();

        Assert.assertEquals(150L, (long)updatedSource.getAmountCents());

        Budget budget = budgetsApiController.usersUserIdBudgetGet(user.getId(), "").getBody().getBudget();
        Assert.assertEquals(150L, budget.getExpenseSourcesAmountCents().longValue());
    }

    @Test
    //Exprnse Source -> Expense Category
    public void shouldCreate_ExpenseSource_ExpenseCategory_transaction() {
        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", RandomString.make(), 100L),
                user.getId()).getBody().getExpenseSource();


        ExpenseCategory expenseCategory = expenseCategoriesApiController.createExpenseCategory(getBasket().getId(),
                "",TestUtils.createExpenseCategoryForm(RandomString.make())).getBody().getExpenseCategory();

        TransactionCreationForm trForm = TestUtils.createTrForm(50L, expenseSource.getId(), expenseCategory.getId());
        trForm.getTransaction().setSourceType("ExpenseSource");
        trForm.getTransaction().setDestinationType("ExpenseCategory");
        transactionsApiController.createTransaction(user.getId(), "",
                trForm);

        ExpenseSource updatedSource = expenseSourcesApiController.getExpenseSourceById(expenseSource.getId(), "")
                .getBody().getExpenseSource();

        Assert.assertEquals(50L, (long)updatedSource.getAmountCents());

//        Budget budget = budgetsApiController.usersUserIdBudgetGet(user.getId(), "").getBody().getBudget();
//        Assert.assertEquals(150L, budget.getExpenseSourcesAmountCents().longValue());
    }

    @Test
    //Active -> Expense Category
    public void shouldCreate_Active_ExpenseCategory_sell_transaction() {
        String name = RandomString.make();
        ActiveCreationForm form = TestUtils.createActiveForm(name);
        form.getActive().setCostCents(200L);
        Active active = activesApiController.basketsBasketIdActivesPost(getBasket().getId(), "", form).getBody().getActive();

        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", RandomString.make(), 100L),
                user.getId()).getBody().getExpenseSource();

        TransactionCreationForm trForm = TestUtils.createTrForm(300L, active.getId(), expenseSource.getId());
        trForm.getTransaction().setSourceType("Active");
        trForm.getTransaction().setDestinationType("ExpenseSource");

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "", trForm)
                .getBody().getTransaction();

        TransactionEntity savedTr = transactionRepository.findById(transaction.getId()).get();
        Assert.assertEquals(TransactionTypeEnum.funds_move, savedTr.getTransactionType());

        ExpenseSource source = expenseSourcesApiController.getExpenseSourceById(expenseSource.getId(), "")
                .getBody().getExpenseSource();

        Active destination = activesApiController.getActiveById(active.getId(), "").getBody().getActive();
        Assert.assertEquals(-100L, (long)destination.getFullSaleProfit());
        Assert.assertEquals(-100L, (long)destination.getCostCents());

//        Budget budget = budgetsApiController.usersUserIdBudgetGet(user.getId(), "").getBody().getBudget();
//        Assert.assertEquals(150L, budget.getExpenseSourcesAmountCents().longValue());
    }

    @Test
    //Expense Source -> Active
    public void shouldCreate_ExpenseSource_Active_transaction() {
        String name = RandomString.make();
        ActiveCreationForm form = TestUtils.createActiveForm(name);
        form.getActive().setCostCents(200L);
        Active active = activesApiController.basketsBasketIdActivesPost(getBasket().getId(), "", form).getBody().getActive();

        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", RandomString.make(), 100L),
                user.getId()).getBody().getExpenseSource();

        TransactionCreationForm trForm = TestUtils.createTrForm(300L, expenseSource.getId(), active.getId());
        trForm.getTransaction().setSourceType("ExpenseSource");
        trForm.getTransaction().setDestinationType("Active");

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "", trForm)
                .getBody().getTransaction();

        TransactionEntity savedTr = transactionRepository.findById(transaction.getId()).get();
        Assert.assertEquals(TransactionTypeEnum.expense, savedTr.getTransactionType());

        ExpenseSource source = expenseSourcesApiController.getExpenseSourceById(expenseSource.getId(), "")
                .getBody().getExpenseSource();

        Active destination = activesApiController.getActiveById(active.getId(), "").getBody().getActive();
        Assert.assertEquals(0L, (long)destination.getFullSaleProfit());
        Assert.assertEquals(200L, (long)destination.getCostCents());
        Assert.assertEquals(500L, (long)destination.getInvestedAtPeriodCents());
        Assert.assertEquals(200L, (long)destination.getBoughtAtPeriodCents());
        Assert.assertEquals(300L, (long)destination.getSpentAtPeriodCents());

//        Budget budget = budgetsApiController.usersUserIdBudgetGet(user.getId(), "").getBody().getBudget();
//        Assert.assertEquals(150L, budget.getExpenseSourcesAmountCents().longValue());
    }

    @Test
    //Debt(Вам должны) (Expense Source -> Expense Category(Debts & Returns))
    public void shouldCreate_Debt_transaction() {
        String name = RandomString.make();
        DebtCreationForm debtForm = TestUtils.createDebtForm(name);
        Borrow borrow = debtsApiController.usersUserIdDebtsPost(user.getId(), "", debtForm)
                .getBody().getDebt();

        Set<TransactionEntity> trs = transactionFinder.findAllByBorrowIdTypeInPeriod(borrow.getId(),
                borrow.getType().name(),
                new Timestamp(System.currentTimeMillis() - 1000L * 5),
                new Timestamp(System.currentTimeMillis()));

        Assert.assertEquals(1, trs.size());
        TransactionEntity transactionEntity = trs.stream().findFirst().get();
        Assert.assertEquals(new BigDecimal("100"), transactionEntity.getAmountCents());
    }

    @Test
    //Loan(Вы должны) (Income Source(Loans & Returns) -> Expense Source("Wallet"))
    public void shouldCreate_Loan_transaction() {
        String name = RandomString.make();
        LoanCreationForm loanForm = TestUtils.createLoanForm(name);
        Borrow borrow = loansApiController.createLoan(user.getId(), "", loanForm)
                .getBody().getLoan();

        Set<TransactionEntity> trs = transactionFinder.findAllByBorrowIdTypeInPeriod(borrow.getId(),
                borrow.getType().name(),
                new Timestamp(System.currentTimeMillis() - 1000L * 5),
                new Timestamp(System.currentTimeMillis()));

        Assert.assertEquals(1, trs.size());
        TransactionEntity transactionEntity = trs.stream().findFirst().get();
        Assert.assertEquals(new BigDecimal("1000"), transactionEntity.getAmountCents());
        Assert.assertEquals("Loans, returns", transactionEntity.getSourceTitle());
        Assert.assertEquals("Wallet", transactionEntity.getDestinationTitle());
    }


    @Test
    //Return Debt (Income Source(Loans & Returns) -> Expense Source("Wallet"))
    public void shouldCreate_BorrowReturn_transaction_forDebt() {
        String name = RandomString.make();
        DebtCreationForm debtForm = TestUtils.createDebtForm(name);
        Borrow borrow = debtsApiController.usersUserIdDebtsPost(user.getId(), "", debtForm)
                .getBody().getDebt();

        Set<TransactionEntity> trs = transactionFinder.findAllByBorrowIdTypeInPeriod(borrow.getId(),
                borrow.getType().name(),
                new Timestamp(System.currentTimeMillis() - 1000L * 5),
                new Timestamp(System.currentTimeMillis()));

        Assert.assertEquals(1, trs.size());
        TransactionEntity transactionEntity = trs.stream().findFirst().get();
        Assert.assertEquals(new BigDecimal("100"), transactionEntity.getAmountCents());

        //Return
        IncomeSource source = incomeSourcesApiController
                .usersUserIdIncomeSourcesFirstBorrowGet(user.getId(), "RUB", "").getBody().getIncomeSource();

        ExpenseSource destination = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", RandomString.make(), 100L),
                user.getId()).getBody().getExpenseSource();

        TransactionCreationForm trForm = TestUtils.createTrForm(50L, source.getId(), destination.getId(),
                "IncomeSource", "ExpenseSource", LocalDateTime.now());
        trForm.getTransaction().setReturningBorrowId(borrow.getId());

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "", trForm).getBody().getTransaction();

        Set<TransactionEntity> trs2 = transactionFinder.findAllByBorrowIdTypeInPeriod(borrow.getId(),
                borrow.getType().name(),
                new Timestamp(System.currentTimeMillis() - 1000L * 5),
                new Timestamp(System.currentTimeMillis()));

        Borrow updatedBorrow = debtsApiController.debtsIdGet(borrow.getId(), "")
                .getBody().getDebt();
        Assert.assertEquals(50L, (long)updatedBorrow.getAmountCentsLeft());

    }

    @Test
    //Return Loan (Expense Source -> Expense Category(Debts & Returns))
    public void shouldCreate_BorrowReturn_transaction_for_Loan() {
        String name = RandomString.make();
        LoanCreationForm loanForm = TestUtils.createLoanForm(name);
        Borrow borrow = loansApiController.createLoan(user.getId(), "", loanForm)
                .getBody().getLoan();

        Set<TransactionEntity> trs = transactionFinder.findAllByBorrowIdTypeInPeriod(borrow.getId(),
                borrow.getType().name(),
                new Timestamp(System.currentTimeMillis() - 1000L * 5),
                new Timestamp(System.currentTimeMillis()));

        Assert.assertEquals(1, trs.size());
        TransactionEntity transactionEntity = trs.stream().findFirst().get();
        Assert.assertEquals(new BigDecimal("1000"), transactionEntity.getAmountCents());
        Assert.assertEquals("Loans, returns", transactionEntity.getSourceTitle());
        Assert.assertEquals("Wallet", transactionEntity.getDestinationTitle());

        //Return
        ExpenseSource source = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", RandomString.make(), 100L),
                user.getId()).getBody().getExpenseSource();

        ExpenseCategory destination = expenseCategoriesApiController.getFirstBorrowExpenseCategory(
                getJoyBasket().getId(), "RUB", "").getBody().getExpenseCategory();


        TransactionCreationForm trForm = TestUtils.createTrForm(50L, source.getId(), destination.getId(),
                "ExpenseSource", "ExpenseCategory", LocalDateTime.now());
        trForm.getTransaction().setReturningBorrowId(borrow.getId());

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "", trForm).getBody().getTransaction();

        Set<TransactionEntity> trs2 = transactionFinder.findAllByBorrowIdTypeInPeriod(borrow.getId(),
                borrow.getType().name(),
                new Timestamp(System.currentTimeMillis() - 1000L * 5),
                new Timestamp(System.currentTimeMillis()));

        Borrow updatedBorrow = loansApiController.getLoanById(borrow.getId(), "")
                .getBody().getLoan();
        Assert.assertEquals(950L, (long)updatedBorrow.getAmountCentsLeft());
    }


    @Test
    //Credit transaction (Income Source(Loans & Returns) -> Expense Source)
    public void shouldCreate_Credit_transaction() {
        String name = RandomString.make();
        CreditCreationForm creditForm = TestUtils.createCreditForm(name);


        ExpenseSource destination = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", RandomString.make(), 100L),
                user.getId()).getBody().getExpenseSource();
        creditForm.getCredit().getCreditingTransactionAttributes().setDestinationId(destination.getId());
        creditForm.getCredit().getCreditingTransactionAttributes().setDestinationType("ExpenseSource");

        Credit credit = creditsApiController.createCreditByUser(user.getId(), "", creditForm)
                .getBody().getCredit();

        IncomeSource source = incomeSourcesApiController
                .usersUserIdIncomeSourcesFirstBorrowGet(user.getId(), "RUB", "").getBody().getIncomeSource();


        Set<TransactionEntity> trs = transactionFinder.findAsSourceAndAsDestinationInPeriod(
                userEntity, TransactionTypeEnum.income, source.getId(), "IncomeSource",
                new Timestamp(System.currentTimeMillis() - 1000L * 50000),
                new Timestamp(System.currentTimeMillis()));

        Assert.assertEquals(1, trs.size());
        TransactionEntity transactionEntity = trs.stream().findFirst().get();
        Assert.assertEquals(new BigDecimal("1000"), transactionEntity.getAmountCents());
        Assert.assertEquals("Loans, returns", transactionEntity.getSourceTitle());
    }

    @Test
    //Return Credit transaction (Expense Source -> Expense Category(Debts & Returns))
    public void shouldCreate_ReturnCredit_transaction() {
        String name = RandomString.make();
        CreditCreationForm creditForm = TestUtils.createCreditForm(name);


        ExpenseSource destination = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", RandomString.make(), 100L),
                user.getId()).getBody().getExpenseSource();
        creditForm.getCredit().getCreditingTransactionAttributes().setDestinationId(destination.getId());
        creditForm.getCredit().getCreditingTransactionAttributes().setDestinationType("ExpenseSource");

        Credit credit = creditsApiController.createCreditByUser(user.getId(), "", creditForm)
                .getBody().getCredit();

        IncomeSource source = incomeSourcesApiController
                .usersUserIdIncomeSourcesFirstBorrowGet(user.getId(), "RUB", "").getBody().getIncomeSource();


        Set<TransactionEntity> trs = transactionFinder.findAsSourceAndAsDestinationInPeriod(
                userEntity, TransactionTypeEnum.income, source.getId(), "IncomeSource",
                new Timestamp(System.currentTimeMillis() - 1000L * 50000),
                new Timestamp(System.currentTimeMillis()));

        Assert.assertEquals(1, trs.size());
        TransactionEntity transactionEntity = trs.stream().findFirst().get();
        Assert.assertEquals(new BigDecimal("1000"), transactionEntity.getAmountCents());
        Assert.assertEquals("Loans, returns", transactionEntity.getSourceTitle());

        //Return
        ExpenseSource rsource = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", RandomString.make(), 100L),
                user.getId()).getBody().getExpenseSource();

        ExpenseCategoryEntity rdestination = expenseCategoryFinder.findByCredit(creditRepository.findById(credit.getId()).get());


        TransactionCreationForm trForm = TestUtils.createTrForm(50L, rsource.getId(), rdestination.getId(),
                "ExpenseSource", "ExpenseCategory", LocalDateTime.now());

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "", trForm).getBody().getTransaction();

        Set<TransactionEntity> trs2 = transactionFinder.findAllByCreditIdInPeriod(credit.getId(),
                new Timestamp(System.currentTimeMillis() - 1000L * 50000),
                new Timestamp(System.currentTimeMillis()));
        Assert.assertEquals(2, trs2.size());

        Credit updatedBorrow = creditsApiController.creditsIdGet(credit.getId(), "")
                .getBody().getCredit();
        Assert.assertEquals(50L, (long)updatedBorrow.getPaidAmountCents());
    }

}