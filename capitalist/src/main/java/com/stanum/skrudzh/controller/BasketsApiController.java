package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.response.BasketResponse;
import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.model.dto.Basket;
import com.stanum.skrudzh.model.dto.Baskets;
import com.stanum.skrudzh.service.basket.BasketDtoService;
import com.stanum.skrudzh.service.basket.BasketService;
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

import java.util.Set;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequiredArgsConstructor
public class BasketsApiController {

    private final BasketService basketService;

    private final BasketDtoService dtoService;

    @GetMapping(path = "/users/{userId}/baskets")
    @ApiOperation(value = "Index baskets", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<Baskets> getBasketsForUser(
            @ApiParam(value = "", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Set<BasketEntity> basketEntities = basketService.indexBasketsByUserId(userId);
        Baskets response = dtoService.createBasketsResponse(basketEntities);
        return new ResponseEntity<Baskets>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/baskets/{id}")
    @ApiOperation(value = "Get basket by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<BasketResponse> basketsIdGet(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        BasketEntity basketEntity = basketService.getBasketById(id);
        Basket basket = dtoService.createBasketResponse(basketEntity);
        return new ResponseEntity<BasketResponse>(new BasketResponse(basket), HttpStatus.OK);
    }

}
