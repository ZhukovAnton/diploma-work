package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.jpa.model.ActiveTypeEntity;
import com.stanum.skrudzh.model.dto.ActiveTypes;
import com.stanum.skrudzh.service.active.ActiveTypesService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequiredArgsConstructor
public class ActiveTypesApiController {

    private final ActiveTypesService activeTypesService;

    @ApiOperation(value = "Load active types", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/active_types")
    public ResponseEntity<ActiveTypes> activeTypesGet(@ApiParam(value = "token", required = true)
                                                      @RequestHeader(value = "Authorization", required = true) String authorization) {
        List<ActiveTypeEntity> activeTypeEntities = activeTypesService.getAllActiveTypes();
        ActiveTypes activeTypesResponse = activeTypesService.createActiveTypesResponse(activeTypeEntities);
        return new ResponseEntity<ActiveTypes>(activeTypesResponse, HttpStatus.OK);
    }

}
