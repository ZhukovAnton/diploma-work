package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.model.dto.Currencies;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.utils.constant.Constants;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequiredArgsConstructor
public class CurrenciesApiController {

    private final CurrencyService currencyService;

    private final ExchangeService exchangeService;

    @GetMapping(path = "/currencies")
    @ApiOperation(value = "Return currencies", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<Currencies> currenciesGet(
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) throws IOException {
        Currencies currenciesResponse = new Currencies(currencyService.getAllCurrencies());
        return new ResponseEntity<>(currenciesResponse, HttpStatus.OK);
    }

}
