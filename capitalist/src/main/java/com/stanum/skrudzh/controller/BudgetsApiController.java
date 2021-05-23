package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.response.BudgetResponse;
import com.stanum.skrudzh.model.dto.Budget;
import com.stanum.skrudzh.service.budget.BudgetCalculationService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BudgetsApiController {

    private final BudgetCalculationService budgetCalculationService;

    @GetMapping(path = "/users/{userId}/budget")
    @ApiOperation(value = "Budget of user", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<BudgetResponse> usersUserIdBudgetGet(
            @ApiParam(value = "userId", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "authorization", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Budget budget = budgetCalculationService.calculateUsersBudget(userId);
        return new ResponseEntity<>(new BudgetResponse(budget), HttpStatus.OK);
    }

}
