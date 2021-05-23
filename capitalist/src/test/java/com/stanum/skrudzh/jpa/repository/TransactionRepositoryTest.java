package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.model.enums.SaltEdgeTransactionStatusEnum;
import com.stanum.skrudzh.service.saltedge.connectable.ConnectableTransactionFinder;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@SpringBootTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ConnectableTransactionFinder connectableTransactionFinder;

    @Test
    public void shouldGetMaxFetchFromDate() {
        Timestamp start = new Timestamp(System.currentTimeMillis() - 1000);
        Long sourceId = new Random().nextLong();
        String sourceType = "ExpenseSource";
        TransactionEntity transaction = createTransaction(sourceId, sourceType);
        TransactionEntity transaction2 = createTransaction(sourceId, sourceType);

        transactionRepository.save(transaction);
        transactionRepository.save(transaction2);

        List<TransactionEntity> all = transactionRepository.findAll();

        ExpenseSourceEntity transactionable = new ExpenseSourceEntity();
        transactionable.setId(sourceId);

        Timestamp end = new Timestamp(System.currentTimeMillis() + 1000);

        Optional<Timestamp> firstPendingGotAtInPeriod =
                connectableTransactionFinder.findFirstPendingGotAtInPeriod(transactionable, start, end);
        Assert.assertTrue(firstPendingGotAtInPeriod.isPresent());
    }

    @Test
    public void shouldReturnEmptyIfMaxFetchFromDateNotFound() {
        Long sourceId = new Random().nextLong();
        String sourceType = "ExpenseSource";
        TransactionEntity transaction = createTransaction(sourceId, sourceType);
        TransactionEntity transaction2 = createTransaction(sourceId, sourceType);


        transactionRepository.save(transaction);
        transactionRepository.save(transaction2);

        ExpenseSourceEntity transactionable = new ExpenseSourceEntity();
        transaction.setId(sourceId);

        Timestamp start = new Timestamp(System.currentTimeMillis());
        Timestamp end = new Timestamp(System.currentTimeMillis());

        Optional<Timestamp> firstPendingGotAtInPeriod =
                connectableTransactionFinder.findFirstPendingGotAtInPeriod(transactionable, start, end);
        Assert.assertTrue(firstPendingGotAtInPeriod.isEmpty());
    }

    private TransactionEntity createTransaction(Long sourceId, String sourceType) {
        TransactionEntity entity = new TransactionEntity();
        entity.setAmountCents(new BigDecimal("100"));
        entity.setAmountCurrency("RUB");
        entity.setConvertedAmountCents(new BigDecimal("100"));
        entity.setConvertedAmountCurrency("RUB");
        entity.setSourceId(sourceId);
        entity.setSourceType(sourceType);
        entity.setGotAt(new Timestamp(System.currentTimeMillis()));
        entity.setSaltEdgeTransactionStatus(SaltEdgeTransactionStatusEnum.pending);

        return entity;
    }

}
