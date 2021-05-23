package com.stanum.skrudzh.service.expense_source;

import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.TransactionNatureEnum;
import com.stanum.skrudzh.model.enums.TransactionPurposeEnum;
import com.stanum.skrudzh.service.TransactionBase;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryManagementService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceManagementService;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

@Service
@EqualsAndHashCode(callSuper = true)
public class ExpenseSourceTransactionsService extends TransactionBase {

    private final IncomeSourceManagementService incomeSourceManagementService;

    private final ExpenseCategoryManagementService expenseCategoryManagementService;

    private final ExpenseCategoryFinder expenseCategoryFinder;

    private final IncomeSourceFinder incomeSourceFinder;

    @Autowired
    public ExpenseSourceTransactionsService(EntityUtil entityUtil,
                                            UserUtil userUtil,
                                            CurrencyService currencyService,
                                            IncomeSourceManagementService incomeSourceManagementService,
                                            ExpenseCategoryManagementService expenseCategoryManagementService,
                                            TransactionRepository transactionRepository,
                                            ExpenseCategoryFinder expenseCategoryFinder,
                                            IncomeSourceFinder incomeSourceFinder) {
        super(transactionRepository, entityUtil, userUtil, currencyService);
        this.incomeSourceManagementService = incomeSourceManagementService;
        this.expenseCategoryManagementService = expenseCategoryManagementService;
        this.expenseCategoryFinder = expenseCategoryFinder;
        this.incomeSourceFinder = incomeSourceFinder;
    }

    public Set<TransactionEntity> findAllTransactions(ExpenseSourceEntity expenseSourceEntity) {
        return transactionRepository.getAllExpenseSourceTransactions(expenseSourceEntity.getId());
    }

    public Optional<TransactionEntity> findCreationTransaction(ExpenseSourceEntity expenseSourceEntity) {
        return transactionRepository.getExpenseSourceCreationTransaction(expenseSourceEntity.getId());
    }

    public void createAmountChangeTransaction(ExpenseSourceEntity expenseSourceEntity, BigDecimal amountToChange) {
        UserEntity expenseSourceUser = expenseSourceEntity.getUser();
        TransactionEntity transactionEntity;
        if (amountToChange.compareTo(BigDecimal.ZERO) > 0) {
            IncomeSourceEntity virtualIncomeSource = incomeSourceFinder
                    .findVirtualIncomeSource(expenseSourceUser, expenseSourceEntity.getCurrency())
                    .orElseGet(() -> incomeSourceManagementService.createVirtualIncomeSource(expenseSourceUser,
                            expenseSourceEntity.getCurrency()));
            transactionEntity = createChangeTransaction(
                    new ChangeTransactionCF(
                            expenseSourceUser,
                            virtualIncomeSource,
                            expenseSourceEntity,
                            amountToChange,
                            amountToChange,
                            false,
                            null,
                            null));
        } else {
            ExpenseCategoryEntity virtualExpenseCategory = expenseCategoryFinder
                    .findVirtualExpenseCategoryByParams(expenseSourceUser, expenseSourceEntity.getCurrency())
                    .orElseGet(() -> expenseCategoryManagementService.createVirtualExpenseCategory(expenseSourceUser,
                            expenseSourceEntity.getCurrency()));

            transactionEntity = createChangeTransaction(
                    new ChangeTransactionCF(
                            expenseSourceUser,
                            expenseSourceEntity,
                            virtualExpenseCategory,
                            amountToChange.negate(),
                            amountToChange.negate(),
                            false,
                            null,
                            null));
        }
        transactionEntity.setTransactionNature(TransactionNatureEnum.system);
        save(transactionEntity);
    }

    public void createInitialTransaction(ExpenseSourceEntity source, ExpenseSourceEntity destination, BigDecimal initialAmount, Boolean isChangeable) {
        TransactionEntity transactionEntity = createChangeTransaction(
                new ChangeTransactionCF(
                        source.getUser(),
                        source,
                        destination,
                        initialAmount,
                        initialAmount,
                        false,
                        TransactionPurposeEnum.creation,
                        TransactionNatureEnum.system));
        transactionEntity.setIsChangeable(isChangeable);
        transactionEntity.setGotAt(destination.getCreatedAt());
        save(transactionEntity);
    }

    //TODO: move to saltEdgeTransactionService
    public Optional<TransactionEntity> findSyncTransaction(ExpenseSourceEntity expenseSourceEntity) {
        Optional<TransactionEntity> syncTransactionOptional = findLastSyncTransaction(expenseSourceEntity);
        if (syncTransactionOptional.isPresent()) {
            if (hasRegularUserTransactions(expenseSourceEntity)) {
                Optional<Timestamp> lastRegularTransactionGotAtOptional = findLastRegularTransactionGotAt(expenseSourceEntity);
                if (lastRegularTransactionGotAtOptional.isPresent()) {
                    if (lastRegularTransactionGotAtOptional.get().before(syncTransactionOptional.get().getGotAt())) {
                        return syncTransactionOptional;
                    } else {
                        return Optional.empty();
                    }
                } else {
                    return syncTransactionOptional;
                }
            } else {
                return syncTransactionOptional;
            }
        } else {
            return Optional.empty();
        }
    }

    public BigDecimal getBalanceFromTransactions(ExpenseSourceEntity expenseSourceEntity) {
        BigDecimal balance = positiveTransactions(expenseSourceEntity)
                .stream()
                .map(TransactionEntity::getConvertedAmountCents)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        balance = balance.add(negativeTransactions(expenseSourceEntity).stream()
                .map(TransactionEntity::getAmountCents)
                .reduce(BigDecimal.ZERO, BigDecimal::add).negate());
        return balance;
    }

    private Set<TransactionEntity> positiveTransactions(ExpenseSourceEntity expenseSourceEntity) {
        return transactionRepository.getPositiveExpenseSourceTransactions(expenseSourceEntity.getId());
    }

    public Set<TransactionEntity> negativeTransactions(ExpenseSourceEntity expenseSourceEntity) {
        return transactionRepository.getNegativeTransactions(expenseSourceEntity.getId(), EntityTypeEnum.ExpenseSource.name());
    }

    public boolean hasRegularUserTransactions(ExpenseSourceEntity expenseSourceEntity) {
        return !findRegularUserTransactions(expenseSourceEntity).isEmpty();
    }
}
