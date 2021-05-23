package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.jpa.model.IconEntity;
import com.stanum.skrudzh.model.dto.Icons;
import com.stanum.skrudzh.service.icon.IconService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequiredArgsConstructor
public class IconsApiController {

    private final IconService iconService;

    @ApiOperation(value = "Index basket actives", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @GetMapping(path = "/icons")
    public ResponseEntity<Icons> getIcons(
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestParam(value = "category", required = false) String category) {
        List<IconEntity> icons = iconService.indexIcons(category);
        Icons iconsResponse = iconService.createIconsResponse(icons);
        return new ResponseEntity<Icons>(iconsResponse, HttpStatus.OK);
    }

}
