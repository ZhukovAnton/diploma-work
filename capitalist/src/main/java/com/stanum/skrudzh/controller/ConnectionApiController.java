package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.ConnectionCreationForm;
import com.stanum.skrudzh.controller.form.ConnectionRefreshForm;
import com.stanum.skrudzh.controller.response.ConnectionResponse;
import com.stanum.skrudzh.controller.response.ConnectionsResponse;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.ConnectionDto;
import com.stanum.skrudzh.saltage.model.Connection;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionDtoService;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionRequestService;
import com.stanum.skrudzh.service.user.UserManagementService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConnectionApiController {

    private final ConnectionRequestService connectionRequestService;

    private final ConnectionDtoService connectionDtoService;

    @ApiOperation(value = "Retrieves users's connections",
            authorizations = {@Authorization(Constants.JWT_AUTH)},
            notes = "Returns a list of all users internet banking connections from salt-edge. \n " +
                    "Can be filtered with provider")
    @GetMapping(path = "/users/{userId}/connections")
    public ResponseEntity<ConnectionsResponse> getUserConnections(
            @ApiParam(required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "token", required = true) @RequestHeader(value = "Authorization") String authorization,
            @ApiParam(value = "SaltEdge provider id")
            @RequestParam(name = "provider_id", required = false) String saltEdgeProviderId) {
        log.info("Retrieve connection for userId = {}, saltEdgeProviderId = {}", userId, saltEdgeProviderId);
        Set<ConnectionEntity> dbConnections = connectionRequestService.getUsersConnectionsFromDbFilteredWithParam(userId, saltEdgeProviderId);
        List<ConnectionDto> connections;
        if (saltEdgeProviderId != null && dbConnections.isEmpty()) {
            Set<Connection> connectionsFromSaltEdge = connectionRequestService
                    .getUsersConnectionsFromSaltEdgeWithParam(userId, saltEdgeProviderId);
            connections = connectionDtoService.createConnectionsDto(connectionsFromSaltEdge);
        } else {
            connections = connectionDtoService.createConnectionsDto(dbConnections);
        }
        return new ResponseEntity<>(new ConnectionsResponse(connections), HttpStatus.OK);
    }

    @ApiOperation(value = "Creates saltedge connection",
            authorizations = {@Authorization(Constants.JWT_AUTH)},
            notes = "Creates saltEdge connection entity by salEdge connection id. " +
                    "All necessary information obtained from saltEdge api by connection saltEdge id")
    @PostMapping(path = "/users/{userId}/connections")
    public ResponseEntity<ConnectionResponse> createConnectionEntity(
            @ApiParam(required = true)
            @PathVariable("userId") Long userId,
            @ApiParam(value = "token", required = true)
            @RequestHeader(value = "Authorization") String authorization,
            @ApiParam(value = "creation form", required = true)
            @RequestBody ConnectionCreationForm payload) {
        log.info("Create saltedge connection for userId = {}, payload = {}", userId, payload);
        ConnectionEntity connectionEntity = connectionRequestService.createConnectionEntity(userId, payload.getConnection());
        ConnectionDto connectionDto = connectionDtoService.createConnectionDto(connectionEntity);
        return new ResponseEntity<>(new ConnectionResponse(connectionDto), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieves connection by api id",
            authorizations = {@Authorization(Constants.JWT_AUTH)},
            notes = "returns user internet banking connection by api id")
    @GetMapping(path = "/connections/{id}")
    public ResponseEntity<ConnectionResponse> getConnectionById(
            @ApiParam(value = "api id", required = true)
            @PathVariable("id") Long id,
            @ApiParam(value = "token", required = true)
            @RequestHeader(value = "Authorization") String authorization) {
        ConnectionEntity connectionEntity = connectionRequestService.getConnectionById(id);
        ConnectionDto connection = connectionDtoService.createConnectionDto(connectionEntity);
        return new ResponseEntity<>(new ConnectionResponse(connection), HttpStatus.OK);
    }

    @ApiOperation(value = "Refreshes connection by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PatchMapping("/connections/{id}")
    public ResponseEntity<Void> refreshConnection(
            @ApiParam(value = "token", required = true) @RequestHeader(value = "Authorization") String authorization,
            @ApiParam(value = "connection api id", required = true) @PathVariable(name = "id") Long id,
            @ApiParam(value = "is need to refresh accounts") @RequestBody ConnectionRefreshForm payload) {
        log.info("Refresh connection id = {}, payload = {}", id, payload);
        connectionRequestService.refreshConnection(id, payload.getConnection());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
