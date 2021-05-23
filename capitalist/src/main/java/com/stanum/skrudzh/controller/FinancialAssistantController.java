package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.response.FreeMoneyResponse;
import com.stanum.skrudzh.controller.response.PlannedIncomeSavingResponse;
import com.stanum.skrudzh.model.dto.FreeMoney;
import com.stanum.skrudzh.model.dto.PlannedIncomeSaving;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.service.financial_assistent.FinancialAssistantDtoService;
import com.stanum.skrudzh.service.financial_assistent.FinancialAssistantRequestService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FinancialAssistantController {

    private final FinancialAssistantRequestService requestService;

    private final FinancialAssistantDtoService dtoService;

    @ApiOperation(value = "Retrieves users free money per period", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/users/{id}/free_money")
    public ResponseEntity<FreeMoneyResponse> getFreeMoney(
            @ApiParam(value = "token", required = true)
            @RequestHeader(value = "Authorization") String authorization,
            @ApiParam(value = "users id", required = true)
            @PathVariable(name = "id") Long usersId,
            @ApiParam(value = "period", required = true)
            @RequestParam(name = "per") FreeMoneyPeriodEnum freeMoneyPeriod) {
        PeriodEnum periodEnum = convertFreeMoneyPeriodIntoPeriod(freeMoneyPeriod);
        long freeAmount = requestService.getFreeMoney(usersId, periodEnum);
        FreeMoney freeMoney = dtoService.createFreeMoneyDto(RequestUtil.getUser(), freeAmount);
        return new ResponseEntity<>(new FreeMoneyResponse(freeMoney), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieves users planned income saving", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/users/{id}/planned_income_saving")
    public ResponseEntity<PlannedIncomeSavingResponse> getPlannedIncomeSaving(
            @ApiParam(value = "token", required = true)
            @RequestHeader(value = "Authorization") String authorization,
            @ApiParam(value = "users id", required = true)
            @PathVariable(name = "id") Long usersId) {
        requestService.plannedIncomeSaving(usersId);
        PlannedIncomeSaving plannedIncomeSaving = dtoService.createPlannedIncomeSavingDto(RequestUtil.getUser());
        return new ResponseEntity<>(new PlannedIncomeSavingResponse(plannedIncomeSaving), HttpStatus.OK);
    }

    public enum FreeMoneyPeriodEnum {
        day,

        month
    }

    private PeriodEnum convertFreeMoneyPeriodIntoPeriod(FreeMoneyPeriodEnum freeMoneyPeriod) {
        switch (freeMoneyPeriod) {
            case day: return PeriodEnum.day;
            case month:
            default:
                return PeriodEnum.month;
        }
    }
}
