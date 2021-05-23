package com.stanum.skrudzh.service.credit;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.TransactionNatureEnum;
import com.stanum.skrudzh.model.enums.TransactionPurposeEnum;
import com.stanum.skrudzh.service.TransactionBase;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class CreditTransactionsService extends TransactionBase {

    private final ExpenseCategoryFinder expenseCategoryFinder;

    @Autowired
    public CreditTransactionsService(EntityUtil entityUtil,
                                     UserUtil userUtil,
                                     CurrencyService currencyService,
                                     TransactionRepository transactionRepository,
                                     ExpenseCategoryFinder expenseCategoryFinder) {
        super(transactionRepository, entityUtil, userUtil, currencyService);
        this.expenseCategoryFinder = expenseCategoryFinder;
    }

    public Set<TransactionEntity> getCreditPayTransactions(CreditEntity creditEntity) {
        ExpenseCategoryEntity creditExpenseCategory = expenseCategoryFinder.findByCreditWithDeleted(creditEntity);
        return transactionRepository.getCreditPayTransactions(creditExpenseCategory.getId());
    }

    public Set<TransactionEntity> getCreditPayTransactionsInPeriod(CreditEntity creditEntity,
                                                                   Timestamp from,
                                                                   Timestamp till) {
        ExpenseCategoryEntity creditExpenseCategory = expenseCategoryFinder.findByCreditWithDeleted(creditEntity);
        return transactionRepository.getCreditPayTransactionsInPeriod(creditExpenseCategory.getId(), from, till);
    }

    public TransactionEntity getCreditTransaction(CreditEntity creditEntity) {
        return transactionRepository.getByCredit(creditEntity)
                .orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND));
    }

    public void bindCreditingTransaction(CreditEntity creditEntity, Long transactionId) {
        Optional<TransactionEntity> transactionOptional = transactionRepository.findById(transactionId);
        if(!transactionOptional.isPresent()) {
            log.error("Transaction with id={} not found", transactionId);
            throw new AppException(HttpAppError.NOT_FOUND);
        }
        TransactionEntity transaction = transactionOptional.get();
        transaction.setCredit(creditEntity);
        save(transaction);
    }

    public void createCreditingTransaction(CreditEntity creditEntity, IncomeSourceEntity source, ExpenseSourceEntity destination) {
        log.info("Create crediting transaction creditEntityId={}, sourceId={}, destination={}",
                creditEntity.getId(), source.getId(), destination.getId());
        TransactionEntity creditingTransaction = new TransactionEntity();
        creditingTransaction.setUser(creditEntity.getUser());
        creditingTransaction.setCredit(creditEntity);
        creditingTransaction.setSourceType(EntityTypeEnum.IncomeSource.name());
        creditingTransaction.setSourceId(source.getId());
        creditingTransaction.setDestinationType(EntityTypeEnum.ExpenseSource.name());
        creditingTransaction.setDestinationId(destination.getId());
        creditingTransaction.setAmountCents(creditEntity.getAmountCents());
        creditingTransaction.setAmountCurrency(creditEntity.getCurrency());
        creditingTransaction.setConvertedAmountCents(creditEntity.getAmountCents());
        creditingTransaction.setConvertedAmountCurrency(creditEntity.getCurrency());
        creditingTransaction.setGotAt(creditEntity.getGotAt());
        creditingTransaction.setBuyingAsset(false);
        creditingTransaction.setTransactionPurpose(TransactionPurposeEnum.creation);
        afterCreate(creditingTransaction);
        if (destination.getIsVirtual()) creditingTransaction.setTransactionNature(TransactionNatureEnum.system);
        save(creditingTransaction);
    }

    public void updateCreditTransaction(CreditEntity creditEntity, boolean isNeedToUpdate) {
        if (!isNeedToUpdate) return;
        TransactionEntity creditTransaction = getCreditTransaction(creditEntity);
        BigDecimal oldAmount = creditTransaction.getAmountCents();
        Long oldSourceId = creditTransaction.getSourceId();
        String oldSourceType = creditTransaction.getSourceType();
        Long oldDestinationId = creditTransaction.getDestinationId();
        String oldDestinationType = creditTransaction.getDestinationType();
        creditTransaction.setAmountCents(creditEntity.getAmountCents());
        creditTransaction.setConvertedAmountCents(creditEntity.getAmountCents());
        creditTransaction.setGotAt(creditEntity.getGotAt());
        afterUpdate(creditTransaction, oldSourceId, oldSourceType, oldDestinationId, oldDestinationType, oldAmount, oldAmount, false);
        save(creditTransaction);
    }
}
