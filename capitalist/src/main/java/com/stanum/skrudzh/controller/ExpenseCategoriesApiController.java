package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.ExpenseCategoryCreationForm;
import com.stanum.skrudzh.controller.form.ExpenseCategoryUpdatingForm;
import com.stanum.skrudzh.controller.response.ExpenseCategoryResponse;
import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.model.dto.ExpenseCategories;
import com.stanum.skrudzh.model.dto.ExpenseCategory;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryDtoService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryRequestService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequiredArgsConstructor
public class ExpenseCategoriesApiController {

    private final ExpenseCategoryRequestService expenseCategoryRequestService;

    private final ExpenseCategoryDtoService dtoService;

    @GetMapping(path = "/baskets/{basketId}/expense_categories")
    @ApiOperation(value = "Index basket expense categories", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<ExpenseCategories> getExpenseCategoriesByBasket(
            @ApiParam(value = "", required = true) @PathVariable("basketId") Long basketId,
            @ApiParam(value = "", required = false) @RequestParam(value = "noBorrows", required = false) boolean noBorrows,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Set<ExpenseCategoryEntity> expenseCategoryEntities = expenseCategoryRequestService
                .indexExpenseCategoriesByBasket(basketId, noBorrows);
        ExpenseCategories response = dtoService.createExpenseCategoriesResponse(expenseCategoryEntities);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/users/{userId}/expense_categories")
    @ApiOperation(value = "Index user expense categories", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<ExpenseCategories> getExpenseCategoriesByUser(
            @ApiParam(value = "", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "", required = false) @RequestParam(value = "noBorrows", required = false) boolean noBorrows,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Set<ExpenseCategoryEntity> expenseCategoryEntities = expenseCategoryRequestService
                .indexExpenseCategoriesByUser(userId, noBorrows);
        ExpenseCategories response = dtoService.createExpenseCategoriesResponse(expenseCategoryEntities);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/baskets/{basketId}/expense_categories/first_borrow")
    @ApiOperation(value = "Index basket expense categories", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<ExpenseCategoryResponse> getFirstBorrowExpenseCategory(
            @ApiParam(value = "", required = true) @PathVariable("basketId") Long basketId,
            @ApiParam(value = "", required = true) @RequestParam(value = "currency", required = true) String currency,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        ExpenseCategoryEntity expenseCategoryEntity = expenseCategoryRequestService.getFirstBorrowExpenseCategory(basketId, currency);
        ExpenseCategory response = dtoService.createExpenseCategoryResponse(expenseCategoryEntity);
        return new ResponseEntity<>(new ExpenseCategoryResponse(response), HttpStatus.OK);
    }

    @PostMapping(path = "/baskets/{basketId}/expense_categories")
    @ApiOperation(value = "Creates basket expense categorie", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @Deprecated(since = "1.0.2")
    public ResponseEntity<ExpenseCategoryResponse> createExpenseCategory(
            @ApiParam(value = "", required = true) @PathVariable("basketId") Long basketId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody ExpenseCategoryCreationForm payload) {
        ExpenseCategoryEntity expenseCategoryEntity = expenseCategoryRequestService.createExpenseCategoryWithForm(null, basketId, payload.getExpenseCategory());
        ExpenseCategory expenseCategory = dtoService.createExpenseCategoryResponse(expenseCategoryEntity);
        return new ResponseEntity<>(new ExpenseCategoryResponse(expenseCategory), HttpStatus.OK);
    }

    @PostMapping(path = "/users/{userId}/expense_categories")
    @ApiOperation(value = "Creates expense categorie", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<ExpenseCategoryResponse> createExpenseCategoryByUser(
            @ApiParam(value = "", required = true) @PathVariable("basketId") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody ExpenseCategoryCreationForm payload) {
        ExpenseCategoryEntity expenseCategoryEntity = expenseCategoryRequestService.createExpenseCategoryWithForm(userId, null, payload.getExpenseCategory());
        ExpenseCategory expenseCategory = dtoService.createExpenseCategoryResponse(expenseCategoryEntity);
        return new ResponseEntity<>(new ExpenseCategoryResponse(expenseCategory), HttpStatus.OK);
    }

    @DeleteMapping(path = "/expense_categories/{id}")
    @ApiOperation(value = "Deletes an expense category", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<Void> destroyExpenseCategoryById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestParam(value = "delete_transactions", required = false) boolean deleteTransactions) {
        expenseCategoryRequestService.destroyExpenseCategory(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/expense_categories/{id}")
    @ApiOperation(value = "Retrieves expense category", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<ExpenseCategoryResponse> getExpenseCategoryById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        ExpenseCategoryEntity expenseCategoryEntity = expenseCategoryRequestService.getExpenseCategoryById(id);
        ExpenseCategory expenseCategory = dtoService.createExpenseCategoryResponse(expenseCategoryEntity);
        return new ResponseEntity<>(new ExpenseCategoryResponse(expenseCategory), HttpStatus.OK);
    }

    @RequestMapping(path = "/expense_categories/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    @ApiOperation(value = "Updates an expense category", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<Void> updateExpenseCategory(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody ExpenseCategoryUpdatingForm payload,
            HttpServletRequest httpRequest) {
        boolean patch = RequestMethod.PATCH.name().equals(httpRequest.getMethod());
        expenseCategoryRequestService.updateExpenseCategoryWithForm(id, payload.getExpenseCategory(), patch);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

}
