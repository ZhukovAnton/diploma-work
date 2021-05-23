package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.model.enums.TransactionTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    @Query("from TransactionEntity " +
            "where sourceType = 'IncomeSource' and sourceId = :sourceId " +
            "and gotAt between :from and :till " +
            "and deletedAt is null and isDuplicated = false")
    Set<TransactionEntity> getIncomeTransactionsForIncomeSourceInPeriod(Long sourceId, Timestamp from, Timestamp till);

    @Query("from TransactionEntity where sourceId = :sourceId and sourceType = :sourceType " +
            "and deletedAt is null and isDuplicated = false")
    Set<TransactionEntity> getAllBySourceIdAndSourceType(Long sourceId, String sourceType);


    @Query("from TransactionEntity where sourceId = :entityId and sourceType = :entityType " +
            " or destinationId= :entityId and destinationType = :entityType")
    Set<TransactionEntity> findAllBySourceOrDestination(Long entityId, String entityType);

    Set<TransactionEntity> findByUserAndCommentAndTransactionTypeAndSourceTypeAndSourceId(UserEntity user,
                                                                        String comment,
                                                                        TransactionTypeEnum transactionType,
                                                                        String sourceType,
                                                                        Long sourceId);

    Set<TransactionEntity> findByUserAndCommentAndTransactionTypeAndDestinationTypeAndDestinationId(UserEntity user,
                                                                                  String comment,
                                                                                  TransactionTypeEnum transactionType,
                                                                                  String destinationType,
                                                                                  Long destinationId);

    @Query("from TransactionEntity trns " +
            "where (trns.destinationId = :destinationId) " +
            "and (trns.destinationType = :destinationType) and (trns.gotAt between :from and :to) " +
            "and (trns.transactionType = 2)" +
            "and trns.deletedAt is null and trns.isDuplicated = false")
    Set<TransactionEntity> getExpensesAsDestinationWithinPeriod(Long destinationId,
                                                                String destinationType,
                                                                Timestamp from,
                                                                Timestamp to);

    @Query("select trns from ExpenseCategoryEntity expensctgr " +
            "left join TransactionEntity trns on expensctgr.id = trns.destinationId " +
            "where expensctgr.basket = :basketEntity " +
            "and (:withVirtual = true or (:withVirtual = false and expensctgr.isVirtual = false)) " +
            "and trns.transactionType = 2 " +
            "and (trns.gotAt between :from and :to) " +
            "and trns.deletedAt is null and trns.isDuplicated = false " +
            "and trns.destinationType = 'ExpenseCategory'")
    Set<TransactionEntity> getAllJoyBasketTransactionsWithinPeriod(BasketEntity basketEntity, boolean withVirtual, Timestamp from, Timestamp to);

    @Query("select transaction from ActiveEntity active " +
            "left join TransactionEntity transaction on active.id = transaction.destinationId " +
            "left join ExpenseSourceEntity expenseSource on transaction.sourceId = expenseSource.id " +
            "where transaction.sourceType = 'ExpenseSource' " +
            "and active.basketEntity = :basketEntity and transaction.destinationType = 'Active' " +
            "and expenseSource.isVirtual = false " +
            "and (transaction.gotAt between :from and :to) " +
            "and transaction.deletedAt is null and transaction.isDuplicated = false and active.deletedAt is null")
    Set<TransactionEntity> getBuyOrExpenseActiveTransactionsWithinPeriod(BasketEntity basketEntity, Timestamp from, Timestamp to);

    @Query("from TransactionEntity transaction " +
            "where transaction.sourceType = 'Active' and transaction.sourceId = :activeId " +
            "and transaction.profit is not null and transaction.profit > 0 " +
            "and (transaction.gotAt between :from and :till) " +
            "and transaction.deletedAt is null and transaction.isDuplicated = false")
    Set<TransactionEntity> getPositiveProfitTransactionsInPeriod(Long activeId, Timestamp from, Timestamp till);

    @Query("from TransactionEntity where user = :userEntity " +
            "and ((:transactionType is null) or (transactionType = :transactionType)) " +
            "and ((sourceType = :transactionableType and sourceId = :transactionableId) or ((destinationType = :transactionableType and destinationId = :transactionableId))) " +
            "and gotAt between :from and :to " +
            "and deletedAt is null and isDuplicated = false " +
            "order by gotAt desc")
    Set<TransactionEntity> getTransactionsAsSourceAndAsDestination(UserEntity userEntity,
                                                                   TransactionTypeEnum transactionType,
                                                                   Long transactionableId,
                                                                   String transactionableType,
                                                                   Timestamp from,
                                                                   Timestamp to);

    @Query("select count(transaction.id) from TransactionEntity transaction where transaction.user = :userEntity " +
            "and transaction.gotAt between :from and :till and transaction.isDuplicated = false")
    Long countTransactionsInPeriod(UserEntity userEntity, Timestamp from, Timestamp till);

    @Query("from TransactionEntity where ((sourceType = 'ExpenseSource' and sourceId = :expenseSourceId) " +
            "or (destinationType = 'ExpenseSource' and destinationId = :expenseSourceId)) " +
            "and deletedAt is null and isDuplicated = false")
    Set<TransactionEntity> getAllExpenseSourceTransactions(Long expenseSourceId);

    @Query("from TransactionEntity " +
            "where transactionType = 2 and destinationType = 'ExpenseCategory' " +
            "and destinationId = :expenseCategoryId " +
            "and deletedAt is null and isDuplicated = false")
    Set<TransactionEntity> getTransactionsForExpenseCategory(Long expenseCategoryId);

    @Query("from TransactionEntity where transactionType = 2 " +
            "and destinationType = 'Active' and destinationId = :activeId " +
            "and gotAt between :from and :till " +
            "and deletedAt is null and isDuplicated = false")
    Set<TransactionEntity> getExpenseTransactionsForActive(Long activeId, Timestamp from, Timestamp till);

    @Query("from TransactionEntity transaction left join ExpenseSourceEntity expenseSource " +
            "on transaction.destinationId = expenseSource.id " +
            "where transaction.destinationType = 'ExpenseSource' and transaction.sourceType = 'Active' " +
            "and expenseSource.isVirtual = false and transaction.sourceId = :activeId " +
            "and transaction.deletedAt is null and transaction.isDuplicated = false")
    Set<TransactionEntity> getSaleTransactionForActive(Long activeId);

    @Query("from TransactionEntity transaction left join ExpenseSourceEntity expenseSource on transaction.sourceId = expenseSource.id " +
            "where transaction.sourceType = 'ExpenseSource' " +
            "and (expenseSource.isVirtual = false or (expenseSource.isVirtual = true and transaction.activeEntity is not null)) " +
            "and transaction.destinationType = 'Active' and transaction.destinationId = :activeId " +
            "and transaction.buyingAsset = true " +
            "and transaction.gotAt between :from and :till " +
            "and transaction.deletedAt is null and transaction.isDuplicated = false")
    Set<TransactionEntity> getBuyActiveTransactionsWithParams(Long activeId, Timestamp from, Timestamp till);

    @Query("from TransactionEntity " +
            "where ((destinationType = 'Active' and destinationId = :activeId) or (sourceType = 'Active' and sourceId = :activeId)) " +
            "and deletedAt is null and isDuplicated = false order by gotAt")
    Set<TransactionEntity> getAllOrderedTransactionsForActive(Long activeId);

    @Query("from TransactionEntity where transactionType = 2 " +
            "and destinationType = 'ExpenseCategory' and destinationId = :expenseCategoryId " +
            "and deletedAt is null and isDuplicated = false")
    Set<TransactionEntity> getCreditPayTransactions(Long expenseCategoryId);

    @Query("from TransactionEntity where transactionType = 2 and " +
            "destinationType = 'ExpenseCategory' and destinationId = :expenseCategoryId " +
            "and gotAt between :from and :till " +
            "and deletedAt is null and isDuplicated = false")
    Set<TransactionEntity> getCreditPayTransactionsInPeriod(Long expenseCategoryId, Timestamp from, Timestamp till);

    Optional<TransactionEntity> getByCredit(CreditEntity creditEntity);

    @Query("from TransactionEntity where borrow = :borrowEntity")
    Optional<TransactionEntity> getBorrowingTransaction(BorrowEntity borrowEntity);

    @Query("from TransactionEntity where borrow = :borrowEntity " +
            "and gotAt between :from and :till")
    TransactionEntity getBorrowingTransactionAtPeriod(BorrowEntity borrowEntity, Timestamp from, Timestamp till);

    @Query("from TransactionEntity where returningBorrow = :borrowEntity " +
            "and deletedAt is null and isDuplicated = false")
    Set<TransactionEntity> getReturningBorrowTransactions(BorrowEntity borrowEntity);

    @Query("from TransactionEntity where returningBorrow = :borrowEntity " +
            "and deletedAt is null and isDuplicated = false and gotAt between :from and :till")
    Set<TransactionEntity> getReturningBorrowTransactionsAtPeriod(BorrowEntity borrowEntity, Timestamp from, Timestamp till);

    @Query("from TransactionEntity where id = :id")
    Optional<TransactionEntity> getTransactionByIdWithDeleted(Long id);

    @Query("from TransactionEntity where accountEntity = :account " +
            "and deletedAt is null")
    Set<TransactionEntity> getSaltedgeTransactionsByAccount(AccountEntity account);

    @Query("from TransactionEntity where accountEntity = :account and saltEdgeTransactionStatus = 1 " +
            "and deletedAt is null")
    Set<TransactionEntity> getPendingTransactionsByAccount(AccountEntity account);

    @Query("from TransactionEntity where user = :userEntity " +
            "and gotAt between :from and :till " +
            "and deletedAt is null and isDuplicated = false " +
            "order by gotAt")
    Set<TransactionEntity> getAllByUserInPeriod(UserEntity userEntity, Timestamp from, Timestamp till);

    @Query("select min(gotAt) from TransactionEntity where user = :userEntity")
    Optional<Timestamp> findMinGotAt(UserEntity userEntity);

    @Query("from TransactionEntity where saltEdgeTransactionId is not null " +
            "and deletedAt is not null and isDuplicated = false")
    Set<TransactionEntity> getDestroyedSaltEdgeTransactions();

    @Query("from TransactionEntity where isDuplicated = true and isDuplicationActual = true and saltEdgeTransactionId is not null")
    Set<TransactionEntity> getActualDuplicatedTransactions();

    @Query("select t from ExpenseSourceEntity es " +
            "left join TransactionEntity t on t.destinationId = es.id " +
            "where t.sourceType = 'ExpenseSource' and t.destinationType = 'ExpenseSource' " +
            "and t.isVirtualSource = true and es.isVirtual = false and es.deletedAt is null and t.deletedAt is null " +
            "and t.gotAt = (select min(tr.gotAt) from TransactionEntity tr where (tr.destinationId = es.id and tr.destinationType='ExpenseSource') or (tr.sourceId = es.id and tr.sourceType = 'ExpenseSource'))")
    Set<TransactionEntity> getAllExpenseSourcesCreationTransactions();

    @Query("from TransactionEntity " +
            "where ((destinationId = :entityId and destinationType = :entityType) or (sourceId = :entityId and sourceType = :entityType)) " +
            "and transactionPurpose = 0 and transactionNature = 0 and deletedAt is null and isDuplicated = false")
    Set<TransactionEntity> getRegularUserTransactions(Long entityId, String entityType);

    @Query("select max(gotAt) from TransactionEntity " +
            "where ((destinationId = :entityId and destinationType = :entityType) or (sourceId = :entityId and sourceType = :entityType)) " +
            "and ((transactionPurpose = 0 and transactionNature = 0) or (transactionPurpose = 1 and transactionNature = 2)) and deletedAt is null")
    Optional<Timestamp> getLastRegularTransactionGotAt(Long entityId, String entityType);

    @Query("select max(gotAt) from TransactionEntity " +
            "where ((destinationId = :entityId and destinationType = :entityType) or (sourceId = :entityId and sourceType = :entityType)) " +
            "and transactionPurpose = 0 and transactionNature = 1 " +
            "and isDuplicated = false and deletedAt is null")
    Optional<Timestamp> getLastBankTransactionGotAt(Long entityId, String entityType);

    @Query("select min(gotAt) from TransactionEntity " +
            "where ((destinationId = :entityId and destinationType = :entityType) or (sourceId = :entityId and sourceType = :entityType)) " +
            "and transactionPurpose = 0 and transactionNature = 1 and gotAt between :after and current_timestamp " +
            "and isDuplicated = false and deletedAt is null")
    Optional<Timestamp> getFirstBankTransactionGotAt(Long entityId, String entityType, Timestamp after);

    @Query("select min(gotAt) from TransactionEntity " +
            "where ((destinationId = :entityId and destinationType = :entityType) or (sourceId = :entityId and sourceType = :entityType)) " +
            "and transactionPurpose = 0 and transactionNature = 1 " +
            "and isDuplicated = false and deletedAt is null")
    Optional<Timestamp> getFirstBankTransactionGotAt(Long entityId, String entityType);

    @Query("from TransactionEntity where destinationId = :destinationId and destinationType = 'ExpenseSource' " +
            "and deletedAt is null and isDuplicated = false")
    Set<TransactionEntity> getPositiveExpenseSourceTransactions(Long destinationId);

    @Query("from TransactionEntity where destinationType = 'Active' and destinationId = :activeId and buyingAsset = true and " +
            "deletedAt is null and isDuplicated = false")
    Set<TransactionEntity> getPositiveActiveTransactions(Long activeId);

    @Query("from TransactionEntity where sourceId = :entityId and sourceType = :entityType " +
            "and deletedAt is null and isDuplicated = false")
    Set<TransactionEntity> getNegativeTransactions(Long entityId, String entityType);

    @Query("from TransactionEntity " +
            "where ((destinationId = :entityId and destinationType = :entityType) or (sourceId = :entityId and sourceType = :entityType)) " +
            "and gotAt = (select max(gotAt) from TransactionEntity " +
            "             where ((destinationId = :entityId and destinationType = :entityType) or (sourceId = :entityId and sourceType = :entityType))" +
            "             and transactionPurpose = 2 and deletedAt is null)" +
            "and transactionPurpose = 2 and deletedAt is null")
    Optional<TransactionEntity> getLastSyncTransaction(Long entityId, String entityType);

    @Query("from TransactionEntity where destinationId = :expenseSourceId and destinationType = 'ExpenseSource' " +
            "and transactionPurpose = 1 " +
            "and deletedAt is null and isDuplicated = false")
    Optional<TransactionEntity> getExpenseSourceCreationTransaction(Long expenseSourceId);

    @Query("from TransactionEntity where destinationId = :activeId and destinationType = 'Active' " +
            "and activeEntity is not null and activeEntity.id = :activeId and transactionPurpose = 1 " +
            "and deletedAt is null and isDuplicated = false")
    Optional<TransactionEntity> getActiveCreationTransaction(Long activeId);

    @Query("select gotAt from TransactionEntity where ((sourceId = :entityId and sourceType = :entityType) or (destinationId = :entityId)) " +
            "and saltEdgeTransactionStatus = 1 and gotAt = " +
            "(select min(gotAt) from TransactionEntity where ((sourceId = :entityId and sourceType = :entityType) or (destinationId = :entityId)) " +
            "and saltEdgeTransactionStatus = 1 and gotAt between :from and :till)")
    List<Timestamp> getFirstPendingGotAtInPeriod(Long entityId, String entityType, Timestamp from, Timestamp till);

}
