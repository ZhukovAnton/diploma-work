package com.stanum.skrudzh.service.transaction;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.TransactionTypeEnum;
import com.stanum.skrudzh.service.borrow.BorrowFinder;
import com.stanum.skrudzh.service.borrow.BorrowTransactionService;
import com.stanum.skrudzh.service.credit.CreditFinder;
import com.stanum.skrudzh.service.credit.CreditTransactionsService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceTransactionsService;
import com.stanum.skrudzh.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TransactionFinder {

    private final TransactionRepository transactionRepository;

    private final CreditFinder creditFinder;

    private final CreditTransactionsService creditTransactionsService;

    private final BorrowFinder borrowFinder;

    private final BorrowTransactionService borrowTransactionService;

    private final IncomeSourceFinder incomeSourceFinder;

    private final IncomeSourceTransactionsService incomeSourceTransactionsService;

    public TransactionEntity findByIdWithDeleted(Long id) {
        return transactionRepository.getTransactionByIdWithDeleted(id)
                .orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND));
    }

    public Set<TransactionEntity> findAsSourceAndAsDestinationInPeriod(
            UserEntity userEntity,
            TransactionTypeEnum transactionType,
            Long transactionableId,
            String transactionableType,
            Timestamp from,
            Timestamp till) {
        return transactionRepository.getTransactionsAsSourceAndAsDestination(
                userEntity,
                transactionType,
                transactionableId,
                transactionableType,
                from,
                till);
    }

    public Timestamp findOldestTransactionGotAt(UserEntity userEntity) {
        return transactionRepository.findMinGotAt(userEntity).orElse(null);
    }

    public Set<TransactionEntity> findAllByUserInPeriod(UserEntity userEntity,
                                                        Timestamp from,
                                                        Timestamp till) {
        return transactionRepository.getAllByUserInPeriod(userEntity, from, till);
    }

    public Set<TransactionEntity> findAllByCreditIdInPeriod(Long creditId, Timestamp from, Timestamp till) {
        Set<TransactionEntity> transactionEntities = Collections.emptySet();
        CreditEntity creditEntity = creditFinder.findByIdWithNull(creditId);
        if (creditEntity != null) {
            transactionEntities = creditTransactionsService.getCreditPayTransactionsInPeriod(creditEntity, from, till);
            TransactionEntity creditTransaction = creditTransactionsService.getCreditTransaction(creditEntity);
            if (creditTransaction.getGotAt().after(from) && creditTransaction.getGotAt().before(till)) {
                transactionEntities.add(creditTransaction);
            }
        }
        return transactionEntities;
    }

    public Set<TransactionEntity> findAllByBorrowIdTypeInPeriod(Long borrowId, String borrowType, Timestamp from, Timestamp till) {
        Set<TransactionEntity> transactionEntities = Collections.emptySet();
        BorrowEntity borrowEntity = borrowFinder.findBorrowByIdAndType(borrowId, BorrowTypeEnum.valueOf(borrowType));
        if (borrowEntity != null) {
            transactionEntities = borrowTransactionService.getReturningTransactionsAtPeriod(borrowEntity, from, till);
            TransactionEntity borrowingTransaction = borrowTransactionService.getBorrowingTransactionAtPeriod(borrowEntity, from, till);
            if (borrowingTransaction != null) {
                transactionEntities.add(borrowingTransaction);
            }
        }
        return transactionEntities;
    }

    public Set<TransactionEntity> findAllByIdTypeAndParams(Long transactionableId, String transactionableType,
                                                           String transactionType, Timestamp from, Timestamp till) {
        UserEntity userEntity = RequestUtil.getUser();
        Set<TransactionEntity> transactionEntities = findAsSourceAndAsDestinationInPeriod(
                userEntity,
                transactionType != null ? TransactionTypeEnum.valueOf(transactionableType) : null,
                transactionableId,
                transactionableType,
                from,
                till);
        if (transactionableType.equals(EntityTypeEnum.Active.name())) {
            IncomeSourceEntity activeIncomeSource = incomeSourceFinder.findByActiveId(transactionableId);
            if (activeIncomeSource != null) {
                transactionEntities.addAll(incomeSourceTransactionsService
                        .findIncomeTransactionsInPeriod(activeIncomeSource, from, till));
            }
        }
        if (transactionableType.equals(EntityTypeEnum.IncomeSource.name())) {
            IncomeSourceEntity incomeSourceEntity = incomeSourceFinder.findById(transactionableId);
            if (incomeSourceEntity.getActive() != null) {
                transactionEntities.addAll(incomeSourceTransactionsService
                        .findPositiveProfitTransactions(incomeSourceEntity.getActive(), from, till));
            }
        }
        return transactionEntities;
    }

    public Set<TransactionEntity> findDestroyedSaltEdgeTransactions() {
        return transactionRepository.getDestroyedSaltEdgeTransactions();
    }

    public Set<TransactionEntity> findActualDuplications() {
        return transactionRepository.getActualDuplicatedTransactions();
    }

    public Set<TransactionEntity> findAllExpenseSourceCreationTransactions() {
        return transactionRepository.getAllExpenseSourcesCreationTransactions();
    }
}
