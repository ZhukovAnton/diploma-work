package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.controller.CurrenciesApiController;
import com.stanum.skrudzh.model.dto.Currencies;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class CurrenciesApiControllerTest extends IntegrationTest {

    @Autowired
    private CurrenciesApiController currenciesApiController;

    @Test
    public void shouldReturnAllCurrencies() throws Exception{
        ResponseEntity<Currencies> currenciesResponseEntity = currenciesApiController.currenciesGet("");
        Assert.assertNotNull(currenciesResponseEntity.getBody());
        Assert.assertEquals(169, currenciesResponseEntity.getBody().getCurrencies().size());
    }

}
