package com.stanum.skrudzh.service.expense_source;

import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.model.dto.Currency;
import com.stanum.skrudzh.model.dto.ExpenseSource;
import com.stanum.skrudzh.model.dto.ExpenseSources;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.providers_meta.ProvidersMetaService;
import com.stanum.skrudzh.service.saltedge.account_connection.AccountConnectionDtoService;
import com.stanum.skrudzh.service.saltedge.connectable.ConnectableCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseSourceDtoService {

    private final AccountConnectionDtoService accountConnectionDtoService;

    private final ConnectableCalculationService calculationService;

    private final ProvidersMetaService providersMetaService;

    private final ExpenseSourceTransactionsService transactionsService;

    public ExpenseSources createExpenseSourcesResponse(Set<ExpenseSourceEntity> expenseSourceEntities) {
        return new ExpenseSources(expenseSourceEntities
                .stream()
                .map(this::createExpenseSourceResponse)
                .collect(Collectors.toList()));
    }

    public ExpenseSource createExpenseSourceResponse(ExpenseSourceEntity expenseSourceEntity) {
        ExpenseSource expenseSource = new ExpenseSource(expenseSourceEntity);
        expenseSource.setHasTransactions(transactionsService.hasTransactions(expenseSourceEntity.getId(), EntityTypeEnum.ExpenseSource.name()));
        Currency currency = CurrencyService
                .getCurrencyByIsoCode(expenseSourceEntity.getCurrency());
        Timestamp fetchFromDateTimestamp = calculationService.calculateFetchFromDate(expenseSourceEntity);
        ZonedDateTime fetchFromDate = fetchFromDateTimestamp != null
                ? ZonedDateTime.of(fetchFromDateTimestamp.toLocalDateTime(), ZoneId.of("Z"))
                : null;
        expenseSource.setCurrency(currency);
        expenseSource.setAmount(CurrencyService.getReadableAmount(expenseSourceEntity.getAmountCents(), currency));
        expenseSource.setFetchFromDate(fetchFromDate);

        //backward compatibility
        expenseSource.setFetchDataFrom(fetchFromDate);
        if (expenseSourceEntity.getAccountConnectionEntity() != null)
            expenseSource.setSaltEdgeAccountConnection(accountConnectionDtoService
                    .createAccountConnectionDto(expenseSourceEntity.getAccountConnectionEntity()));
        expenseSource.setProviderCodes(providersMetaService.getProviders(expenseSourceEntity.getPrototypeKey()));
        expenseSource.setPrototypeKey(expenseSourceEntity.getPrototypeKey());
        return expenseSource;
    }

}
