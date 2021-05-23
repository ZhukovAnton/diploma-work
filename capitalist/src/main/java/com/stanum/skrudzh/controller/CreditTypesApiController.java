package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.jpa.model.CreditTypeEntity;
import com.stanum.skrudzh.model.dto.CreditTypes;
import com.stanum.skrudzh.service.credit.CreditTypesService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequiredArgsConstructor
public class CreditTypesApiController {

    private final CreditTypesService creditTypesService;

    @ApiOperation(value = "Retrieves credit types", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/credit_types")
    public ResponseEntity<CreditTypes> getCreditTypes() {
        Set<CreditTypeEntity> creditTypeEntities = creditTypesService.getAllCreditTypes();
        CreditTypes creditsResponse = creditTypesService.createCreditTypeResponse(creditTypeEntities);
        return new ResponseEntity<CreditTypes>(creditsResponse, HttpStatus.OK);
    }

}
