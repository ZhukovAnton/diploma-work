package com.stanum.skrudzh.saltedge.learn;

import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.ExpenseCategoriesApiController;
import com.stanum.skrudzh.controller.ExpenseSourcesApiController;
import com.stanum.skrudzh.controller.TransactionsApiController;
import com.stanum.skrudzh.controller.form.TransactionCreationForm;
import com.stanum.skrudzh.controller.saltedge.AbstractSaltedgeTest;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.dto.*;
import com.stanum.skrudzh.service.saltedge.learn.LearnSaltedgeService;
import com.stanum.skrudzh.service.user.UserRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LearnSaltedgeServiceTest extends AbstractSaltedgeTest {

    @Autowired
    private LearnSaltedgeService learnSaltedgeService;

    @Autowired
    private ExpenseSourcesApiController expenseSourcesApiController;

    @Autowired
    private TransactionsApiController transactionsApiController;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ExpenseCategoriesApiController expenseCategoriesApiController;

    @Autowired
    private UserRequestService userRequestService;

    @Test
    public void shouldLearnSaltEdge() {
        userRequestService.onboarding(user.getId());
        ExpenseSource expenseSource = expenseSourcesApiController.usersUserIdExpenseSourcesPost("",
                TestUtils.createExpenseForm("RUB", "Expense Source", 100L),
                user.getId()).getBody().getExpenseSource();

        ExpenseCategory expenseCategory = expenseCategoriesApiController.getExpenseCategoriesByUser(user.getId(),
                true, "").getBody().getExpenseCategories().get(0);

        TransactionCreationForm trForm = TestUtils.createTrForm(50L, expenseSource.getId(), expenseCategory.getId());
        trForm.getTransaction().setSourceType("ExpenseSource");
        trForm.getTransaction().setDestinationType("ExpenseCategory");
        Transaction transaction = transactionsApiController.createTransaction(user.getId(), "",
                trForm).getBody().getTransaction();

        TransactionEntity transactionEntity = transactionRepository.findById(transaction.getId()).get();
        learnSaltedgeService.learn(transactionEntity);
    }
}
