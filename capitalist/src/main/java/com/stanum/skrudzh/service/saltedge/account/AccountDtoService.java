package com.stanum.skrudzh.service.saltedge.account;

import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.model.dto.Account;
import com.stanum.skrudzh.model.dto.Accounts;
import com.stanum.skrudzh.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountDtoService {

    private final CurrencyService currencyService;

    public Account createAccountConnectionDto(AccountEntity accountEntity) {
        Account account = new Account(accountEntity);
        account.setCurrency(currencyService.getCurrencyByIsoCode(accountEntity.getCurrencyCode()));
        return account;
    }

    public Accounts createAccountConnectionsDto(Set<AccountEntity> accountConnectionEntities) {
        return new Accounts(accountConnectionEntities.stream()
                .map(this::createAccountConnectionDto)
                .collect(Collectors.toList()));
    }
}
