package com.stanum.skrudzh.service.currency;

import com.stanum.skrudzh.IntegrationTest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CurrencyServiceTest extends IntegrationTest {

    @Autowired
    private CurrencyService currencyService;

    @Test
    public void shouldGetCurrencyByIsoCode() {
        Assert.assertNotNull(CurrencyService.getCurrencyByIsoCode("RUB"));
    }
}
