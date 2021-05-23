package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.response.ExchangeRateResponse;
import com.stanum.skrudzh.model.dto.ExchangeRate;
import com.stanum.skrudzh.model.dto.ExchangeRates;
import com.stanum.skrudzh.service.exchange_rate.ExchangeRateRequestService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.constant.Constants;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequiredArgsConstructor
public class ExchangeRatesApiController {

    private final ExchangeService exchangeService;

    private final ExchangeRateRequestService exchangeRateRequestService;

    @ApiOperation(value = "Retrieves exchange rate", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/exchange_rates/find_by")
    public ResponseEntity<ExchangeRateResponse> exchangeRatesFindByGet(
            @ApiParam(value = "", required = true)
            @RequestParam(value = "from", required = true)
                    String from,
            @ApiParam(value = "", required = true)
            @RequestParam(value = "to", required = true)
                    String to,
            @ApiParam(value = "", required = true)
            @RequestHeader(value = "Authorization", required = true)
                    String authorization) {

        BigDecimal rate = exchangeRateRequestService.getRate(from, to);
        ExchangeRate exchangeRate = new ExchangeRate(from, to, rate);
        return new ResponseEntity<>(new ExchangeRateResponse(exchangeRate), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieves exchange rate", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/users/{user_id}/exchange_rates")
    public ResponseEntity<ExchangeRates> getAllUsersUniqueExchangeRates(
            @ApiParam(value = "", required = true)
            @RequestHeader(value = "Authorization", required = true)
                    String authorization,
            @ApiParam(value = "", required = true)
            @PathVariable("user_id")
                    Long userId) {
        Map<String, BigDecimal> exchangeRatesToDefaultCurrency = exchangeRateRequestService
                .getAllRatesToDefaultCurrency(userId);
        ExchangeRates exchangeRates = new ExchangeRates(exchangeRatesToDefaultCurrency
                .entrySet()
                .stream()
                .map(entry -> new ExchangeRate(
                        entry.getKey(),
                        RequestUtil.getUser().getDefaultCurrency(),
                        entry.getValue()))
                .collect(Collectors.toList()));
        return new ResponseEntity<>(exchangeRates, HttpStatus.OK);
    }


}
