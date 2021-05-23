package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.response.AccountResponse;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.model.dto.Account;
import com.stanum.skrudzh.model.dto.Accounts;
import com.stanum.skrudzh.model.enums.NatureTypeEnum;
import com.stanum.skrudzh.service.saltedge.account.AccountDtoService;
import com.stanum.skrudzh.service.saltedge.account.AccountRequestService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountRequestService accountRequestService;

    private final AccountDtoService accountDtoService;

    @ApiOperation(value = "index accounts", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping("/users/{userId}/accounts")
    public ResponseEntity<Accounts> indexAccounts(
            @ApiParam(value = "token", required = true)
            @RequestHeader String authorization,
            @ApiParam(value = "user id", required = true)
            @PathVariable(name = "userId") Long userId,
            @ApiParam(value = "saltEdge connection id whose accounts are to be returned")
            @RequestParam(name = "connection_id", required = false) String connectionId,
            @ApiParam(value = "true - will returned not attached to any expense source or active. \n" +
                    "false - attached to any expense source or active. null - all.")
            @RequestParam(name = "not_attached", required = false) Boolean notAttached,
            @ApiParam(value = "saltEdge provider id whose accounts are to be returned")
            @RequestParam(name = "provider_id", required = false) String providerId,
            @ApiParam(value = "filter accounts by currency")
            @RequestParam(name = "currency", required = false) String currencyCode,
            @ApiParam(value = "filter accounts by nature type")
            @RequestParam(name = "nature_type", required = false) NatureTypeEnum natureType) {
        Set<AccountEntity> accountConnectionEntities = accountRequestService
                .indexAccounts(userId, connectionId, providerId, currencyCode, notAttached, natureType);

        Accounts response = accountDtoService
                .createAccountConnectionsDto(accountConnectionEntities);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "retrieves account by api id",
            authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping("/accounts/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
            @ApiParam(value = "token", required = true)
            @RequestHeader String authorization,
            @ApiParam(value = "account connection api id", required = true)
            @PathVariable(name = "id") Long id) {
        AccountEntity accountEntity = accountRequestService.getAccountById(id);
        Account account = accountDtoService
                .createAccountConnectionDto(accountEntity);

        return new ResponseEntity<>(new AccountResponse(account), HttpStatus.OK);
    }
}
