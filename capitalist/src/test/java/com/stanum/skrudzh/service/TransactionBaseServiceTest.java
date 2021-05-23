package com.stanum.skrudzh.service;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.saltage.model.Transaction;
import com.stanum.skrudzh.service.saltedge.SaltEdgeTransactionService;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class TransactionBaseServiceTest extends IntegrationTest {
    private LocalDateTime now = LocalDateTime.now();
    private LocalDate dateNow = LocalDate.now();

    @Autowired
    private SaltEdgeTransactionService transactionBase;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void shouldSaveSaltedgeOrder() {
        AccountEntity accountEntity = new AccountEntity();
        ConnectionEntity connectionEntity = new ConnectionEntity();
        accountEntity.setConnectionEntity(connectionEntity);

        String id1 = "123456789012345678";
        String id2 = "123456789012345690";
        String id3 = "123456789012377777";
        String id4 = "123456789112387723";
        String id5 = "123456789212393663";

        TransactionEntity result = transactionBase.createTransactionEntityFromSaltEdgeTransaction(createTr(id3), accountEntity);
        TransactionEntity result2 = transactionBase.createTransactionEntityFromSaltEdgeTransaction(createTr(id2), accountEntity);
        TransactionEntity result3 = transactionBase.createTransactionEntityFromSaltEdgeTransaction(createTr(id1), accountEntity);
        TransactionEntity result4 = transactionBase.createTransactionEntityFromSaltEdgeTransaction(createTr(id5), accountEntity);
        TransactionEntity result5 = transactionBase.createTransactionEntityFromSaltEdgeTransaction(createTr(id4), accountEntity);

        List<TransactionEntity> transactionEntities = new ArrayList<>();
        transactionEntities.add(result);
        transactionEntities.add(result2);
        transactionEntities.add(result3);
        transactionEntities.add(result4);
        transactionEntities.add(result5);

        List<TransactionEntity> sorted = transactionEntities.stream()
                .sorted(Comparator.comparing(TransactionEntity::getGotAt)).collect(Collectors.toList());

        Assert.assertEquals(id1, sorted.get(0).getSaltEdgeTransactionId());
        Assert.assertEquals(id2, sorted.get(1).getSaltEdgeTransactionId());
        Assert.assertEquals(id3, sorted.get(2).getSaltEdgeTransactionId());
        Assert.assertEquals(id4, sorted.get(3).getSaltEdgeTransactionId());
        Assert.assertEquals(id5, sorted.get(4).getSaltEdgeTransactionId());
    }

    @Test
    @Disabled
    public void shouldNotCreateTransactionWithSameSaltedgeId() {
        String saltedgeId = RandomString.make();

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setSaltEdgeTransactionId(saltedgeId);
        transactionEntity.setAmountCents(new BigDecimal("100"));
        transactionEntity.setAmountCurrency("RUB");
        transactionEntity.setConvertedAmountCurrency("RUB");
        transactionEntity.setConvertedAmountCents(new BigDecimal("100"));
        transactionEntity.setUser(userEntity);


        TransactionEntity transactionEntity2 = SerializationUtils.clone(transactionEntity);
        TransactionEntity save = transactionRepository.save(transactionEntity);
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            TransactionEntity save2 = transactionRepository.save(transactionEntity2);
        });
    }


    private Transaction createTr(String id) {
        Transaction tr = new Transaction();
        tr.setId(id);
        tr.setCurrencyCode("RUB");
        tr.setCreatedAt(now);
        tr.setAmount(new BigDecimal("100"));
        tr.setMadeOn(dateNow);
        return tr;
    }

}
