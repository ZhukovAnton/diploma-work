package com.stanum.skrudzh.service.saltedge.connection;

import com.stanum.skrudzh.controller.form.ConnectionCreationForm;
import com.stanum.skrudzh.controller.form.ConnectionRefreshForm;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.saltage.model.Connection;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionRequestService {

    private final ConnectionFinder connectionFinder;

    private final ConnectionService connectionService;

    private final UserUtil userUtil;

    public ConnectionEntity createConnectionEntity(Long userId, ConnectionCreationForm.ConnectionCF form) {
        return connectionService.createConnectionEntity(checkRightAccess(userId), form);
    }

    public Set<ConnectionEntity> getUsersConnectionsFromDbFilteredWithParam(Long userId, String saltEdgeProviderId) {
        return connectionFinder.findByUserAndProvider(checkRightAccess(userId), saltEdgeProviderId);
    }

    public Set<Connection> getUsersConnectionsFromSaltEdgeWithParam(Long userId, String saltEdgeProviderId) {
        log.info("Get connections from SaltEdge, userId={}, saltedgeProviderId={}", userId, saltEdgeProviderId);
        return connectionFinder.findConnectionsInSaltEdge(checkRightAccess(userId), saltEdgeProviderId);
    }

    public ConnectionEntity getConnectionById(Long connectionId) {
        ConnectionEntity connectionEntity = connectionFinder.findById(connectionId);
        userUtil.checkRightAccess(connectionEntity.getUser().getId());
        return connectionEntity;
    }

    public void refreshConnection(Long connectionId, ConnectionRefreshForm.ConnectionRF form) {
        ConnectionEntity connectionEntity = connectionFinder.findById(connectionId);
        userUtil.checkRightAccess(connectionEntity.getUser().getId());
        if(form.getInteractiveCredentials() != null) {
            connectionService.interactive(connectionEntity, form);
        } else {
            connectionService.refreshConnection(connectionEntity, form);
        }
    }

    private UserEntity checkRightAccess(Long userId) {
        userUtil.checkRightAccess(userId);
        return RequestUtil.getUser();
    }
}
