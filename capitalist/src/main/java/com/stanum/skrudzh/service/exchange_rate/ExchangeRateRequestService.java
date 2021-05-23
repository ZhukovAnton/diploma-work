package com.stanum.skrudzh.service.exchange_rate;

import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.Currency;
import com.stanum.skrudzh.service.active.ActiveFinder;
import com.stanum.skrudzh.service.borrow.BorrowFinder;
import com.stanum.skrudzh.service.credit.CreditFinder;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ExchangeRateRequestService {
    private final UserUtil userUtil;
    private final ExchangeService exchangeService;

    private final IncomeSourceFinder incomeSourceFinder;
    private final ExpenseSourceFinder expenseSourceFinder;
    private final ExpenseCategoryFinder expenseCategoryFinder;
    private final ActiveFinder activeFinder;
    private final BorrowFinder borrowFinder;
    private final CreditFinder creditFinder;

    public BigDecimal getRate(String from, String to) {
        Currency fromCurrency = CurrencyService.getCurrencyByIsoCode(from);
        Currency toCurrency = CurrencyService.getCurrencyByIsoCode(to);
        return exchangeService.exchange(from, to, BigDecimal.valueOf(fromCurrency.getSubunitToUnit()))
                .divide(BigDecimal.valueOf(toCurrency.getSubunitToUnit()), MathContext.DECIMAL64);
    }

    public Map<String, BigDecimal> getAllRatesToDefaultCurrency(Long userId) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        Set<String> allUsersCurrencies = getUniqueUsersCurrencies(userEntity);
        Map<String, BigDecimal> currenciesRates = new ConcurrentHashMap<>();
        allUsersCurrencies.forEach(currencyCode -> {
            if (currencyCode.equals(userEntity.getDefaultCurrency())) return;
            Currency currency = CurrencyService.getCurrencyByIsoCode(currencyCode);
            Currency defaultCurrency = CurrencyService.getCurrencyByIsoCode(userEntity.getDefaultCurrency());
            currenciesRates.put(currencyCode, exchangeService
                    .exchange(currencyCode, userEntity.getDefaultCurrency(), BigDecimal.valueOf(currency.getSubunitToUnit()))
                    .divide(BigDecimal.valueOf(defaultCurrency.getSubunitToUnit()), MathContext.DECIMAL64));
        });
        return currenciesRates;
    }

    public Set<String> getUniqueUsersCurrencies(UserEntity userEntity) {
        Set<String> uniqueUsersCurrencies = incomeSourceFinder.findAllIncomeSourcesCurrencies(userEntity);
        uniqueUsersCurrencies.addAll(expenseSourceFinder.findAllExpenseSourcesCurrencies(userEntity));
        uniqueUsersCurrencies.addAll(expenseCategoryFinder.findAllExpenseCategoriesCurrencies(userEntity));
        uniqueUsersCurrencies.addAll(activeFinder.findAllActivesCurrencies(userEntity));
        uniqueUsersCurrencies.addAll(borrowFinder.findAllBorrowsCurrencies(userEntity));
        uniqueUsersCurrencies.addAll(creditFinder.findAllCreditsCurrencies(userEntity));
        return uniqueUsersCurrencies;
    }

}
