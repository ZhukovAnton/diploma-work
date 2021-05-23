package com.stanum.skrudzh.service.saltedge.account;

import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.NatureTypeEnum;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountRequestService {

    private final UserUtil userUtil;

    private final AccountFinder accountFinder;

    private final AccountManagementService managementService;

    public Set<AccountEntity> indexAccounts(Long userId, String connectionId, String providerId, String currencyCode, Boolean notAttached, NatureTypeEnum natureType) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        Set<AccountEntity> accountEntities;
        if (connectionId != null) {
            accountEntities = accountFinder
                    .findByConnectionFilteredWithParams(userEntity, connectionId, currencyCode, notAttached);
        } else if (providerId != null) {
            accountEntities = accountFinder
                    .findByProviderFilteredWithParams(userEntity, providerId, currencyCode, notAttached);
        } else {
            accountEntities = accountFinder
                    .findByUserFilteredWithParams(userEntity, currencyCode, notAttached);
        }
        return accountEntities.stream()
                .filter(accountEntity -> managementService.getNatureType(accountEntity).equals(natureType))
                .collect(Collectors.toSet());
    }

    public AccountEntity getAccountById(Long accountId) {
        AccountEntity accountEntity = accountFinder.findByApiId(accountId);
        userUtil.checkRightAccess(accountEntity.getConnectionEntity().getUser().getId());
        return accountEntity;
    }

}
