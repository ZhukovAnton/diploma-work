package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    @Query("from AccountEntity account " +
            "left join ConnectionEntity connection on account.connectionEntity = connection " +
            "left join AccountConnectionEntity accountConnection on account = accountConnection.accountEntity " +
            "where connection.saltEdgeConnectionId = :connectionId and connection.user = :userEntity " +
            "and ((:currencyCode is null) or (account.currencyCode = :currencyCode)) " +
            "and ((:notAttached is null) " +
            "or (accountConnection is null and :notAttached = true) or (accountConnection is not null and :notAttached = false))")
    Set<AccountEntity> findByConnectionFilteredWithParams(UserEntity userEntity, String connectionId, String currencyCode, Boolean notAttached);

    @Query("from AccountEntity account " +
            "left join ConnectionEntity connection on account.connectionEntity = connection " +
            "left join AccountConnectionEntity accountConnection on account = accountConnection.accountEntity " +
            "where connection.providerId is not null and connection.providerId = :providerId " +
            "and connection.user = :userEntity " +
            "and ((:currencyCode is null) or (account.currencyCode = :currencyCode)) " +
            "and ((:notAttached is null) " +
            "or (accountConnection is null and :notAttached = true) or (accountConnection is not null and :notAttached = false))")
    Set<AccountEntity> findByProviderFilteredWithParams(UserEntity userEntity, String providerId, String currencyCode, Boolean notAttached);

    @Query("from AccountEntity account " +
            "left join ConnectionEntity connection on account.connectionEntity = connection " +
            "left join AccountConnectionEntity accountConnection on account = accountConnection.accountEntity " +
            "where connection.user = :userEntity " +
            "and ((:currencyCode is null) or (account.currencyCode = :currencyCode)) " +
            "and ((:notAttached is null) " +
            "or (accountConnection is null and :notAttached = true) or (accountConnection is not null and :notAttached = false))")
    Set<AccountEntity> findByUserFilteredWithParams(UserEntity userEntity, String currencyCode, Boolean notAttached);

    Set<AccountEntity> findByConnectionEntity(ConnectionEntity connectionEntity);

    @Query("from AccountEntity where accountId = :accountId")
    Optional<AccountEntity> findBySaltEdgeAccountId(String accountId);
}
