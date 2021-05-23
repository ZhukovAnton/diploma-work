package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.form.IncomeSourceCreationForm;
import com.stanum.skrudzh.controller.response.ExchangeRateResponse;
import com.stanum.skrudzh.controller.response.IncomeSourceResponse;
import com.stanum.skrudzh.model.dto.ExchangeRates;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class ExchangeRatesApiControllerTest extends IntegrationTest {

    @Autowired
    private ExchangeRatesApiController exchangeRatesApiController;

    @Autowired
    private IncomeSourcesApiController incomeSourcesApiController;

    @Test
    public void shouldFindByGet() {
        ExchangeRateResponse response = exchangeRatesApiController.exchangeRatesFindByGet("RUB", "USD", "").getBody();
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getExchangeRate().getRate());
    }

    @Test
    public void shouldFindAllUsersExchangeRates() {
        createIncomeSource("RUB", "Income 1");
        createIncomeSource("USD", "Income 2");
        createIncomeSource("CAD", "Income 3");
        ExchangeRates response = exchangeRatesApiController.getAllUsersUniqueExchangeRates("", user.getId()).getBody();
        Assert.assertNotNull(response);
        Assert.assertEquals(2, response.getExchangeRates().size());

    }

    private void createIncomeSource(String currency, String sourceName) {
        IncomeSourceCreationForm form = TestUtils.createIncomeForm(currency, sourceName, 1000L);
        ResponseEntity<IncomeSourceResponse> response = incomeSourcesApiController.createIncomeSource(user.getId(), form, null);
    }
}
