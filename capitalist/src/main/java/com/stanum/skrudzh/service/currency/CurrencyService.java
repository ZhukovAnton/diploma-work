package com.stanum.skrudzh.service.currency;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.Currencies;
import com.stanum.skrudzh.model.dto.Currency;
import com.stanum.skrudzh.service.exchange_rate.ExchangeRateFinder;
import com.stanum.skrudzh.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@DependsOn("exchangeRateManagementService")
public class CurrencyService {
    public static final String rubIsoCode = "RUB";
    public static final String usdIsoCode = "USD";
    private static final ConcurrentHashMap<String, Currency> currencies = new ConcurrentHashMap<>();

    private final ResourceLoader resourceLoader;
    private final ExchangeRateFinder exchangeRateFinder;

    public static Currency getCurrencyByIsoCode(String isoCode) {
        if (currencies.containsKey(isoCode)) {
            Currency currencyToReturn = currencies.get(isoCode);
            if (currencyToReturn.getIsoCode().equals(rubIsoCode)) {
                Locale locale = RequestUtil.getLocale();
                currencyToReturn.setTranslatedName(ResourceBundle.getBundle("messages", locale).getString("rub"));
            }
            return currencies.get(isoCode);
        } else {
            throw new AppException(HttpAppError.NOT_FOUND);
        }
    }

    public static List<Currency> getAllCurrencies() {
        UserEntity userEntity = RequestUtil.getUser();
        List<Currency> currenciesResponse = new ArrayList<>(currencies.values());
        currenciesResponse.sort(Comparator.comparing(Currency::getPriority).thenComparing(Currency::getName));
        currenciesResponse.remove(currencies.get(userEntity.getDefaultCurrency()));
        currenciesResponse.add(0, currencies.get(userEntity.getDefaultCurrency()));
        currenciesResponse
                .get(currenciesResponse.indexOf(currencies.get(rubIsoCode)))
                .setTranslatedName(ResourceBundle.getBundle("messages", RequestUtil.getLocale()).getString("rub"));
        return currenciesResponse;
    }

    public static BigDecimal getAmountInCents(BigDecimal amount, String currencyCode) {
        return amount.multiply(BigDecimal.valueOf(getCurrencyByIsoCode(currencyCode).getSubunitToUnit()))
                .setScale(0, RoundingMode.CEILING);
    }

    public static String getReadableAmount(BigDecimal amount, String currencyCode) {
        return getReadableAmount(amount, getCurrencyByIsoCode(currencyCode));
    }

    public static String getReadableAmount(BigDecimal amountOfCents, Currency currency) {
        BigDecimal integerPart;
        BigDecimal fractionalPart = null;

        StringBuilder result = new StringBuilder();

        if (currency.getSubunitToUnit() > 1) {
            BigDecimal divisor = new BigDecimal(currency.getSubunitToUnit());
            BigDecimal rationalUnitsAmount = amountOfCents.divide(divisor, MathContext.DECIMAL128);
            fractionalPart = rationalUnitsAmount
                    .remainder(BigDecimal.ONE)
                    .movePointRight(rationalUnitsAmount.scale())
                    .abs();
            integerPart = new BigDecimal(rationalUnitsAmount.longValue());
        } else {
            integerPart = amountOfCents;
        }

        String thousandsSeparator = currency.getThousandsSeparator();

        if (thousandsSeparator != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            result.append(decimalFormat.format(integerPart));
            if (!thousandsSeparator.equals(",")) {
                result = new StringBuilder(result.toString().replace(",", thousandsSeparator));
            }
        }

        if (fractionalPart != null && !fractionalPart.equals(BigDecimal.ZERO)) {
            result.append(currency.getDecimalMark()).append(fractionalPart.toString());
        }

        if (currency.getSymbol() != null) {
            if (currency.getSymbolFirst()) {
                result.insert(0, currency.getSymbol());
            } else {
                result.append(" ").append(currency.getSymbol());
            }
        }

        return result.toString();
    }

    @PostConstruct
    private void checkAndInitialise() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        Gson gson = gsonBuilder.create();
        Currencies currenciesFromJson;
        try {
            currenciesFromJson = gson.fromJson(
                    new InputStreamReader(resourceLoader.getResource("classpath:currency.json").getInputStream()),
                    new TypeToken<Currencies>() {
                    }.getType());
            Set<String> exchangeRateCurrencies = exchangeRateFinder.findExchangeRatesCurrencies();
            currenciesFromJson.getCurrencies().forEach(currency -> {
                if (!exchangeRateCurrencies.contains(currency.getIsoCode())) return;
                currencies.put(currency.getIsoCode(), currency);
            });
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            RuntimeException e = new RuntimeException();
            e.addSuppressed(ex);
            throw e;
        }
    }
}
