package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.CreditCreationForm;
import com.stanum.skrudzh.controller.form.CreditUpdatingForm;
import com.stanum.skrudzh.controller.response.CreditResponse;
import com.stanum.skrudzh.jpa.model.CreditEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.Credit;
import com.stanum.skrudzh.model.dto.Credits;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.credit.CreditDtoService;
import com.stanum.skrudzh.service.credit.CreditRequestService;
import com.stanum.skrudzh.service.order.OrderService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.constant.Constants;
import com.stanum.skrudzh.model.enums.OrderType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequiredArgsConstructor
@Slf4j
public class CreditsApiController {

    private final CreditRequestService creditRequestService;

    private final CreditDtoService dtoService;

    private final OrderService orderService;

    @ApiOperation(value = "Creates credit for user", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PostMapping(path = "/users/{userId}/credits")
    public ResponseEntity<CreditResponse> createCreditByUser(
            @ApiParam(value = "", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody CreditCreationForm payload) {
        log.info("Create credit for userId = {}, payload = {}", userId, payload);
        CreditEntity creditEntity = creditRequestService.createCreditWithCreationForm(userId, payload.getCredit());
        Credit credit = dtoService.createCreditResponse(creditEntity);
        return new ResponseEntity<>(new CreditResponse(credit), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Retrieves users credits", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/users/{userId}/credits")
    public ResponseEntity<Credits> indexUserCredits(
            @ApiParam(value = "", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Set<CreditEntity> creditEntities = creditRequestService.indexCreditsByUserId(userId);
        Credits creditsResponse = dtoService.createCreditsResponse(creditEntities);
        fillCreditsOrder(creditsResponse.getCredits());
        return new ResponseEntity<>(creditsResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieves credit by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/credits/{id}")
    public ResponseEntity<CreditResponse> creditsIdGet(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        CreditEntity creditEntity = creditRequestService.getCreditById(id);
        Credit credit = dtoService.createCreditResponse(creditEntity);
        return new ResponseEntity<CreditResponse>(new CreditResponse(credit), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieves credit by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @DeleteMapping(path = "/credits/{id}")
    public ResponseEntity<Void> destroyCreditById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestParam(value = "delete_transactions", required = false) boolean deleteTransactions) {
        creditRequestService.destroyCredit(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Updates credit by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @RequestMapping(path = "/credits/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<Void> updateCreditById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody CreditUpdatingForm payload) {
        log.info("Update credit by id = {}, payload = {}", id, payload);
        creditRequestService.updateCredit(id, payload.getCredit());
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    private void fillCreditsOrder(List<Credit> credits) {
        if(RequestUtil.hasGlobalSorting()) {
            UserEntity user = RequestUtil.getUser();
            orderService.fillOrder(user,
                    OrderType.CREDIT_BORROW,
                    EntityTypeEnum.Credit,
                    credits);
        }
    }

}
