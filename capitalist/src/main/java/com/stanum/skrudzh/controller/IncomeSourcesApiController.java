package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.IncomeSourceCreationForm;
import com.stanum.skrudzh.controller.form.IncomeSourceUpdatingForm;
import com.stanum.skrudzh.controller.response.IncomeSourceResponse;
import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.model.dto.IncomeSource;
import com.stanum.skrudzh.model.dto.IncomeSources;
import com.stanum.skrudzh.service.income_source.IncomeSourceDtoService;
import com.stanum.skrudzh.service.income_source.IncomeSourceRequestService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
@RequiredArgsConstructor
public class IncomeSourcesApiController {

    private final IncomeSourceRequestService incomeSourceRequestService;

    private final IncomeSourceDtoService dtoService;

    @GetMapping(path = "/users/{id}/income_sources")
    @ApiOperation(value = "Index user income sources", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<IncomeSources> getIncomeSources(
            @ApiParam(value = "user_id", required = true) @PathVariable("id") Long userId,
            @ApiParam(value = "no_borrows", required = false) @RequestParam(value = "no_borrows", required = false) boolean noBorrows,
            @ApiParam(value = "authorization", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        log.info("Get income source, userId={}, noBorrows={}", userId, noBorrows);
        List<IncomeSourceEntity> incomeSourceEntities = incomeSourceRequestService.indexIncomeSources(userId, noBorrows, true);
        IncomeSources response = dtoService.createIncomeSourcesResponse(incomeSourceEntities);
        return new ResponseEntity<IncomeSources>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/users/{id}/income_sources")
    @ApiOperation(value = "Creates an income source", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<IncomeSourceResponse> createIncomeSource(
            @ApiParam(value = "user_id", required = true) @PathVariable("id") Long userId,
            @ApiParam(value = "form") @RequestBody IncomeSourceCreationForm payload,
            @ApiParam(value = "authorization", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        log.info("Create income source, userId={}, payload={}", userId, payload);
        IncomeSourceEntity incomeSourceEntity = incomeSourceRequestService.createIncomeSourceEntityWithForm(userId, payload.getIncomeSource());
        IncomeSource incomeSource = dtoService.createIncomeSourceResponse(incomeSourceEntity);
        return new ResponseEntity<>(new IncomeSourceResponse(incomeSource), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/income_sources/{income_id}")
    @ApiOperation(value = "Destroy user income sources", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<Void> incomeSourcesIdDelete(
            @ApiParam(value = "", required = true) @PathVariable("income_id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestParam(value = "delete_transactions", required = false) boolean deleteTransactions) {
        log.info("Destroy income source");
        incomeSourceRequestService.destroyIncomeSource(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/income_sources/{income_id}")
    @ApiOperation(value = "Index user income sources", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<IncomeSourceResponse> getIncomeSourcebyId(
            @ApiParam(value = "id", required = true) @PathVariable("income_id") Long id,
            @ApiParam(value = "authorization", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        log.info("Get income source by id = {}", id);
        IncomeSourceEntity incomeSourceEntity = incomeSourceRequestService.getIncomeSourceById(id);
        IncomeSourceResponse response = new IncomeSourceResponse(dtoService.createIncomeSourceResponse(incomeSourceEntity));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/income_sources/{income_id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    @ApiOperation(value = "Updates user income sources", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<Void> updateIncomeSource(
            @ApiParam(value = "id", required = true) @PathVariable("income_id") Long id,
            @ApiParam(value = "authorization", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody IncomeSourceUpdatingForm payload,
            HttpServletRequest httpRequest) {
        log.info("Update income source, id={}, form={}", id, payload);
        boolean patch = RequestMethod.PATCH.name().equals(httpRequest.getMethod());
        incomeSourceRequestService.updateIncomeSourceWithForm(payload.getIncomeSource(), id, patch);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }


    @GetMapping(path = "/users/{userId}/income_sources/first_borrow")
    @ApiOperation(value = "First income source borrow", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<IncomeSourceResponse> usersUserIdIncomeSourcesFirstBorrowGet(
            @ApiParam(value = "", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "", required = true) @RequestParam(value = "currency", required = true) String currency,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        log.info("Get first income source borrow, userId={}, currency={}", userId, currency);
        IncomeSourceEntity incomeSourceEntity = incomeSourceRequestService.getFirstBorrowIncomeSource(userId, currency);
        IncomeSource incomeSource = dtoService.createIncomeSourceResponse(incomeSourceEntity);
        return new ResponseEntity<>(new IncomeSourceResponse(incomeSource), HttpStatus.OK);
    }
}
