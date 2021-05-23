package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.ActiveCreationForm;
import com.stanum.skrudzh.controller.form.ActiveUpdatingForm;
import com.stanum.skrudzh.controller.response.ActiveResponse;
import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.Active;
import com.stanum.skrudzh.model.dto.Actives;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.active.ActiveDtoService;
import com.stanum.skrudzh.service.active.ActiveRequestService;
import com.stanum.skrudzh.service.basket.BasketService;
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
public class ActivesApiController {

    private final BasketService basketService;

    private final ActiveDtoService dtoService;

    private final ActiveRequestService activeRequestService;

    private final OrderService orderService;

    private final OrderMigrator orderMigrator;

    @ApiOperation(value = "Index basket actives", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/baskets/{basketId}/actives")
    public ResponseEntity<Actives> getActivesByBasket(
            @ApiParam(value = "", required = true) @PathVariable("basketId") Long basketId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Set<ActiveEntity> activeEntities = activeRequestService.indexActivesByBasketId(basketId);
        Actives activesResponse = dtoService.createActiveDtoList(activeEntities);
        fillActivesOrder(activesResponse.getActives(), true);
        return new ResponseEntity<Actives>(activesResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Index user actives", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/users/{userId}/actives")
    public ResponseEntity<Actives> getActivesByUser(
            @ApiParam(value = "", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Set<ActiveEntity> activeEntities = activeRequestService.indexActivesByUserId(userId);
        Actives activesResponse = dtoService.createActiveDtoList(activeEntities);
        fillActivesOrder(activesResponse.getActives(), true);
        return new ResponseEntity<Actives>(activesResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Create basket actives", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PostMapping(path = "/baskets/{basketId}/actives")
    public ResponseEntity<ActiveResponse> basketsBasketIdActivesPost(
            @ApiParam(value = "", required = true) @PathVariable("basketId") Long basketId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody ActiveCreationForm payload) {
        log.info("Create active: basketId = {}, payload = {}", basketId, payload);
        ActiveEntity activeEntity = activeRequestService.createActiveEntityWithCreationForm(null, basketId, payload.getActive());
        Active active = dtoService.createActiveDto(activeEntity);
        return new ResponseEntity<ActiveResponse>(new ActiveResponse(active), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Create active for user", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PostMapping(path = "/users/{userId}/actives")
    public ResponseEntity<ActiveResponse> createActiveForUser(
            @ApiParam(value = "", required = true) @PathVariable("basketId") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody ActiveCreationForm payload) {
        log.info("Create active: userId = {}, payload = {}", userId, payload);
        ActiveEntity activeEntity = activeRequestService.createActiveEntityWithCreationForm(userId, null, payload.getActive());
        Active active = dtoService.createActiveDto(activeEntity);
        return new ResponseEntity<ActiveResponse>(new ActiveResponse(active), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete active by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @DeleteMapping(path = "/actives/{id}")
    public ResponseEntity<Void> deleteActiveById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestParam(value = "delete_transactions", required = false) boolean deleteTransactions) {
        activeRequestService.destroyActive(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Get active by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/actives/{id}")
    public ResponseEntity<ActiveResponse> getActiveById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        ActiveEntity activeEntity = activeRequestService.getActiveById(id);
        Active active = dtoService.createActiveDto(activeEntity);
        return new ResponseEntity<>(new ActiveResponse(active), HttpStatus.OK);
    }

    @ApiOperation(value = "Updates basket actives",
            authorizations = {@Authorization(Constants.JWT_AUTH)},
            notes = "For attaching account to active need to pass accountConnectionAttributes in the request. \n" +
                    "From account will be created accountConnection entity. \n" +
                    "For detaching need to pass _destroy=true.")
    @RequestMapping(path = "/actives/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<Void> activesIdPatch(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody ActiveUpdatingForm payload) {
        log.info("Update active id={}, payload={}", id, payload);
        activeRequestService.updateActiveWithUpdatingForm(id, payload.getActive());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void fillActivesOrder(List<Active> actives, boolean withMigration) {
        if(RequestUtil.hasGlobalSorting()) {
            UserEntity user = RequestUtil.getUser();
            boolean isNeedMigration = orderService.fillOrder(user,
                    OrderType.ACTIVE_BORROW,
                    EntityTypeEnum.Active,
                    actives);
            if (isNeedMigration && withMigration) {
                orderMigrator.migrateOrders(user);
                fillActivesOrder(actives, false);
            }
        }
    }
}
