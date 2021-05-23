package com.stanum.skrudzh.service.saltedge.account_connection;

import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.service.saltedge.account.AccountFinder;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountConnectionService {

    private final UserUtil userUtil;

    private final AccountFinder accountFinder;

    private final AccountConnectionFinder accountConnectionFinder;

    private final AccountConnectionManagementService managementService;

    public Optional<AccountConnectionEntity> indexAccountConnections(Long accountId) {
        AccountEntity accountEntity = accountFinder.findByApiId(accountId);
        userUtil.checkRightAccess(accountEntity.getConnectionEntity().getUser().getId());
        return accountConnectionFinder.findByAccount(accountEntity);
    }

    public AccountConnectionEntity getAccountConnectionById(Long id) {
        AccountConnectionEntity accountConnectionEntity = accountConnectionFinder.findById(id);
        userUtil.checkRightAccess(accountConnectionEntity.getAccountEntity()
                .getConnectionEntity().getUser().getId());
        return accountConnectionEntity;
    }

    public void destroyAccountConnectionById(Long id) {
        AccountConnectionEntity accountConnectionEntity = getAccountConnectionById(id);
        managementService.destroyAccountConnection(accountConnectionEntity);
    }

}
