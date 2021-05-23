package com.stanum.skrudzh.service.saltedge.account_connection;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.repository.AccountConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountConnectionFinder {

    private final AccountConnectionRepository accountConnectionRepository;

    public Optional<AccountConnectionEntity> findByAccount(AccountEntity accountEntity) {
        return accountConnectionRepository.findByAccountEntity(accountEntity);
    }

    public AccountConnectionEntity findById(Long id) {
        return accountConnectionRepository.findById(id).orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND));
    }
}
