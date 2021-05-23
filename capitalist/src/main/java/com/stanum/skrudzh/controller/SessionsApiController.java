package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.SessionCredentialsForm;
import com.stanum.skrudzh.controller.response.SessionResponse;
import com.stanum.skrudzh.jpa.model.SessionEntity;
import com.stanum.skrudzh.model.dto.Session;
import com.stanum.skrudzh.service.user.SessionManagementService;
import com.stanum.skrudzh.service.user.UserRequestService;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
@RequiredArgsConstructor
public class SessionsApiController {

    private final UserRequestService userRequestService;

    private final SessionManagementService sessionManagementService;

    @PostMapping("/sessions")
    public ResponseEntity<SessionResponse> create(
            @ApiParam(value = "") @RequestBody SessionCredentialsForm payload, HttpServletResponse response) {
        log.info("Create session payload={}", payload);
        long start = System.currentTimeMillis();
        Session session = userRequestService.
                login(payload.getSession());
        return new ResponseEntity(new SessionResponse(session), HttpStatus.CREATED);
    }

    @DeleteMapping("/sessions/{token}")
    @ApiOperation(value = "delete session", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<Void> destroy(
            @ApiParam(value = "", required = true) @PathVariable("token") String token,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        log.info("Destroy session for token={}", token);
        long start = System.currentTimeMillis();
        SessionEntity sessionEntity = sessionManagementService.getByToken(token);
        sessionManagementService.deleteSession(sessionEntity);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

}
