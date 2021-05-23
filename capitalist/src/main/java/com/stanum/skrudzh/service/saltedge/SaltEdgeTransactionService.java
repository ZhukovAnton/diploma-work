package com.stanum.skrudzh.service.saltedge;

import com.google.common.collect.Sets;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.HashableTypeEnum;
import com.stanum.skrudzh.model.enums.TransactionNatureEnum;
import com.stanum.skrudzh.model.enums.TransactionPurposeEnum;
import com.stanum.skrudzh.saltage.model.Transaction;
import com.stanum.skrudzh.service.TransactionBase;
import com.stanum.skrudzh.service.active.ActiveTransactionsService;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryManagementService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceManagementService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceTransactionsService;
import com.stanum.skrudzh.service.hash.HashFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceManagementService;
import com.stanum.skrudzh.service.saltedge.account.AccountFinder;
import com.stanum.skrudzh.utils.LoggerUtil;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EqualsAndHashCode(callSuper = true)
@EnableScheduling
@Slf4j
public class SaltEdgeTransactionService extends TransactionBase {

    private static final Set<String> skipSaltEdgeCategory = Sets.newHashSet("uncategorized");

    private final IncomeSourceManagementService incomeSourceManagementService;

    private final IncomeSourceFinder incomeSourceFinder;

    private final ExpenseSourceManagementService expenseSourceManagementService;

    private final ExpenseCategoryManagementService expenseCategoryManagementService;

    private final AccountFinder accountFinder;

    private final ExpenseSourceFinder expenseSourceFinder;

    private final ExpenseCategoryFinder expenseCategoryFinder;

    private final HashFinder hashFinder;

    private final ExpenseSourceTransactionsService expenseSourceTransactionsService;

    private final ActiveTransactionsService activeTransactionsService;

    private final ExchangeService exchangeService;

    @Autowired
    public SaltEdgeTransactionService(IncomeSourceManagementService incomeSourceManagementService,
                                      IncomeSourceFinder incomeSourceFinder,
                                      ExpenseSourceManagementService expenseSourceManagementService,
                                      ExpenseCategoryManagementService expenseCategoryManagementService,
                                      AccountFinder accountFinder,
                                      CurrencyService currencyService,
                                      EntityUtil entityUtil, UserUtil userUtil,
                                      TransactionRepository transactionRepository,
                                      ExpenseSourceFinder expenseSourceFinder,
                                      ExpenseCategoryFinder expenseCategoryFinder,
                                      HashFinder hashFinder,
                                      ExpenseSourceTransactionsService expenseSourceTransactionsService,
                                      ActiveTransactionsService activeTransactionsService,
                                      ExchangeService exchangeService) {
        super(transactionRepository, entityUtil, userUtil, currencyService);
        this.incomeSourceManagementService = incomeSourceManagementService;
        this.incomeSourceFinder = incomeSourceFinder;
        this.expenseSourceManagementService = expenseSourceManagementService;
        this.expenseCategoryManagementService = expenseCategoryManagementService;
        this.accountFinder = accountFinder;
        this.expenseSourceFinder = expenseSourceFinder;
        this.expenseCategoryFinder = expenseCategoryFinder;
        this.hashFinder = hashFinder;
        this.expenseSourceTransactionsService = expenseSourceTransactionsService;
        this.activeTransactionsService = activeTransactionsService;
        this.exchangeService = exchangeService;
    }

    public void refreshTransactions(List<Transaction> saltEdgeTransactions, AccountConnectionEntity accountConnectionEntity) {
        log.info("Refresh {} transactions for accountConnectionEntityId={}, transactions:{} ",
                saltEdgeTransactions.size(), accountConnectionEntity.getId(), LoggerUtil.printTrs(saltEdgeTransactions));

        destroyPendingTransactions(accountConnectionEntity.getAccountEntity());

        Map<String, TransactionEntity> saltEdgeTransactionMap = findTransactionsByAccount(accountConnectionEntity.getAccountEntity())
                .stream()
                .collect(Collectors.toMap(TransactionEntity::getSaltEdgeTransactionId,
                        tr -> tr));

        saltEdgeTransactions.forEach(transaction -> {
            try {
                if (!saltEdgeTransactionMap.containsKey(transaction.getId())) {
                    if (!transaction.isDuplicated()) {
                        TransactionEntity tr = transformIntoApiAttachedTransaction(transaction, accountConnectionEntity);
                        afterCreate(tr);
                        save(tr);
                    }
                } else {
                    TransactionEntity transactionToUpdate = saltEdgeTransactionMap.get(transaction.getId());
                    updateTransactionData(transactionToUpdate, transaction);
                    save(transactionToUpdate);
                }
            } catch (Throwable th) {
                log.error("Error while saving transaction", th);
            }
        });
    }

    public void destroyConnectionTransactions(ConnectionEntity connectionEntity) {
        Set<AccountEntity> connectionAccounts = accountFinder.findAccountsByConnection(connectionEntity);
        Set<TransactionEntity> connectionTransactions = connectionAccounts
                .stream()
                .flatMap(accountEntity -> findTransactionsByAccount(accountEntity).stream())
                .collect(Collectors.toSet());
        connectionTransactions.forEach(transactionForDestroy -> {
            transactionForDestroy.setDeletedAt(TimeUtil.now());
            //No need for afterDestroy because salt-edge transactions does not changes any entities balances
            save(transactionForDestroy);
        });
    }

    public void synchronizeBalances(Object transactionHolder) {
        log.info("Synchronize balances");
        BigDecimal balanceFromTransactions = getBalanceFromTransactions(transactionHolder);
        BigDecimal syncAmount = entityUtil.getBalance(transactionHolder).add(balanceFromTransactions.negate());
        if (syncAmount.compareTo(BigDecimal.ZERO) == 0) return;
        Optional<TransactionEntity> syncTransactionOptional = findSyncTransaction(transactionHolder);
        if (syncTransactionOptional.isPresent()) {
            log.info("");
            TransactionEntity syncTransaction = syncTransactionOptional.get();
            BigDecimal actualBalanceOfSyncTransaction = syncTransaction.getAmountCents().add(syncAmount);
            if (actualBalanceOfSyncTransaction.compareTo(BigDecimal.ZERO) < 0) {
                Long sourceId = syncTransaction.getSourceId();
                String sourceType = syncTransaction.getSourceType();
                syncTransaction.setSourceId(syncTransaction.getDestinationId());
                syncTransaction.setSourceType(syncTransaction.getDestinationType());
                syncTransaction.setDestinationId(sourceId);
                syncTransaction.setDestinationType(sourceType);
                syncTransaction.setAmountCents(actualBalanceOfSyncTransaction.negate());
                syncTransaction.setConvertedAmountCents(actualBalanceOfSyncTransaction.negate());
            } else {
                syncTransaction.setAmountCents(actualBalanceOfSyncTransaction);
                syncTransaction.setConvertedAmountCents(actualBalanceOfSyncTransaction);
            }
            log.info("Update sync transaction id={}", syncTransaction.getId());
            save(syncTransaction);
        } else {
            if (!hasAnyRegularUserTransactions(transactionHolder)) {
                Optional<TransactionEntity> creationTransaction = findCreationTransaction(transactionHolder);
                creationTransaction.ifPresent(this::destroyCreationTransaction);
            }
            createSyncTransaction(transactionHolder, syncAmount);
        }
    }

    //Have to destroy pending transaction because of pending transactions have unpredictable behavior
    //No need to update them, because we destroy them and load new one
    public void destroyPendingTransactions(AccountEntity accountEntity) {
        Set<TransactionEntity> pendingTransactions = findPendingTransactionsByAccount(accountEntity);
        log.info("Destroy {} pending transactions for accountEntityId={}", pendingTransactions.size(), accountEntity.getId());
        pendingTransactions.forEach(pendingTransaction -> {
            pendingTransaction.setDeletedAt(TimeUtil.now());
            save(pendingTransaction);
        });
    }

    public Set<TransactionEntity> findTransactionsByAccount(AccountEntity accountEntity) {
        Set<TransactionEntity> result = transactionRepository.getSaltedgeTransactionsByAccount(accountEntity);
        log.info("{} transactions found by accountEntity id={}", result.size(), accountEntity.getId());
        return result;
    }

    public Set<TransactionEntity> findPendingTransactionsByAccount(AccountEntity accountEntity) {
        Set<TransactionEntity> result = transactionRepository.getPendingTransactionsByAccount(accountEntity);
        return result;
    }

    private TransactionEntity transformIntoApiAttachedTransaction(Transaction saltEdgeTransaction,
                                                                  AccountConnectionEntity accountConnectionEntity) {
        log.info("Create new SaltEdge transaction, accountConnectionEntity id={}, sourceType={}, transaction={}",
                accountConnectionEntity.getId(), accountConnectionEntity.getSourceType(), LoggerUtil.printTr(saltEdgeTransaction));
        TransactionEntity transactionEntity =
                createTransactionEntityFromSaltEdgeTransaction(saltEdgeTransaction, accountConnectionEntity.getAccountEntity());
        if (accountConnectionEntity.getSourceType().equals(EntityTypeEnum.ExpenseSource)) {
            ExpenseSourceEntity expenseSourceEntity = (ExpenseSourceEntity) entityUtil
                    .find(accountConnectionEntity.getSourceId(), EntityTypeEnum.ExpenseSource);
            setSourceAndDestinationForExpenseSourceWithHash(
                    transactionEntity,
                    expenseSourceEntity,
                    saltEdgeTransaction);
        } else if (accountConnectionEntity.getSourceType().equals(EntityTypeEnum.Active)) {
            ActiveEntity activeEntity = (ActiveEntity) entityUtil
                    .find(accountConnectionEntity.getSourceId(), EntityTypeEnum.Active);
            if (transactionEntity.getGotAt().before(activeEntity.getCreatedAt())) {
                transactionEntity.setGotAt(Timestamp
                        .valueOf(activeEntity
                                .getCreatedAt()
                                .toLocalDateTime()
                                .plusNanos(1000)));
            }
            setSourceAndDestinationForActive(
                    transactionEntity,
                    activeEntity,
                    saltEdgeTransaction.getAmount());
        }
        return transactionEntity;
    }

    private void setSourceAndDestinationForExpenseSourceWithHash(TransactionEntity transactionEntity,
                                                                 ExpenseSourceEntity attachTo,
                                                                 Transaction saltEdgeTransaction) {
        log.info("Set source and dest for Expense Source id={}, transactionId={}", attachTo.getId(), transactionEntity.getId());
        if (saltEdgeTransaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            IncomeSourceEntity source = getIncomeSourceFromHash(transactionEntity, saltEdgeTransaction)
                    .orElseGet(() -> incomeSourceFinder
                            .findVirtualIncomeSource(
                                    attachTo.getUser(),
                                    transactionEntity.getAmountCurrency())
                            .orElseGet(() -> incomeSourceManagementService
                                    .createVirtualIncomeSource(attachTo.getUser(),
                                            transactionEntity.getAmountCurrency())));
            transactionEntity.setSourceId(source.getId());
            transactionEntity.setSourceType(EntityTypeEnum.IncomeSource.name());
            transactionEntity.setDestinationId(attachTo.getId());
            transactionEntity.setDestinationType(EntityTypeEnum.ExpenseSource.name());
            if (!source.getCurrency().equals(transactionEntity.getAmountCurrency())) {
                transactionEntity.setAmountCents(exchangeService
                        .exchange(
                                transactionEntity.getAmountCurrency(),
                                source.getCurrency(),
                                transactionEntity.getAmountCents()));
                transactionEntity.setAmountCurrency(source.getCurrency());
            }
        } else {
            ExpenseCategoryEntity destination;
            destination = getExpenseCategoryFromHash(transactionEntity, saltEdgeTransaction)
                    .orElseGet(() -> expenseCategoryFinder
                            .findVirtualExpenseCategoryByParams(
                                    attachTo.getUser(),
                                    transactionEntity.getConvertedAmountCurrency())
                            .orElseGet(() -> expenseCategoryManagementService
                                    .createVirtualExpenseCategory(attachTo.getUser(),
                                            transactionEntity.getConvertedAmountCurrency())));
            transactionEntity.setSourceId(attachTo.getId());
            transactionEntity.setSourceType(EntityTypeEnum.ExpenseSource.name());
            transactionEntity.setDestinationId(destination.getId());
            transactionEntity.setDestinationType(EntityTypeEnum.ExpenseCategory.name());
            transactionEntity.setConvertedAmountCurrency(destination.getCurrency());
            if (!destination.getCurrency().equals(transactionEntity.getConvertedAmountCurrency())) {
                transactionEntity.setConvertedAmountCents(exchangeService
                        .exchange(
                                transactionEntity.getConvertedAmountCurrency(),
                                destination.getCurrency(),
                                transactionEntity.getConvertedAmountCents()));
                transactionEntity.setConvertedAmountCurrency(destination.getCurrency());
            }
        }
    }

    private void setSourceAndDestinationForActive(TransactionEntity transactionEntity,
                                                  ActiveEntity attachTo,
                                                  BigDecimal amount) {
        log.info("Set source and dest for ActiveEntityId = {}, saltEdgeTrId={}", attachTo.getId(), transactionEntity.getSaltEdgeTransactionId());
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            IncomeSourceEntity source = incomeSourceFinder
                    .findIncomeSourceByActive(attachTo);

            ExpenseSourceEntity destination = expenseSourceFinder.findFirstByParams(attachTo.getBasketEntity().getUser(),
                    true, transactionEntity.getConvertedAmountCurrency()).
                    orElseGet(() -> expenseSourceManagementService.createDefault(attachTo.getBasketEntity().getUser(),
                            true, transactionEntity.getConvertedAmountCurrency()));

            transactionEntity.setSourceId(source.getId());
            transactionEntity.setSourceType(EntityTypeEnum.IncomeSource.name());
            transactionEntity.setDestinationId(destination.getId());
            transactionEntity.setDestinationType(EntityTypeEnum.ExpenseSource.name());
        } else {
            ExpenseSourceEntity source = expenseSourceFinder.findFirstByParams(attachTo.getBasketEntity().getUser(),
                    true, transactionEntity.getAmountCurrency()).
                    orElseGet(() -> expenseSourceManagementService.createDefault(attachTo.getBasketEntity().getUser(),
                            true, transactionEntity.getAmountCurrency()));

            transactionEntity.setSourceId(source.getId());
            transactionEntity.setSourceType(EntityTypeEnum.ExpenseSource.name());
            transactionEntity.setDestinationId(attachTo.getId());
            transactionEntity.setDestinationType(EntityTypeEnum.Active.name());
        }
    }

    private void updateTransactionData(TransactionEntity transactionEntity, Transaction saltEdgeTransaction) {
        log.debug("Update existing transaction with id={}, set SaltEdge category {}",
                transactionEntity.getId(), saltEdgeTransaction.getCategory());
        transactionEntity.setSaltEdgeCategory(saltEdgeTransaction.getCategory());
        if (saltEdgeTransaction.isDuplicated()) {
            transactionEntity.setIsDuplicated(true);
        }
    }

    private Optional<ExpenseCategoryEntity> getExpenseCategoryFromHash(TransactionEntity transactionEntity,
                                                                       Transaction saltEdgeTransaction) {
        if (validateSaltEdgeCategory(saltEdgeTransaction)) {
            String saltEdgeCategory = saltEdgeTransaction.getCategory();
            Optional<HashEntity> hashEntityOptional = hashFinder
                    .findHashByParams(transactionEntity.getUser(),
                            saltEdgeCategory,
                            HashableTypeEnum.ExpenseCategory,
                            transactionEntity.getConvertedAmountCurrency());
            if (hashEntityOptional.isPresent()) {
                transactionEntity.setIsAutoCategorized(true);
                if (hashEntityOptional.get().getHashableId() != null) {
                    return expenseCategoryFinder.findByIdOptional(hashEntityOptional.get().getHashableId());
                } else {
                    return expenseCategoryManagementService.createExpenseCategoryFromPrototypeWithTransaction(
                            transactionEntity.getUser(),
                            transactionEntity.getConvertedAmountCurrency(),
                            hashEntityOptional.get().getPrototypeKey());
                }
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private Optional<IncomeSourceEntity> getIncomeSourceFromHash(TransactionEntity transactionEntity,
                                                                 Transaction saltEdgeTransaction) {
        log.info("Get income source from hash, transaction id = {}, saltedge tr id ={}", transactionEntity.getId(), saltEdgeTransaction.getId());
        if (validateSaltEdgeCategory(saltEdgeTransaction)) {
            String saltEdgeCategory = saltEdgeTransaction.getCategory();
            Optional<HashEntity> hashEntityOptional = hashFinder
                    .findHashByParams(transactionEntity.getUser(),
                            saltEdgeCategory,
                            HashableTypeEnum.IncomeSource,
                            transactionEntity.getAmountCurrency())
                    .or(() -> hashFinder.findHashByParams(transactionEntity.getUser(),
                            saltEdgeCategory,
                            HashableTypeEnum.IncomeSource,
                            transactionEntity.getUser().getDefaultCurrency()));
            if (hashEntityOptional.isPresent()) {
                transactionEntity.setIsAutoCategorized(true);
                if (hashEntityOptional.get().getHashableId() != null) {
                    return incomeSourceFinder.findByIdOptional(hashEntityOptional.get().getHashableId());
                } else {
                    return incomeSourceManagementService.createIncomeSourceFromPrototypeWithTransaction(
                            transactionEntity.getUser(),
                            transactionEntity.getAmountCurrency(),
                            hashEntityOptional.get().getPrototypeKey());
                }
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private boolean validateSaltEdgeCategory(Transaction saltEdgeTransaction) {
        return saltEdgeTransaction.getCategory() != null
                && !skipSaltEdgeCategory.contains(saltEdgeTransaction.getCategory())
                && saltEdgeTransaction.getExtra() != null
                && saltEdgeTransaction.getExtra().getCategorizationConfidence() != null
                && saltEdgeTransaction.getExtra().getCategorizationConfidence().compareTo(BigDecimal.valueOf(0.8)) >= 0;
    }

    private BigDecimal getBalanceFromTransactions(Object transactionHolder) {
        if (transactionHolder instanceof ExpenseSourceEntity) {
            return expenseSourceTransactionsService.getBalanceFromTransactions((ExpenseSourceEntity) transactionHolder);
        } else {
            return activeTransactionsService.getBalanceFromTransactions((ActiveEntity) transactionHolder);
        }
    }

    private Optional<TransactionEntity> findSyncTransaction(Object transactionHolder) {
        if (transactionHolder instanceof ExpenseSourceEntity) {
            return expenseSourceTransactionsService.findSyncTransaction((ExpenseSourceEntity) transactionHolder);
        } else if (transactionHolder instanceof ActiveEntity) {
            return activeTransactionsService.findSyncTransaction((ActiveEntity) transactionHolder);
        } else {
            return Optional.empty();
        }
    }

    private boolean hasAnyRegularUserTransactions(Object transactionHolder) {
        if (transactionHolder instanceof ExpenseSourceEntity) {
            return expenseSourceTransactionsService.hasRegularUserTransactions((ExpenseSourceEntity) transactionHolder);
        } else {
            return activeTransactionsService.hasRegularUserTransactions((ActiveEntity) transactionHolder);
        }
    }

    private Optional<TransactionEntity> findCreationTransaction(Object transactionHolder) {
        if (transactionHolder instanceof ExpenseSourceEntity) {
            return expenseSourceTransactionsService.findCreationTransaction((ExpenseSourceEntity) transactionHolder);
        } else if (transactionHolder instanceof ActiveEntity) {
            return activeTransactionsService.findCreationTransaction((ActiveEntity) transactionHolder);
        } else {
            return Optional.empty();
        }
    }

    private void destroyCreationTransaction(TransactionEntity transactionForDestroy) {
        transactionForDestroy.setDeletedAt(TimeUtil.now());
        save(transactionForDestroy);
    }

    private void createSyncTransaction(Object transactionHolder,
                                       BigDecimal syncAmount) {
        log.info("Create sync transaction");
        TransactionEntity syncTransaction = new TransactionEntity();
        if (transactionHolder instanceof ExpenseSourceEntity) {
            ExpenseSourceEntity expenseSourceEntity = (ExpenseSourceEntity) transactionHolder;
            syncTransaction.setUser(expenseSourceEntity.getUser());
            syncTransaction.setAmountCurrency(expenseSourceEntity.getCurrency());
            syncTransaction.setConvertedAmountCurrency(expenseSourceEntity.getCurrency());
            syncTransaction.setGotAt(getSyncTransactionGotAt(transactionHolder));
            setSourceAndDestinationForExpenseSource(syncTransaction, expenseSourceEntity, syncAmount);
        } else if (transactionHolder instanceof ActiveEntity) {
            ActiveEntity activeEntity = (ActiveEntity) transactionHolder;
            syncTransaction.setUser(activeEntity.getBasketEntity().getUser());
            syncTransaction.setAmountCurrency(activeEntity.getCurrency());
            syncTransaction.setConvertedAmountCurrency(activeEntity.getCurrency());
            syncTransaction.setGotAt(getSyncTransactionGotAt(transactionHolder));
            setSourceAndDestinationForActive(syncTransaction, activeEntity, syncAmount);
        } else {
            return;
        }
        fillGeneralInfoForSyncTransaction(syncTransaction, syncAmount);
        afterCreate(syncTransaction);
        save(syncTransaction);
    }

    private void setSourceAndDestinationForExpenseSource(TransactionEntity transactionEntity,
                                                         ExpenseSourceEntity expenseSourceEntity,
                                                         BigDecimal amount) {
        ExpenseSourceEntity virtualExpenseSource = expenseSourceFinder
                .findFirstByParams(expenseSourceEntity.getUser(), true, expenseSourceEntity.getCurrency())
                .orElseGet(() -> expenseSourceManagementService
                        .createDefault(
                                expenseSourceEntity.getUser(),
                                true,
                                expenseSourceEntity.getCurrency()));
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            transactionEntity.setSourceId(virtualExpenseSource.getId());
            transactionEntity.setDestinationId(expenseSourceEntity.getId());
        } else {
            transactionEntity.setSourceId(expenseSourceEntity.getId());
            transactionEntity.setDestinationId(virtualExpenseSource.getId());
        }
        transactionEntity.setSourceType(EntityTypeEnum.ExpenseSource.name());
        transactionEntity.setDestinationType(EntityTypeEnum.ExpenseSource.name());
    }

    private void fillGeneralInfoForSyncTransaction(TransactionEntity syncTransaction, BigDecimal syncAmount) {
        if (syncAmount.compareTo(BigDecimal.ZERO) > 0) {
            syncTransaction.setAmountCents(syncAmount);
            syncTransaction.setConvertedAmountCents(syncAmount);
        } else {
            syncTransaction.setAmountCents(syncAmount.negate());
            syncTransaction.setConvertedAmountCents(syncAmount.negate());
        }
        syncTransaction.setTransactionNature(TransactionNatureEnum.system);
        syncTransaction.setTransactionPurpose(TransactionPurposeEnum.sync);
    }

    private Timestamp getSyncTransactionGotAt(Object transactionHolder) {
        Timestamp lastRegularTransactionGotAt = getLastRegularTransactionGotAt(transactionHolder).orElse(null); //null is allowed
        Timestamp tempGotAt;
        if (transactionHolder instanceof ExpenseSourceEntity) {
            tempGotAt = lastRegularTransactionGotAt != null
                    ? transactionRepository
                    .getFirstBankTransactionGotAt(
                            ((ExpenseSourceEntity) transactionHolder).getId(),
                            EntityTypeEnum.ExpenseSource.name(),
                            lastRegularTransactionGotAt)
                    .orElse(TimeUtil.now())
                    : transactionRepository
                    .getFirstBankTransactionGotAt(
                            ((ExpenseSourceEntity) transactionHolder).getId(),
                            EntityTypeEnum.ExpenseSource.name())
                    .orElse(TimeUtil.now());
        } else {
            tempGotAt = lastRegularTransactionGotAt != null
                    ? transactionRepository
                    .getFirstBankTransactionGotAt(
                            ((ActiveEntity) transactionHolder).getId(),
                            EntityTypeEnum.Active.name(),
                            lastRegularTransactionGotAt)
                    .orElse(TimeUtil.now())
                    : transactionRepository
                    .getFirstBankTransactionGotAt(
                            ((ActiveEntity) transactionHolder).getId(),
                            EntityTypeEnum.Active.name())
                    .orElse(TimeUtil.now());
        }
        return Timestamp.valueOf(tempGotAt.toLocalDateTime().minusNanos(1000));
    }

}
