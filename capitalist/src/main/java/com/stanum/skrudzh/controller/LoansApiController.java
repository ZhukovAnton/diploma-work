package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.LoanCreationForm;
import com.stanum.skrudzh.controller.form.LoanUpdatingForm;
import com.stanum.skrudzh.controller.response.LoanResponse;
import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.model.dto.Borrow;
import com.stanum.skrudzh.model.dto.Loans;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import com.stanum.skrudzh.service.borrow.BorrowDtoService;
import com.stanum.skrudzh.service.borrow.BorrowRequestService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoansApiController {

    private final BorrowRequestService borrowRequestService;

    private final BorrowDtoService dtoService;

    @ApiOperation(value = "Index loans", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/users/{userId}/loans")
    public ResponseEntity<Loans> indexLoansByUserId(
            @ApiParam(value = "", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Set<BorrowEntity> borrowEntities = borrowRequestService.indexBorrowsByUserId(userId, BorrowTypeEnum.Loan);
        Loans loans = dtoService.createLoansResponse(borrowEntities);
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @ApiOperation(value = "Creates a loan", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PostMapping(path = "/users/{userId}/loans")
    public ResponseEntity<LoanResponse> createLoan(
            @ApiParam(value = "", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody LoanCreationForm payload) {
        log.info("Create loan: userId = {}, payload = {}", userId, payload);
        BorrowEntity borrowEntity = borrowRequestService.createBorrowWithCreationForm(userId, payload.getLoan(), BorrowTypeEnum.Loan);
        Borrow borrow = dtoService.createBorrowResponse(borrowEntity);
        return new ResponseEntity<>(new LoanResponse(borrow), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieves a loan", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/loans/{id}")
    public ResponseEntity<LoanResponse> getLoanById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        BorrowEntity borrowEntity = borrowRequestService.getBorrowById(id);
        Borrow borrow = dtoService.createBorrowResponse(borrowEntity);
        return new ResponseEntity<>(new LoanResponse(borrow), HttpStatus.OK);
    }

    @ApiOperation(value = "Destroing a loan", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @DeleteMapping(path = "/loans/{id}")
    public ResponseEntity<Void> destroyLoan(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestParam(value = "delete_transactions", required = false) boolean deleteTransactions) {
        borrowRequestService.destroyBorrow(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Updates a loan", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @RequestMapping(path = "/loans/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Void> updateLoanById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody LoanUpdatingForm payload) {
        log.info("Create loan by id = {}, payload = {}", id, payload);
        borrowRequestService.updateBorrow(id, payload.getLoan());
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

}
