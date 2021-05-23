package com.stanum.skrudzh.service.saltedge.account;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountFinder {

    private final AccountRepository accountRepository;

    public Set<AccountEntity> findAccountsByConnection(ConnectionEntity connectionEntity) {
        return accountRepository.findByConnectionEntity(connectionEntity);
    }

    public AccountEntity findBySaltedgeAccountId(String saltEdgeAccountId) {
        return accountRepository.findBySaltEdgeAccountId(saltEdgeAccountId).orElse(null);
    }

    public AccountEntity findByApiId(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND));
    }

    public Set<AccountEntity> findByConnectionFilteredWithParams(UserEntity userEntity,
                                                                 String saltEdgeConnectionId,
                                                                 String currencyCode,
                                                                 Boolean notAttached) {
        return accountRepository
                .findByConnectionFilteredWithParams(userEntity,
                        saltEdgeConnectionId,
                        currencyCode,
                        notAttached);
    }

    public Set<AccountEntity> findByProviderFilteredWithParams(UserEntity userEntity,
                                                               String saltEdgeProviderId,
                                                               String currencyCode,
                                                               Boolean notAttached) {
        return accountRepository
                .findByProviderFilteredWithParams(userEntity,
                        saltEdgeProviderId,
                        currencyCode,
                        notAttached);
    }

    public Set<AccountEntity> findByUserFilteredWithParams(UserEntity userEntity,
                                                           String currencyCode,
                                                           Boolean notAttached) {
        return accountRepository
                .findByUserFilteredWithParams(userEntity,
                        currencyCode,
                        notAttached);
    }

}
