package com.stanum.skrudzh.controller.transactions;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.config.Limits;
import com.stanum.skrudzh.controller.*;
import com.stanum.skrudzh.controller.form.DebtCreationForm;
import com.stanum.skrudzh.controller.form.ExpenseCategoryCreationForm;
import com.stanum.skrudzh.controller.form.TransactionCreationForm;
import com.stanum.skrudzh.controller.form.TransactionUpdatingForm;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.dto.*;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.utils.RequestUtil;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
public class TransactionsApiControllerTest extends IntegrationTest {

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

    @MockBean
    private Limits limits;

    @BeforeEach
    public void init() {
        when(limits.getTransactionLimit()).thenReturn(10);
        when(limits.getAssetsLimit()).thenReturn(10);
        super.init();
    }

    @Test
    public void shouldBindTransactionWithDebt() {
        ExpenseSource expenseSource = createExpense(RandomString.make());

        ExpenseCategory expenseCategory = expenseCategoriesApiController.createExpenseCategory(getBasket().getId(), "",
                createExpenseForm(RandomString.make())).getBody().getExpenseCategory();

        ExpenseCategory expenseCategory2 = expenseCategoriesApiController.createExpenseCategory(getBasket().getId(), "",
                createExpenseForm(RandomString.make())).getBody().getExpenseCategory();

        TransactionCreationForm transactionForm = TestUtils.createTransactionForm(50L, expenseSource.getId(), expenseCategory.getId());
        transactionForm.getTransaction().setSourceType("ExpenseSource");
        transactionForm.getTransaction().setDestinationType("ExpenseCategory");
        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "",
                transactionForm).getBody().getTransaction();

        DebtCreationForm debtForm = TestUtils.createDebtForm(RandomString.make());
        Borrow debt = debtsApiController.usersUserIdDebtsPost(user.getId(), "", debtForm).getBody().getDebt();
        Assert.assertNotNull(debt);

        TransactionUpdatingForm payload = updatingForm(60L, expenseSource.getId(), expenseCategory2.getId(), LocalDateTime.now());
        payload.getTransaction().setSourceType("ExpenseSource");
        payload.getTransaction().setDestinationType("ExpenseCategory");
        payload.getTransaction().setBorrowId(debt.getId());

        transactionsApiController.updateTransactionById(transaction.getId(), "", payload);
        Transaction bindedTransaction = transactionsApiController.getTransactionById(transaction.getId(), "").getBody().getTransaction();
        Assert.assertEquals(debt.getId(), bindedTransaction.getReturningBorrow().getId());
        Assert.assertEquals(expenseCategory2.getId(), bindedTransaction.getDestinationId());
        Assert.assertEquals("ExpenseCategory", bindedTransaction.getDestinationType());
    }


    @Test
    public void shouldCreateTransaction() {
        IncomeSource incomeSource = incomeSourcesApiController.createIncomeSource(user.getId(),
                TestUtils.createIncomeForm("RUB", RandomString.make(), 1000L),
                "").getBody().getIncomeSource();

        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", RandomString.make(), 100L),
                user.getId()).getBody().getExpenseSource();

        transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId()));

        IncomeSource updatedSource = incomeSourcesApiController.
                getIncomeSourcebyId(incomeSource.getId(), "").getBody().getIncomeSource();

        Budget budget = budgetsApiController.usersUserIdBudgetGet(user.getId(), "").getBody().getBudget();
        Assert.assertEquals(150L, budget.getExpenseSourcesAmountCents().longValue());
    }

    @Test
    public void shouldNotCreateTransaction_ifUserExceedLimit() {
        when(limits.getTransactionLimit()).thenReturn(1);
        IncomeSource incomeSource = createIncome(RandomString.make());
        ExpenseSource expenseSource = createExpense(RandomString.make());

        Assertions.assertThrows(AppException.class, () -> {
            userEntity.setHasActiveSubscription(false);

            transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId()));


            transactionsApiController.createTransaction(user.getId(), "",
                    TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId()));
        });
    }

    @Test
    public void shouldLimitTransactions() {
        when(limits.getTransactionLimit()).thenReturn(2);
        UserEntity convert = TestUtils.convert(user);
        convert.setHasActiveSubscription(false);
        RequestUtil.setUser(convert);

        IncomeSource incomeSource = createIncome("Income source(limits)");
        ExpenseSource expenseSource = createExpense("Expense source(limits)");

        transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId()));

        Assertions.assertThrows(AppException.class, () -> {
            transactionsApiController.createTransaction(user.getId(), "",
                    TestUtils.createTrForm(40L, incomeSource.getId(), expenseSource.getId()));
        });

    }

    @Test
    @Disabled
    //Doesn't work with positive timezones (only works with negative timezones such as America/Los_Angeles)
    public void shouldGetAllTransactionsByUser() {
        IncomeSource incomeSource = createIncome("Income source(get all)");
        ExpenseSource expenseSource = createExpense("Expense source(get all)");

        transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId(), LocalDateTime.now(ZoneId.of("Europe/Paris"))));

        transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(40L, incomeSource.getId(), expenseSource.getId(), LocalDateTime.now(ZoneId.of("Europe/Paris"))));

        List<Transaction> transactions = transactionsApiController.getTransactions(user.getId(),
                "",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null).getBody().getTransactions();

        // 3 transactions
        List<TransactionEntity> all = transactionRepository.findAll();
        Assert.assertNotNull(transactions);
        Assert.assertEquals(3, transactions.size());
    }

    @Test
    public void shouldGetTransactionById() {
        IncomeSource incomeSource = createIncome(RandomString.make());
        ExpenseSource expenseSource = createExpense(RandomString.make());

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId())).getBody().getTransaction();

        Transaction result = transactionsApiController.getTransactionById(transaction.getId(), "").getBody().getTransaction();

        List<TransactionEntity> all = transactionRepository.findAll();

        // 3 transactions
        Assert.assertNotNull(result);
        Assert.assertEquals(transaction.getId(), result.getId());
    }

    @Test
    public void shouldUpdateTransaction() {
        IncomeSource incomeSource = createIncome("Income source(update)");
        ExpenseSource expenseSource = createExpense("Expense source(update)");

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId())).getBody().getTransaction();
        Budget budget = budgetsApiController.usersUserIdBudgetGet(user.getId(), "").getBody().getBudget();
        Assert.assertEquals(150L, budget.getExpenseSourcesAmountCents().longValue());

        transactionsApiController.updateTransactionById(transaction.getId(), "",
                updatingForm(60L, incomeSource.getId(), expenseSource.getId(), LocalDateTime.now()));

        Budget updatedBudget = budgetsApiController.usersUserIdBudgetGet(user.getId(), "").getBody().getBudget();
        Assert.assertEquals(160L, updatedBudget.getExpenseSourcesAmountCents().longValue());
    }

    @Test
    public void shouldUpdateSimilarTransaction() {
        IncomeSource incomeSource = createIncome(RandomString.make());
        ExpenseSource expenseSource = createExpense(RandomString.make());
        ExpenseSource expenseSource2 = createExpense(RandomString.make());

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId())).getBody().getTransaction();

        Transaction transaction2 = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(60L, incomeSource.getId(), expenseSource.getId())).getBody().getTransaction();

        Transaction transaction3 = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(70L, incomeSource.getId(), expenseSource.getId())).getBody().getTransaction();


        TransactionUpdatingForm form = new TransactionUpdatingForm();
        TransactionUpdatingForm.TransactionUF uf = form.new TransactionUF();
        uf.setUpdateSimilarTransactions(true);
        uf.setSourceId(incomeSource.getId());
        uf.setConvertedAmountCents(100L);
        uf.setGotAt(LocalDateTime.now());
        uf.setDestinationType(EntityTypeEnum.ExpenseSource.name());
        uf.setSourceType(EntityTypeEnum.IncomeSource.name());
        uf.setDestinationId(expenseSource2.getId());
        uf.setAmountCents(100L);

        form.setTransaction(uf);

        transactionsApiController.updateTransactionById(transaction.getId(), "", form);

        List<TransactionEntity> all = transactionRepository.findAll();

        TransactionEntity transactionEntity2 = transactionRepository.findById(transaction2.getId()).get();
        Assert.assertEquals(expenseSource2.getId(), transactionEntity2.getDestinationId());
        Assert.assertEquals("ExpenseSource", transactionEntity2.getDestinationType());

        TransactionEntity transactionEntity3 = transactionRepository.findById(transaction3.getId()).get();
        Assert.assertEquals(expenseSource2.getId(), transactionEntity3.getDestinationId());
        Assert.assertEquals("ExpenseSource", transactionEntity3.getDestinationType());
    }

    @Test
    public void shouldUpdateSimilarTransactionExpSource_ExpCategory() {
        ExpenseSource expenseSource = createExpense(RandomString.make());
        ExpenseCategory expenseCategory = expenseCategoriesApiController.createExpenseCategory(getBasket().getId(), "",
                createExpenseForm(RandomString.make())).getBody().getExpenseCategory();

        ExpenseCategory expenseCategory2 = expenseCategoriesApiController.createExpenseCategory(getBasket().getId(), "",
                createExpenseForm(RandomString.make())).getBody().getExpenseCategory();

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, expenseSource.getId(),
                        expenseCategory.getId(),
                        EntityTypeEnum.ExpenseSource.name(), EntityTypeEnum.ExpenseCategory.name(),
                        LocalDateTime.now())).getBody().getTransaction();

        Transaction transaction2 = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, expenseSource.getId(),
                        expenseCategory.getId(),
                        EntityTypeEnum.ExpenseSource.name(), EntityTypeEnum.ExpenseCategory.name(),
                        LocalDateTime.now())).getBody().getTransaction();

        Transaction transaction3 = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, expenseSource.getId(),
                        expenseCategory.getId(),
                        EntityTypeEnum.ExpenseSource.name(), EntityTypeEnum.ExpenseCategory.name(),
                        LocalDateTime.now())).getBody().getTransaction();


        TransactionUpdatingForm form = new TransactionUpdatingForm();
        TransactionUpdatingForm.TransactionUF uf = form.new TransactionUF();
        uf.setUpdateSimilarTransactions(true);
        uf.setSourceId(expenseSource.getId());
        uf.setSourceType(EntityTypeEnum.ExpenseSource.name());

        uf.setConvertedAmountCents(100L);
        uf.setGotAt(LocalDateTime.now());
        uf.setDestinationType(EntityTypeEnum.ExpenseCategory.name());
        uf.setDestinationId(expenseCategory2.getId());
        uf.setAmountCents(100L);

        form.setTransaction(uf);

        transactionsApiController.updateTransactionById(transaction.getId(), "", form);

        List<TransactionEntity> all = transactionRepository.findAll();

        TransactionEntity transactionEntity2 = transactionRepository.findById(transaction2.getId()).get();
        Assert.assertEquals(expenseCategory2.getId(), transactionEntity2.getDestinationId());
        Assert.assertEquals("ExpenseCategory", transactionEntity2.getDestinationType());

        TransactionEntity transactionEntity3 = transactionRepository.findById(transaction3.getId()).get();
        Assert.assertEquals(expenseCategory2.getId(), transactionEntity3.getDestinationId());
        Assert.assertEquals("ExpenseCategory", transactionEntity3.getDestinationType());
    }

    @Test
    public void shouldDestroyTransaction() {
        IncomeSource incomeSource = createIncome("Income source(destroy)");
        ExpenseSource expenseSource = createExpense("Expense source(destroy)");

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId())).getBody().getTransaction();

        Transaction transaction2 = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(40L, incomeSource.getId(), expenseSource.getId())).getBody().getTransaction();

        Budget budget = budgetsApiController.usersUserIdBudgetGet(user.getId(), "").getBody().getBudget();
        Assert.assertEquals(190L, budget.getExpenseSourcesAmountCents().longValue());

        transactionsApiController.deleteTransactionById(transaction.getId(), "");

        Budget updatedBudget = budgetsApiController.usersUserIdBudgetGet(user.getId(), "").getBody().getBudget();
        Assert.assertEquals(140L, updatedBudget.getExpenseSourcesAmountCents().longValue());
    }

    @Test
    public void shouldMarkAsDuplicated() {
        IncomeSource incomeSource = createIncome("Income source(duplicate)");
        ExpenseSource expenseSource = createExpense("Expense source(duplicate)");

        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId())).getBody().getTransaction();

        Transaction transaction2 = transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId())).getBody().getTransaction();

        Budget budget = budgetsApiController.usersUserIdBudgetGet(user.getId(), "").getBody().getBudget();
        Assert.assertEquals(200L, budget.getExpenseSourcesAmountCents().longValue());

        transactionsApiController.markTransactionAsDuplicated(transaction2.getId(), "");

        TransactionEntity transactionEntity = transactionRepository.findById(transaction2.getId()).get();
        Assert.assertTrue(transactionEntity.getIsDuplicated());
    }

    @Test
    public void shouldGetTransactionsByTimeRange() {
        IncomeSource incomeSource = createIncome("Income source(get all)");
        ExpenseSource expenseSource = createExpense("Expense source(get all)");

        transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(50L, incomeSource.getId(), expenseSource.getId(),
                        LocalDateTime.ofInstant(Instant.parse("2007-12-03T10:15:30.00Z"), ZoneId.of("Europe/Paris"))));

        transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(30L, incomeSource.getId(), expenseSource.getId(),
                        LocalDateTime.ofInstant(Instant.parse("2007-08-03T10:15:30.00Z"), ZoneId.of("Europe/Paris"))));
        transactionsApiController.createTransaction(user.getId(), "",
                TestUtils.createTrForm(30L, incomeSource.getId(), expenseSource.getId(),
                        LocalDateTime.ofInstant(Instant.parse("2007-04-20T10:15:30.00Z"), ZoneId.of("Europe/Paris"))));


        List<Transaction> transactions = transactionsApiController.getTransactions(user.getId(),
                "",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "2007-07-03T10:15:30+01:00[Europe/Paris]",
                "2008-12-03T10:15:30+01:00[Europe/Paris]",
                null).getBody().getTransactions();

        // 3 transactions
        List<TransactionEntity> all = transactionRepository.findAll();
        Assert.assertNotNull(transactions);
        Assert.assertEquals(2, transactions.size());
    }

    private IncomeSource createIncome(String incomeName) {
        IncomeSource incomeSource = incomeSourcesApiController.createIncomeSource(user.getId(),
                TestUtils.createIncomeForm("RUB", incomeName, 1000L),
                "").getBody().getIncomeSource();
        return incomeSource;
    }

    private ExpenseSource createExpense(String expenseName) {
        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", expenseName, 100L),
                user.getId()).getBody().getExpenseSource();
        return expenseSource;
    }

    private TransactionUpdatingForm updatingForm(Long amountCents,  Long sourceId, Long destinationId, LocalDateTime gotAt) {
        TransactionUpdatingForm form = new TransactionUpdatingForm();
        TransactionUpdatingForm.TransactionUF uf = form.new TransactionUF();
        uf.setAmountCents(amountCents);
        uf.setConvertedAmountCents(amountCents);
        uf.setSourceId(sourceId);
        uf.setDestinationId(destinationId);
        uf.setConvertedAmountCents(amountCents);
        uf.setGotAt(gotAt);
        uf.setDestinationType(EntityTypeEnum.ExpenseSource.name());
        uf.setSourceType(EntityTypeEnum.IncomeSource.name());
        form.setTransaction(uf);
        return form;
    }


    protected ExpenseCategoryCreationForm createExpenseForm(String name) {
        ExpenseCategoryCreationForm form = new ExpenseCategoryCreationForm();
        ExpenseCategoryCreationForm.ExpenseCategoryCF categoryCF = form.new ExpenseCategoryCF();
        categoryCF.setCurrency("RUB");
        categoryCF.setName(name);
        form.setExpenseCategory(categoryCF);
        return form;
    }
}

