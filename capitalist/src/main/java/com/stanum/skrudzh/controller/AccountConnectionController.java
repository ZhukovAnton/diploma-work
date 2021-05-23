package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.response.AccountConnectionResponse;
import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.model.dto.AccountConnection;
import com.stanum.skrudzh.model.dto.AccountConnections;
import com.stanum.skrudzh.service.saltedge.account_connection.AccountConnectionDtoService;
import com.stanum.skrudzh.service.saltedge.account_connection.AccountConnectionService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AccountConnectionController {

    private final AccountConnectionService accountConnectionService;

    private final AccountConnectionDtoService dtoService;

    @ApiOperation(value = "Retrieves accountConnection by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/account_connections/{id}")
    public ResponseEntity<AccountConnectionResponse> getAccountConnectionById(
            @ApiParam(value = "token", required = true) @RequestHeader(value = "Authorization") String authorization,
            @ApiParam(value = "id", required = true) @PathVariable(name = "id") Long accountConnectionId) {
        AccountConnectionEntity accountConnectionEntity = accountConnectionService.getAccountConnectionById(accountConnectionId);
        AccountConnection accountConnection = dtoService.createAccountConnectionDto(accountConnectionEntity);
        ResponseEntity<AccountConnectionResponse> response = new ResponseEntity<>(new AccountConnectionResponse(accountConnection), HttpStatus.OK);
        return response;
    }

    @ApiOperation(value = "Retrieves accountConnection by account api id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/accounts/{accountId}/account_connections")
    public ResponseEntity<AccountConnections> indexAccountConnections(
            @ApiParam(value = "token", required = true) @RequestHeader(value = "Authorization") String authorization,
            @ApiParam(value = "api id", required = true) @PathVariable(name = "accountId") Long accountId) {
        Optional<AccountConnectionEntity> accountConnectionEntityOptional = accountConnectionService.indexAccountConnections(accountId);
        ResponseEntity<AccountConnections> response = new ResponseEntity<>(new AccountConnections(accountConnectionEntityOptional.
                map(accountConnectionEntity -> Collections.singletonList(dtoService
                        .createAccountConnectionDto(accountConnectionEntity)))
                .orElse(Collections.emptyList())),
                HttpStatus.OK);
        return response;
    }

    @ApiOperation(value = "Destroy accountConnection by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @DeleteMapping(path = "/account_connections/{id}")
    public ResponseEntity<Void> destroyAmountConnectionById(
            @ApiParam(value = "token", required = true) @RequestHeader(value = "Authorization") String authorization,
            @ApiParam(value = "id", required = true) @PathVariable(name = "id") Long accountConnectionId) {
        accountConnectionService.destroyAccountConnectionById(accountConnectionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
