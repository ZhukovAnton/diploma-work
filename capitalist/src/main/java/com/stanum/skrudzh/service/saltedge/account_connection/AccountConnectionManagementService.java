package com.stanum.skrudzh.service.saltedge.account_connection;

import com.stanum.skrudzh.controller.form.saltedge.AccountConnectionAttributes;
import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.jpa.model.base.Connectable;
import com.stanum.skrudzh.saltage.model.Transaction;

import java.util.List;

public interface AccountConnectionManagementService {
    void updateOrCreateAccountConnection(Connectable connectable, AccountConnectionAttributes accountAttributes);

    void updateAccountConnectionWithAccount(
            AccountConnectionEntity accountConnectionEntity,
            AccountEntity accountEntity,
            List<Transaction> transactionsFromSaltEdge) ;

    void save(AccountConnectionEntity accountConnectionEntity);

    void destroyAccountConnection(AccountConnectionEntity accountConnectionEntity);

    void destroyAccountConnection(AccountConnectionEntity accountConnectionEntity, Connectable connectable);
}
