package com.stanum.skrudzh.service.exchange_rate;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.ExchangeRateEntity;
import com.stanum.skrudzh.model.dto.Currency;
import com.stanum.skrudzh.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRateFinder finder;

    public BigDecimal exchange(String fromCurrencyCode, String toCurrencyCode, BigDecimal amount) {
        Currency fromCurrency = CurrencyService.getCurrencyByIsoCode(fromCurrencyCode);
        Currency toCurrency = CurrencyService.getCurrencyByIsoCode(toCurrencyCode);
        BigDecimal differenceInSubunitToUnit = BigDecimal.valueOf(toCurrency.getSubunitToUnit())
                .divide(BigDecimal.valueOf(fromCurrency.getSubunitToUnit()), MathContext.DECIMAL64);
        if (fromCurrencyCode.equals(toCurrencyCode)) return amount;
        if (toCurrencyCode.equals(CurrencyService.usdIsoCode)) {
            return getUsdAmount(fromCurrencyCode, amount).multiply(differenceInSubunitToUnit);
        } else {
            ExchangeRateEntity exchangeRateFromUsdToToCurrency = finder.findExchangeRateFromUsdTo(toCurrencyCode)
                    .orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND, "Unknown currency " + toCurrency)); //must be unreachable
            return getUsdAmount(fromCurrencyCode, amount)
                    .multiply(exchangeRateFromUsdToToCurrency.getRate(), MathContext.DECIMAL64).multiply(differenceInSubunitToUnit);
        }
    }

    private BigDecimal getUsdAmount(String toCurrency, BigDecimal amount) {
        ExchangeRateEntity exchangeRateTo = finder.findExchangeRateFromUsdTo(toCurrency)
                .orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND, "Unknown currency " + toCurrency)); //must be unreachable
        return amount.divide(exchangeRateTo.getRate(), MathContext.DECIMAL64);
    }
}
