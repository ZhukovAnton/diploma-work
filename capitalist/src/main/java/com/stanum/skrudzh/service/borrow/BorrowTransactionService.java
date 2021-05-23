package com.stanum.skrudzh.service.borrow;

import com.stanum.skrudzh.controller.form.attributes.BorrowingTransactionAttributes;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.enums.*;
import com.stanum.skrudzh.service.TransactionBase;
import com.stanum.skrudzh.service.basket.BasketFinder;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryManagementService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceManagementService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceManagementService;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@Service
@Slf4j
public class BorrowTransactionService extends TransactionBase {

    private final ExpenseCategoryManagementService expenseCategoryManagementService;

    private final IncomeSourceManagementService incomeSourceManagementService;

    private final ExpenseSourceManagementService expenseSourceManagementService;

    private final ExpenseSourceFinder expenseSourceFinder;

    private final ExpenseCategoryFinder expenseCategoryFinder;

    private final BasketFinder basketFinder;

    private final IncomeSourceFinder incomeSourceFinder;

    @Autowired
    public BorrowTransactionService(EntityUtil entityUtil,
                                    UserUtil userUtil,
                                    CurrencyService currencyService,
                                    TransactionRepository transactionRepository,
                                    ExpenseCategoryManagementService expenseCategoryManagementService,
                                    IncomeSourceManagementService incomeSourceManagementService,
                                    ExpenseSourceManagementService expenseSourceManagementService,
                                    ExpenseSourceFinder expenseSourceFinder,
                                    ExpenseCategoryFinder expenseCategoryFinder,
                                    BasketFinder basketFinder, IncomeSourceFinder incomeSourceFinder) {
        super(transactionRepository, entityUtil, userUtil, currencyService);
        this.expenseCategoryManagementService = expenseCategoryManagementService;
        this.incomeSourceManagementService = incomeSourceManagementService;
        this.expenseSourceManagementService = expenseSourceManagementService;
        this.expenseSourceFinder = expenseSourceFinder;
        this.expenseCategoryFinder = expenseCategoryFinder;
        this.basketFinder = basketFinder;
        this.incomeSourceFinder = incomeSourceFinder;
    }

    public void bindBorrowWithTransaction(BorrowEntity borrow, TransactionEntity transaction) {
        BorrowTypeEnum borrowType = borrow.getType();
        transaction.setBorrowType(borrowType);
        transaction.setBorrow(borrow);
        if(borrowType == BorrowTypeEnum.Debt) {
            transaction.setBasketType(BasketTypeEnum.joy);
        }

        fillTransactionWithBorrowData(transaction, borrow);
        save(transaction);
    }

    public void createBorrowingTransaction(BorrowEntity borrowEntity, BorrowingTransactionAttributes attributes) {
        log.info("Create {} transaction for borrowEntity id={}, attributes={}", borrowEntity.getType(), borrowEntity.getId(), attributes);
        if (borrowEntity.getType().equals(BorrowTypeEnum.Debt)) {
            BasketEntity joyBasket = basketFinder.findBasketByUserAndType(borrowEntity.getUser(), BasketTypeEnum.joy);
            ExpenseCategoryEntity borrowExpenseCategory = expenseCategoryFinder
                    .findBorrowExpenseCategoryByParams(joyBasket, borrowEntity.getAmountCurrency())
                    .orElseGet(() -> expenseCategoryManagementService.createBorrowExpenseCategory(joyBasket, borrowEntity.getAmountCurrency()));

            ExpenseSourceEntity sourceExpenseSource;
            if (attributes.getSourceId() != null) {
                sourceExpenseSource = expenseSourceFinder.findById(attributes.getSourceId());
            } else {
                sourceExpenseSource = expenseSourceFinder.findFirstByParams(borrowEntity.getUser(),
                        true, borrowEntity.getAmountCurrency())
                        .orElseGet(() -> expenseSourceManagementService.createDefault(borrowEntity.getUser(),
                                true, borrowEntity.getAmountCurrency()));
            }
            createDebtTransaction(borrowEntity, sourceExpenseSource, borrowExpenseCategory);

        } else {
            IncomeSourceEntity borrowIncomeSource = incomeSourceFinder.findBorrowIncomeSource(borrowEntity.getUser(), borrowEntity.getAmountCurrency())
                    .orElseGet(() -> incomeSourceManagementService.createBorrowIncomeSource(borrowEntity.getUser(), borrowEntity.getAmountCurrency()));

            ExpenseSourceEntity destinationExpenseSource;
            if (attributes.getDestinationId() != null) {
                destinationExpenseSource = expenseSourceFinder.findById(attributes.getDestinationId());
            } else {
                destinationExpenseSource = expenseSourceFinder.findFirstByParams(borrowEntity.getUser(),
                        true, borrowEntity.getAmountCurrency())
                        .orElseGet(() -> expenseSourceManagementService.createDefault(borrowEntity.getUser(),
                                true, borrowEntity.getAmountCurrency()));
            }
            createLoanTransaction(borrowEntity, borrowIncomeSource, destinationExpenseSource);
        }
    }

    public TransactionEntity getBorrowingTransaction(BorrowEntity borrowEntity) {
        return transactionRepository.getBorrowingTransaction(borrowEntity)
                .orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND,
                        "Something went wrong. There is no borrowing transactions for this borrow with id = " + borrowEntity.getId()));
    }

    public TransactionEntity getBorrowingTransactionAtPeriod(BorrowEntity borrowEntity, Timestamp from, Timestamp till) {
        return transactionRepository.getBorrowingTransactionAtPeriod(borrowEntity, from, till);
    }

    public Set<TransactionEntity> getReturningTransactions(BorrowEntity borrowEntity) {
        return transactionRepository.getReturningBorrowTransactions(borrowEntity);
    }

    public Set<TransactionEntity> getReturningTransactionsAtPeriod(BorrowEntity borrowEntity, Timestamp from, Timestamp till) {
        return transactionRepository.getReturningBorrowTransactionsAtPeriod(borrowEntity, from, till);
    }

    private void createDebtTransaction(BorrowEntity borrowEntity, ExpenseSourceEntity source, ExpenseCategoryEntity destination) {
        TransactionEntity debtTransaction = new TransactionEntity();
        debtTransaction.setSourceId(source.getId());
        debtTransaction.setSourceType(EntityTypeEnum.ExpenseSource.name());
        debtTransaction.setDestinationId(destination.getId());
        debtTransaction.setDestinationType(EntityTypeEnum.ExpenseCategory.name());
        debtTransaction.setBorrow(borrowEntity);
        debtTransaction.setBorrowType(BorrowTypeEnum.Debt);
        debtTransaction.setBasketType(BasketTypeEnum.joy);

        fillTransactionWithBorrowData(debtTransaction, borrowEntity);
        if (source.getIsVirtual()) debtTransaction.setTransactionNature(TransactionNatureEnum.system);
        afterCreate(debtTransaction);
        save(debtTransaction);
    }

    private void createLoanTransaction(BorrowEntity borrowEntity, IncomeSourceEntity source, ExpenseSourceEntity destination) {
        TransactionEntity loanTransaction = new TransactionEntity();
        loanTransaction.setSourceId(source.getId());
        loanTransaction.setSourceType(EntityTypeEnum.IncomeSource.name());
        loanTransaction.setDestinationId(destination.getId());
        loanTransaction.setDestinationType(EntityTypeEnum.ExpenseSource.name());
        loanTransaction.setBorrow(borrowEntity);
        loanTransaction.setBorrowType(BorrowTypeEnum.Loan);

        fillTransactionWithBorrowData(loanTransaction, borrowEntity);
        if (destination.getIsVirtual()) loanTransaction.setTransactionNature(TransactionNatureEnum.system);
        afterCreate(loanTransaction);
        save(loanTransaction);
    }

    private void fillTransactionWithBorrowData(TransactionEntity transaction, BorrowEntity borrow) {
        transaction.setAmountCents(borrow.getAmountCents());
        transaction.setAmountCurrency(borrow.getAmountCurrency());
        transaction.setConvertedAmountCents(borrow.getAmountCents());
        transaction.setConvertedAmountCurrency(borrow.getAmountCurrency());
        transaction.setUser(borrow.getUser());
        transaction.setComment(borrow.getComment());
        transaction.setBuyingAsset(false);
        transaction.setIsReturned(false);
        transaction.setPayday(borrow.getPayday());
        transaction.setWhom(borrow.getName());
        transaction.setGotAt(borrow.getBorrowedAt());
        transaction.setTransactionPurpose(TransactionPurposeEnum.creation);
    }

    public void updateBorrowingTransaction(BorrowEntity borrowEntity, boolean isNeedToUpdateTransaction) {
        if (!isNeedToUpdateTransaction) return;
        TransactionEntity borrowingTransaction = getBorrowingTransaction(borrowEntity);
        BigDecimal oldAmount = borrowingTransaction.getAmountCents();
        BigDecimal oldConvertedAmount = borrowingTransaction.getConvertedAmountCents();

        borrowingTransaction.setIsReturned(borrowEntity.getIsReturned());
        borrowingTransaction.setWhom(borrowEntity.getName());
        borrowingTransaction.setAmountCents(borrowEntity.getAmountCents());
        borrowingTransaction.setConvertedAmountCents(borrowEntity.getAmountCents());
        borrowingTransaction.setGotAt(borrowEntity.getBorrowedAt());

        afterUpdate(borrowingTransaction,
                borrowingTransaction.getSourceId(),
                borrowingTransaction.getSourceType(),
                borrowingTransaction.getDestinationId(),
                borrowingTransaction.getDestinationType(),
                oldAmount,
                oldConvertedAmount,
                false);
        save(borrowingTransaction);
    }
}
