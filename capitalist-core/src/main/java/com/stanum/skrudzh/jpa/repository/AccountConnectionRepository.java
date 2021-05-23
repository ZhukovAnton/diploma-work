package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountConnectionRepository extends JpaRepository<AccountConnectionEntity, Long> {

    Optional<AccountConnectionEntity> findByAccountEntity(AccountEntity accountEntity);

}
