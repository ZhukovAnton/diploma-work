package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.ExpenseSourceCreationForm;
import com.stanum.skrudzh.controller.form.ExpenseSourceUpdatingForm;
import com.stanum.skrudzh.controller.response.ExpenseSourceResponse;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.model.dto.ExpenseSource;
import com.stanum.skrudzh.model.dto.ExpenseSources;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceDtoService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceRequestService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequiredArgsConstructor
@Slf4j
public class ExpenseSourcesApiController {

    private final ExpenseSourceRequestService expenseSourceRequestService;

    private final ExpenseSourceDtoService expenseSourceDtoService;

    @ApiOperation(value = "Index user expense sources", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/users/{id}/expense_sources")
    public ResponseEntity<ExpenseSources> getExpenseSourcesWithCurrency(
            @ApiParam(value = "user_id", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "currency", required = false) @RequestParam(value = "currency", required = false) String currency,
            @ApiParam(value = "authorization", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Set<ExpenseSourceEntity> realExpenseSourceEntities = expenseSourceRequestService.getExpenseSources(id, false, currency);
        return new ResponseEntity<>(expenseSourceDtoService.createExpenseSourcesResponse(realExpenseSourceEntities),
                HttpStatus.OK);
    }

    @ApiOperation(value = "Creates an expense source", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PostMapping(path = "/users/{id}/expense_sources")
    public ResponseEntity<ExpenseSourceResponse> usersUserIdExpenseSourcesPost(
            @ApiParam(value = "authorization", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "form") @RequestBody ExpenseSourceCreationForm payload,
            @ApiParam(value = "user_id", required = true) @PathVariable("id") Long id) {
        log.info("Create expense source for userId = {}, payload = {}", id, payload);
        ExpenseSourceEntity expenseSourceEntity = expenseSourceRequestService.createExpenseSource(id, payload.getExpenseSource());
        ExpenseSource expenseSource = expenseSourceDtoService.createExpenseSourceResponse(expenseSourceEntity);
        return new ResponseEntity<ExpenseSourceResponse>(new ExpenseSourceResponse(expenseSource), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Destroy an expense source", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @DeleteMapping(path = "/expense_sources/{id}")
    public ResponseEntity<Void> expenseSourcesIdDelete(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestParam(value = "delete_transactions", required = false) boolean deleteTransactions) {
        expenseSourceRequestService.destroyExpenseSource(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Retrieves an expense source by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/expense_sources/{id}")
    public ResponseEntity<ExpenseSourceResponse> getExpenseSourceById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        ExpenseSourceEntity expenseSourceEntity = expenseSourceRequestService.getExpenseSourceById(id);
        ExpenseSource expenseSource = expenseSourceDtoService.createExpenseSourceResponse(expenseSourceEntity);
        return new ResponseEntity<>(new ExpenseSourceResponse(expenseSource), HttpStatus.OK);
    }

    @ApiOperation(value = "Updates an expense source by id.",
            authorizations = {@Authorization(Constants.JWT_AUTH)},
            notes = "For attaching account to expense source need to pass in the request accountConnectionAttributes. \n" +
                    "From account will be created accountConnection entity. \n" +
                    "For detaching need to pass _destroy=true.")
    @RequestMapping(path = "/expense_sources/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<Void> updateExpenseSourceById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody ExpenseSourceUpdatingForm payload,
            HttpServletRequest httpRequest) {
        log.info("Update expense source id = {}, payload = {}", id, payload);
        expenseSourceRequestService.updateExpenseSourceWithForm(id, payload.getExpenseSource(), httpRequest.getMethod());
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "First expense source", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/users/{user_id}/expense_sources/first")
    public ResponseEntity<ExpenseSourceResponse> usersUserIdExpenseSourcesFirstGet(
            @ApiParam(value = "", required = true) @PathVariable("user_id") Long userId,
            @ApiParam(value = "", required = true) @RequestParam(value = "currency", required = true) String currency,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestParam(value = "is_virtual", required = false) boolean isVirtual) {
        ExpenseSourceEntity expenseSourceEntity = expenseSourceRequestService.getFirstExpenseSource(userId, isVirtual, currency);
        ExpenseSource expenseSource = expenseSourceDtoService.createExpenseSourceResponse(expenseSourceEntity);
        return new ResponseEntity<ExpenseSourceResponse>(new ExpenseSourceResponse(expenseSource), HttpStatus.OK);
    }

    @ApiOperation(value = "get amount of wallets with broken balances", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/get_amount_of_broken_expensesSources")
    public ResponseEntity<Long> getAmountOfBrokenExpenseSources(@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        long answer = expenseSourceRequestService.getAmountOfNotActualBalances();
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }


    @ApiOperation(value = "sync balance with history", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PatchMapping(path = "/expense_sources/{id}/sync_balances")
    public ResponseEntity<Void> syncBalances(@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
                                             @PathVariable("id") Long id) {
        expenseSourceRequestService.syncBalances(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/expense_sources/actualise_purposes")
    @ApiOperation(value = "actualise expense source creation transaction", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<Void> actualiseTransactionsPurposes(@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        expenseSourceRequestService.actualiseExpenseSourceCreationPurposes();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
