package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface ConnectionsRepository extends JpaRepository<ConnectionEntity, Long> {

    @Query("from ConnectionEntity connection " +
            "where connection.user = :userEntity " +
            "and ((:saltEdgeProviderId is null) " +
            "or (connection.providerId is not null and connection.providerId = :saltEdgeProviderId))")
    Set<ConnectionEntity> findByUserAndProviderId(UserEntity userEntity, String saltEdgeProviderId);

    Set<ConnectionEntity> findByUser(UserEntity user);

    Optional<ConnectionEntity> findBySaltEdgeConnectionId(String saltEdgeConnectionId);
}
