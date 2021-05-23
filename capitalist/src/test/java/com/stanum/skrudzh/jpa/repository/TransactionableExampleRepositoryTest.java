package com.stanum.skrudzh.jpa.repository;

import com.google.common.collect.Ordering;
import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.jpa.model.Rankable;
import com.stanum.skrudzh.jpa.model.TransactionableExampleEntity;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@SpringBootTest
public class TransactionableExampleRepositoryTest extends IntegrationTest {

    @Autowired
    private TransactionableExampleRepository transactionableExampleRepository;

    @Test
    public void shouldSortByRowOrder() {
        List<TransactionableExampleEntity> expenseTransactionalEntities = transactionableExampleRepository
                .findAllByTypeCountryAndCreateByDefault(
                        EntityTypeEnum.ExpenseSource.name(),
                        false,
                        "RU");



        assertTrue(Ordering.natural()
                .isOrdered(expenseTransactionalEntities.stream()
                        .map(Rankable::getRowOrder).collect(Collectors.toList())));
    }

}
