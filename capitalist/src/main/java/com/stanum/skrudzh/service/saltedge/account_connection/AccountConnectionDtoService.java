package com.stanum.skrudzh.service.saltedge.account_connection;

import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.model.dto.AccountConnection;
import com.stanum.skrudzh.service.saltedge.account.AccountDtoService;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionDtoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountConnectionDtoService {

    private final AccountDtoService accountDtoService;

    private final ConnectionDtoService connectionDtoService;

    public AccountConnection createAccountConnectionDto(AccountConnectionEntity accountConnectionEntity) {
        AccountConnection accountConnection = new AccountConnection(accountConnectionEntity);
        if (accountConnectionEntity.getAccountEntity() != null) {
            accountConnection.setAccount(accountDtoService
                    .createAccountConnectionDto(accountConnectionEntity.getAccountEntity()));
        }
        if (accountConnectionEntity.getConnectionEntity() != null) {
            accountConnection.setConnection(connectionDtoService
                    .createConnectionDto(accountConnectionEntity.getConnectionEntity()));
        }
        return accountConnection;
    }

}
