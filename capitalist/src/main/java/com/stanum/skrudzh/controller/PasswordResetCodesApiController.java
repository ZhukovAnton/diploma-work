package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.PasswordResetCodeCreationForm;
import com.stanum.skrudzh.controller.response.PasswordResetCodeResponse;
import com.stanum.skrudzh.service.user.UserRequestService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequiredArgsConstructor
@Slf4j
public class PasswordResetCodesApiController {

    private final UserRequestService userRequestService;

    @ApiOperation(value = "Creates password reset code")
    @PostMapping(path = "/password_reset_codes")
    public ResponseEntity<PasswordResetCodeResponse> passwordResetCodesPost(
            @ApiParam(value = "") @RequestBody PasswordResetCodeCreationForm payload) {
        log.info("Password reset, payload={}", payload);
        userRequestService.generateAndSendPassResetCode(payload.getPasswordResetCode());
        return new ResponseEntity<>(new PasswordResetCodeResponse(), HttpStatus.CREATED);
    }

}
