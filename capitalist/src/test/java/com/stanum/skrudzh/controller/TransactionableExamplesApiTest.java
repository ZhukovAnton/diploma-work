package com.stanum.skrudzh.controller;

import com.google.common.collect.Ordering;
import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.model.dto.TransactionableExample;
import com.stanum.skrudzh.model.dto.TransactionableExamples;
import com.stanum.skrudzh.model.dto.base.Ordered;
import com.stanum.skrudzh.utils.RequestUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@SpringBootTest
public class TransactionableExamplesApiTest extends IntegrationTest {

    @Autowired
    private TransactionableExpamlesApiController transactionableExpamlesApiController;

    @Test
    public void shouldGetExamples() {
        TransactionableExamples examples = transactionableExpamlesApiController
                .getTransactionableExamples("ExpenseCategory", null, null, false, "")
                .getBody();

        Assert.assertNotNull(examples);
        Assert.assertFalse(examples.getTransactionableExamples().isEmpty());
    }

    @Test
    public void shouldFillLocalizedValues() {
        RequestUtil.setLocale(new Locale("ru"));
        TransactionableExamples examples = transactionableExpamlesApiController
                .getTransactionableExamples("ExpenseSource", null, "RU", false, "")
                .getBody();

        Assert.assertNotNull(examples);
        Assert.assertFalse(examples.getTransactionableExamples().isEmpty());

        boolean found = false;
        for(TransactionableExample ex : examples.getTransactionableExamples()) {
            if(ex.getName().equals("AK BARS")) {
                found = true;
                Assert.assertEquals("АК БАРС", ex.getLocalizedName());
                break;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void shouldSortExamples() {
        TransactionableExamples examples = transactionableExpamlesApiController
                .getTransactionableExamples("ExpenseSource", null, "RU", false, "")
                .getBody();

        Assert.assertNotNull(examples);
        assertTrue(Ordering.natural()
                .isOrdered(examples.getTransactionableExamples().stream()
                        .map(Ordered::getRowOrder).collect(Collectors.toList())));
    }

    @Test
    public void shouldFillProviderCodes() {
        TransactionableExamples examples = transactionableExpamlesApiController
                .getTransactionableExamples("ExpenseSource", null, "RU", false, "")
                .getBody();

        Assert.assertNotNull(examples);
        Assert.assertFalse(examples.getTransactionableExamples().isEmpty());

        boolean found = false;
        for(TransactionableExample tr :examples.getTransactionableExamples()) {
            if(tr.getName().equals("Sberbank")) {
                found = true;
                Assert.assertEquals(2, tr.getProviderCodes().size());
            }
        }
        Assert.assertTrue(found);
    }

}
