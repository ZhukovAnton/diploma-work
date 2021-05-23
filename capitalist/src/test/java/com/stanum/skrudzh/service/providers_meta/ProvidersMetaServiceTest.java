package com.stanum.skrudzh.service.providers_meta;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProvidersMetaServiceTest {

    @Autowired
    private ProvidersMetaService providersMetaService;

    @Test
    public void shouldFillProviderCodes() {
        List<String> tinkoff_ru = providersMetaService.getProviders("tinkoff_ru");
        Assert.assertEquals("tinkoff_ru", tinkoff_ru.get(0));
    }

    @Test
    public void shouldReturnNullForCash() {
        List<String> cash = providersMetaService.getProviders("Cash");
        Assert.assertNull(cash);
    }

    @Test
    public void shouldReturnNullIfProviderDoesntExist() {
        List<String> cash = providersMetaService.getProviders("rocketbank");
        Assert.assertNull(cash);
    }
}
