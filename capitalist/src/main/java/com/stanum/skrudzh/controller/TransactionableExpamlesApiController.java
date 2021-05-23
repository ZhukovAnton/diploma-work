package com.stanum.skrudzh.controller;

import com.google.common.base.Strings;
import com.stanum.skrudzh.jpa.model.TransactionableExampleEntity;
import com.stanum.skrudzh.localized_values.LocalizedValuesCache;
import com.stanum.skrudzh.metrics.Metric;
import com.stanum.skrudzh.metrics.MetricType;
import com.stanum.skrudzh.metrics.MetricsService;
import com.stanum.skrudzh.model.dto.TransactionableExample;
import com.stanum.skrudzh.model.dto.TransactionableExamples;
import com.stanum.skrudzh.service.providers_meta.ProvidersMetaService;
import com.stanum.skrudzh.service.transactionable.TransactionableExampleRequestService;
import com.stanum.skrudzh.service.transactionable.TransactionableExampleService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Slf4j
public class TransactionableExpamlesApiController {

    private final TransactionableExampleRequestService requestService;

    private final TransactionableExampleService managementService;

    private final ProvidersMetaService providersMetaService;

    private final LocalizedValuesCache cache;

    @ApiOperation(value = "Load transactionable examples", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/transactionable_examples")
    public ResponseEntity<TransactionableExamples> getTransactionableExamples(
            @ApiParam(value = "transactionable_type") @RequestParam(value = "transactionable_type", required = false) String transactionableType,
            @ApiParam(value = "basket_type") @RequestParam(value = "basket_type", required = false) String basketType,
            @ApiParam(value = "country") @RequestParam(value = "country", required = false) String country,
            @ApiParam(value = "is_used") @RequestParam(value = "is_used", required = false) Boolean isUsed,
            @ApiParam(value = "authorization", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Locale locale = RequestUtil.getLocale();
        String lang;
        if(locale == null) {
            lang = "en";
        } else {
            lang = locale.toLanguageTag();
        }
        log.info("Get templates, type={}, basketType={}, country={}, isUsed={}", transactionableType,
                basketType,
                country,
                isUsed);
        List<TransactionableExampleEntity> transactionableExampleEntitySet =
                requestService.getPrototypesByParams(transactionableType, basketType, country, isUsed);
        log.info("Found {} examples", transactionableExampleEntitySet.size());
        TransactionableExamples response = managementService.createTransactionableResponse(transactionableExampleEntitySet);
        for(TransactionableExample tr : response.getTransactionableExamples()) {
            if(!Strings.isNullOrEmpty(tr.getPrototypeKey())) {
                tr.setProviderCodes(providersMetaService.getProviders(tr.getPrototypeKey()));
            }
            tr.setLocalizedName(cache.get(tr.getLocalizedKey(), lang));
            tr.setLocalizedDescription(cache.get(tr.getDescriptionKey(), lang));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
