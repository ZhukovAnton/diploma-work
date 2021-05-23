package com.stanum.skrudzh.localized_values;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;

@SpringBootTest
public class LocalizedValuesCacheTest {

    @Autowired
    private LocalizedValuesCache cache;

    @Test
    public void shouldReturnCachedValue() {
        String cachedValue = cache.get("activerecord.defaults.models.transactionable_example.expense_category.attributes.name.pets", "ru");
        Assert.assertEquals("Животные", cachedValue);
    }

    @Test
    public void shouldReturnDefaultLocaleValue() {
        String cachedValue = cache.get("activerecord.defaults.models.transactionable_example.expense_category.attributes.name.pets");
        Assert.assertEquals("Pets", cachedValue);
    }

    @Test
    public void shouldReturnDefault_ifLocaleNull() {
        Locale locale = null;
        String cachedValue = cache.get("activerecord.defaults.models.transactionable_example.expense_category.attributes.name.pets", locale);
        Assert.assertEquals("Pets", cachedValue);
    }

    @Test
    public void shouldReturnDefault_ifLocaleStringNull() {
        String locale = null;
        String cachedValue = cache.get("activerecord.defaults.models.transactionable_example.expense_category.attributes.name.pets", locale);
        Assert.assertEquals("Pets", cachedValue);
    }

    @Test
    public void shouldFallback_ifNameNotFound() {
        String locale = null;
        String name = "RandomName";
        String cachedValue = cache.get(name, locale);
        Assert.assertEquals(name, cachedValue);
    }

    @Test
    public void shouldNotFallback_ifDescription() {
        String locale = null;
        String name = "activerecord.defaults.models.transactionable_example.expense_category.attributes.description.psaasdasets";
        String cachedValue = cache.get(name, locale);
        Assert.assertNull(cachedValue);
    }
}
