package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.TransactionCreationForm;
import com.stanum.skrudzh.controller.form.TransactionUpdatingForm;
import com.stanum.skrudzh.controller.response.TransactionResponse;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.model.dto.Transaction;
import com.stanum.skrudzh.model.dto.Transactions;
import com.stanum.skrudzh.service.transaction.TransactionDtoService;
import com.stanum.skrudzh.service.transaction.TransactionRequestService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionsApiController {

    private final TransactionRequestService transactionRequestService;

    private final TransactionDtoService dtoService;

    @ApiOperation(value = "Retrieves users transactions", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/users/{id}/transactions")
    public ResponseEntity<Transactions> getTransactions(
            @ApiParam(value = "", required = true) @PathVariable("id") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestParam(value = "transaction_type", required = false) String transactionType,
            @ApiParam(value = "") @RequestParam(value = "transactionable_id", required = false) Long transactionableId,
            @ApiParam(value = "") @RequestParam(value = "transactionable_type", required = false) String transactionableType,
            @ApiParam(value = "") @RequestParam(value = "credit_id", required = false) Long creditId,
            @ApiParam(value = "") @RequestParam(value = "borrow_id", required = false) Long borrowId,
            @ApiParam(value = "") @RequestParam(value = "borrow_type", required = false) String borrowType,
            @ApiParam(value = "") @RequestParam(value = "last_got_at", required = false) String lastGotAt,
            @ApiParam(value = "") @RequestParam(value = "from_got_at", required = false) String fromGotAt,
            @ApiParam(value = "") @RequestParam(value = "to_got_at", required = false) String toGotAt,
            @ApiParam(value = "") @RequestParam(value = "count", required = false) Integer count) {
        log.info("Get user transactions: userId={}," +
                "transactionType={}" +
                "transactionableId={}," +
                "transactionableType={}," +
                "creditId={}," +
                "borrowId={}," +
                "borrowType={}," +
                "lastGotAt={}," +
                "fromGotAt={}," +
                "toGotAt={}," +
                "count={}", userId, transactionType, transactionableId, transactionableType, creditId, borrowId, borrowType, lastGotAt,
                fromGotAt, toGotAt, count);
        List<TransactionEntity> transactionEntities = transactionRequestService.getTransactionsByUserAndParams(
                userId,
                transactionType,
                transactionableId,
                transactionableType,
                creditId,
                borrowId,
                borrowType,
                lastGotAt,
                fromGotAt,
                toGotAt,
                count);
        Transactions transactionsResponse = dtoService.createTransactionsResponse(transactionEntities);
        return new ResponseEntity<Transactions>(transactionsResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Create transaction", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PostMapping(path = "/users/{user_id}/transactions")
    public ResponseEntity<TransactionResponse> createTransaction(
            @ApiParam(value = "", required = true) @PathVariable("user_id") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody TransactionCreationForm payload) {
        log.info("Create transaction for userId = {}, payload = {}", userId, payload);
        TransactionEntity transactionEntity = transactionRequestService.createTransactionWithForm(userId, payload.getTransaction());
        Transaction transaction = dtoService.createTransactionResponse(transactionEntity);
        return new ResponseEntity<>(new TransactionResponse(transaction), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieves transaction by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/transactions/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        log.info("Get transaction by id={}", id);
        TransactionEntity transactionEntity = transactionRequestService.getTransactionById(id);
        Transaction transaction = dtoService.createTransactionResponse(transactionEntity);
        return new ResponseEntity<TransactionResponse>(new TransactionResponse(transaction), HttpStatus.OK);
    }

    @ApiOperation(value = "Update transaction by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @RequestMapping(path = "/transactions/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<Void> updateTransactionById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody TransactionUpdatingForm payload) {
        log.info("Update transaction by id = {}, payload = {}", id, payload);
        transactionRequestService.updateTransactionById(id, payload.getTransaction());
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Destroing an transaction by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @DeleteMapping(path = "/transactions/{id}")
    public ResponseEntity<Void> deleteTransactionById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        log.info("Destroy transaction by id={}", id);
        transactionRequestService.destroyTransactionById(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Mark transaction as duplicated", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PatchMapping(path = "/transactions/{id}/duplicate")
    public ResponseEntity<Void> markTransactionAsDuplicated(@ApiParam(value = "", required = true) @PathVariable("id") Long id,
                                                            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        log.info("Mark trasnsaction as duplicated, id={}", id);
        transactionRequestService.duplicateTransactionById(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

}
