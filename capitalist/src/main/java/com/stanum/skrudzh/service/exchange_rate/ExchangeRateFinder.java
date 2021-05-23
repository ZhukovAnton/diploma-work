package com.stanum.skrudzh.service.exchange_rate;

import com.stanum.skrudzh.jpa.model.ExchangeRateEntity;
import com.stanum.skrudzh.jpa.repository.ExchangeRateRepository;
import com.stanum.skrudzh.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ExchangeRateFinder {

    private final ExchangeRateRepository exchangeRateRepository;

    public Optional<ExchangeRateEntity> findExchangeRateFromUsdTo(String currencyCode) {
        return exchangeRateRepository.findByFromAndTo(CurrencyService.usdIsoCode, currencyCode);
    }

    public Set<String> findExchangeRatesCurrencies() {
        return exchangeRateRepository.findExchangeRateCurrencies();
    }
}
