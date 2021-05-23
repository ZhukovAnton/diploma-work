package com.stanum.skrudzh.service.saltedge.connection;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.ConnectionsRepository;
import com.stanum.skrudzh.saltage.SaltedgeAPI;
import com.stanum.skrudzh.saltage.model.Connection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionFinder {

    private final ConnectionsRepository connectionsRepository;

    private final SaltedgeAPI saltedgeAPI;

    public ConnectionEntity findById(Long id) {
        return connectionsRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND, "Connection with id " + id + "not found"));
    }

    public Set<ConnectionEntity> findByUserAndProvider(UserEntity userEntity, String saltEdgeProviderId) {
        return connectionsRepository.findByUserAndProviderId(userEntity, saltEdgeProviderId);
    }

    public Optional<ConnectionEntity> findBySaltEdgeId(String saltEdgeConnectionId) {
        return connectionsRepository.findBySaltEdgeConnectionId(saltEdgeConnectionId);
    }

    public ConnectionEntity findBySaltEdgeIdOrThrow(String saltEdgeConnectionId) {
        return findBySaltEdgeId(saltEdgeConnectionId).orElseThrow(
                () -> {
                    log.error("SaltEdge connection id {} not found", saltEdgeConnectionId);
                    return new AppException(HttpAppError.NOT_FOUND, "SaltEdge connection id " + saltEdgeConnectionId + " not found");
                }
        );
    }

    public Set<ConnectionEntity> findAllUsersConnections(UserEntity userEntity) {
        return connectionsRepository.findByUser(userEntity);
    }

    public Set<Connection> findConnectionsInSaltEdge(UserEntity userEntity, String saltEdgeProviderId) {
        return new HashSet<>(
                saltedgeAPI
                        .custom
                        .findAllConnectionsByProviderId(
                                userEntity.getSaltEdgeCustomerId(),
                                saltEdgeProviderId));
    }

}
