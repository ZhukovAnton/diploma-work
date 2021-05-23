package com.stanum.skrudzh.service.exchange_rate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stanum.skrudzh.jpa.model.ExchangeRateEntity;
import com.stanum.skrudzh.jpa.repository.ExchangeRateRepository;
import com.stanum.skrudzh.model.enums.EnvironmentEnum;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Scope("singleton")
@EnableScheduling
@EnableAsync
@RequiredArgsConstructor
public class ExchangeRateManagementService {

    private final ResourceLoader resourceLoader;

    private final ExchangeRateRepository exchangeRateRepository;

    private final ExchangeRateFinder exchangeRateFinder;

    @Value("${threebaskets.openexchangerates.key}")
    private String key;

    @Value("${threebaskets.env}")
    private String environment;

    @PostConstruct
    private void initialise() {
        update();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void update() {
        ConcurrentHashMap<String, BigDecimal> ratesToUsd = null;
        if (!environment.equals(EnvironmentEnum.production.name())) {
            Gson gson = new GsonBuilder().create();
            try {
                ratesToUsd = gson.fromJson(
                        new InputStreamReader(resourceLoader.getResource("classpath:rates.json").getInputStream()),
                        new TypeToken<ConcurrentHashMap<String, BigDecimal>>() {
                        }.getType());
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
        } else {
            ratesToUsd = Objects.requireNonNull(new RestTemplate()
                    .getForEntity("https://openexchangerates.org/api/latest.json" + "?app_id=" + key, ExchangeResponse.class)
                    .getBody()).getRates();
        }
        if (ratesToUsd == null) return;
        ratesToUsd.forEach((currencyTo, rate) -> {
            ExchangeRateEntity exchangeRateEntity = exchangeRateFinder
                    .findExchangeRateFromUsdTo(currencyTo)
                    .orElseGet(() -> new ExchangeRateEntity(CurrencyService.usdIsoCode, currencyTo));
            exchangeRateEntity.setRate(rate);
            exchangeRateEntity.setUpdatedAt(TimeUtil.now());
            exchangeRateEntity.setIsUpdated(true);
            save(exchangeRateEntity);
        });
    }

    private void save(ExchangeRateEntity exchangeRateEntity) {
        exchangeRateRepository.save(exchangeRateEntity);
    }

    @Data
    private static class ExchangeResponse {
        private String disclaimer;
        private String license;
        private Long timestamp;
        private String base;
        private ConcurrentHashMap<String, BigDecimal> rates;
    }

}
