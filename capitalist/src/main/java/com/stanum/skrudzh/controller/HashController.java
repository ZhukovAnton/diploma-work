package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.service.hash.HashRequestService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HashController {
    private final HashRequestService hashRequestService;

    @ApiOperation(value = "create hashes for existing users", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PutMapping(path = "/users/create_hashes")
    public ResponseEntity<Void> createHashForExistingUsers(@ApiParam(value = "authorization", required = true)
                                                           @RequestHeader(value = "Authorization", required = true) String authorization) {
        hashRequestService.createHashForUsers();
        return ResponseEntity.noContent().build();
    }

}
