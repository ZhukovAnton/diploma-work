package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.DebtCreationForm;
import com.stanum.skrudzh.controller.form.DebtUpdatingForm;
import com.stanum.skrudzh.controller.response.DebtResponse;
import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.Borrow;
import com.stanum.skrudzh.model.dto.Debts;
import com.stanum.skrudzh.model.dto.base.Ordered;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.borrow.BorrowDtoService;
import com.stanum.skrudzh.service.borrow.BorrowRequestService;
import com.stanum.skrudzh.service.order.OrderService;
import com.stanum.skrudzh.service.order.migration.OrderMigrator;
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
public class DebtsApiController {

    private final BorrowRequestService borrowsService;

    private final BorrowDtoService dtoService;

    private final OrderService orderService;

    private final OrderMigrator orderMigrator;

    @ApiOperation(value = "Retrieves a debt", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/users/{userId}/debts")
    public ResponseEntity<Debts> indexUsersDebts(
            @ApiParam(value = "", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Set<BorrowEntity> borrowEntities = borrowsService.indexBorrowsByUserId(userId, BorrowTypeEnum.Debt);
        Debts debts = dtoService.createDebtsResponse(borrowEntities);
        fillDebtsOrder(debts.getDebts(), true);
        return new ResponseEntity<>(debts, HttpStatus.OK);
    }

    @ApiOperation(value = "Creates a debt", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PostMapping(path = "/users/{userId}/debts")
    public ResponseEntity<DebtResponse> usersUserIdDebtsPost(
            @ApiParam(value = "", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody DebtCreationForm payload) {
        log.info("Create debt for userId = {}, payload = {}", userId, payload);
        BorrowEntity borrowEntity = borrowsService.createBorrowWithCreationForm(userId, payload.getDebt(), BorrowTypeEnum.Debt);
        Borrow borrow = dtoService.createBorrowResponse(borrowEntity);
        return new ResponseEntity<>(new DebtResponse(borrow), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieves a debt", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/debts/{id}")
    public ResponseEntity<DebtResponse> debtsIdGet(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        BorrowEntity borrowEntity = borrowsService.getBorrowById(id);
        Borrow borrow = dtoService.createBorrowResponse(borrowEntity);
        return new ResponseEntity<>(new DebtResponse(borrow), HttpStatus.OK);
    }

    @ApiOperation(value = "Destroing a debt", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @DeleteMapping(path = "/debts/{id}")
    public ResponseEntity<Void> debtsIdDelete(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestParam(value = "delete_transactions", required = false) boolean deleteTransactions) {
        borrowsService.destroyBorrow(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Updates a debt", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @RequestMapping(path = "/debts/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Void> debtsIdPatch(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody DebtUpdatingForm payload) {
        log.info("Create debt by id = {}, payload = {}", id, payload);
        borrowsService.updateBorrow(id, payload.getDebt());
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    private void fillDebtsOrder(List<? extends Ordered> objects, boolean withMigration) {
        if(RequestUtil.hasGlobalSorting()) {
            UserEntity user = RequestUtil.getUser();
            boolean isNeedMigration = orderService.fillOrder(user,
                    OrderType.ACTIVE_BORROW,
                    EntityTypeEnum.Borrow,
                    objects);
            if (isNeedMigration && withMigration) {
                orderMigrator.migrateOrders(user);
                fillDebtsOrder(objects, false);
            }
        }
    }

}
