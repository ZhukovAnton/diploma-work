package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.model.dto.CreditTypes;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CreditTypesApiControllerTest extends IntegrationTest {

    @Autowired
    private CreditTypesApiController creditTypesApiController;

    @Test
    public void shouldReturnCreditTypes() {
        CreditTypes creditTypes = creditTypesApiController.getCreditTypes().getBody();
        Assert.assertNotNull(creditTypes);
        Assert.assertEquals(3, creditTypes.getCreditTypes().size());
    }
}
