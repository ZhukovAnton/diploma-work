package com.stanum.skrudzh.service.expense_source;

import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExpenseSourceCalculationService {
    private final ExchangeService exchangeService;

    public BigDecimal expenseSourcesSummaryBalance(Set<ExpenseSourceEntity> expenseSourceEntities, String defaultCurrency) {
        return expenseSourceEntities.stream()
                .map(expenseSourceEntity -> {
                    var amount = expenseSourceEntity.getAmountCents() == null
                            ? BigDecimal.ZERO
                            : expenseSourceEntity.getAmountCents()
                            .add(expenseSourceEntity.getCreditLimitCents().negate());
                    return exchangeService.exchange(expenseSourceEntity.getCurrency(), defaultCurrency, amount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
