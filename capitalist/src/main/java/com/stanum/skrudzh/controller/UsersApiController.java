package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.controller.form.UserCreationForm;
import com.stanum.skrudzh.controller.form.UserPasswordResetForm;
import com.stanum.skrudzh.controller.form.UserPasswordUpdatingForm;
import com.stanum.skrudzh.controller.form.UserUpdatingForm;
import com.stanum.skrudzh.controller.response.CreditResponse;
import com.stanum.skrudzh.controller.response.CustomerResponse;
import com.stanum.skrudzh.controller.response.UserResponse;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.Customer;
import com.stanum.skrudzh.model.dto.User;
import com.stanum.skrudzh.service.user.UserDtoService;
import com.stanum.skrudzh.service.user.UserFinder;
import com.stanum.skrudzh.service.user.UserManagementService;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
@RequiredArgsConstructor
public class UsersApiController {

    private final UserRequestService userRequestService;

    private final UserManagementService userManagementService;

    private final UserDtoService userDtoService;

    private final UserFinder userFinder;

    @GetMapping(path = "/users/{id}")
    @ApiOperation(value = "Retrieves a user by id", authorizations = {@Authorization(Constants.JWT_AUTH)})
    public ResponseEntity<UserResponse> getUserById(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestHeader(value = "HTTP_ACCEPT_LANGUAGE", required = false) String HTTP_ACCEPT_LANGUAGE) {
        UserEntity userEntity = userRequestService.getUserById(id);
        if (HTTP_ACCEPT_LANGUAGE != null) {
            userManagementService.updateLocale(userEntity, HTTP_ACCEPT_LANGUAGE);
        }
        User user = userDtoService.createUserDto(userEntity);
        return ResponseEntity.ok(new UserResponse(user));
    }

    @ApiOperation(value = "Updates user's password", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PutMapping(path = "/users/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "") @RequestBody UserPasswordUpdatingForm payload) {
        userRequestService.updatePassword(payload.getUser(), id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Retrieves or creates SaltEdge customer", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PostMapping(path = "/users/{id}/salt_edge_customers")
    public ResponseEntity<CustomerResponse> getSaltEdgeCustomer(
            @ApiParam(value = "", required = true) @PathVariable("id") Long userId,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        Customer customer = userRequestService.getOrCreateSaltEdgeCustomer(userId);
        return new ResponseEntity<>(new CustomerResponse(customer), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Updates user's password", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PutMapping(path = "/users/{id}/confirm")
    public ResponseEntity<Void> confirmUser(
            @ApiParam(value = "", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            HttpServletRequest request) {
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        userRequestService.confirmUser(id, baseUrl);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Updates user", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH}, path = "/users/{id}")
    public ResponseEntity<Void> updateUser(
            @ApiParam(value = "id", required = true) @PathVariable("id") Long id,
            @ApiParam(value = "authorization", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "form") @RequestBody UserUpdatingForm payload) {
        log.info("Update user, payload={}", payload);
        userRequestService.updateUser(id, payload.getUser());
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Reset new password")
    @PutMapping(path = "/users/new_password")
    public ResponseEntity<Void> resetPassword(
            @ApiParam(value = "form") @RequestBody UserPasswordResetForm payload) {
        userRequestService.resetPassword(payload.getUser());
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Creates a user", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PostMapping(path = "/users")
    public ResponseEntity<UserResponse> usersPost(
            @ApiParam(value = "authorization", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
            @ApiParam(value = "form") @RequestBody UserCreationForm payload,
            @ApiParam(value = "accept_lang") @RequestHeader(value = "HTTP_ACCEPT_LANGUAGE", required = false) String HTTP_ACCEPT_LANGUAGE,
            HttpServletRequest request) throws URISyntaxException {
        log.info("Create user, email = {}", payload.getUser().getEmail());
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        UserEntity newUser = userRequestService.register(payload.getUser(), baseUrl);
        User user = userDtoService.createUserDto(newUser);
        return new ResponseEntity(new UserResponse(user), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Confirm user email")
    @GetMapping(path = "/{code}/confirm_email")
    public ModelAndView confirmEmail(
            @ApiParam(value = "confirm_code", required = true) @PathVariable("code") String code) {
        log.info("Confirm user by code={}", code);
        return userRequestService.confirmEmailByCode(code);
    }

    @ApiOperation(value = "onboard users data", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @PutMapping(path = "/users/{id}/onboard")
    public ResponseEntity<Void> onboard(@ApiParam(value = "id", required = true) @PathVariable("id") Long id,
                                        @ApiParam(value = "authorization", required = true) @RequestHeader(value = "Authorization", required = true) String authorization) {
        userRequestService.onboarding(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Destroy users data", authorizations = {@Authorization(Constants.JWT_AUTH)})
    @DeleteMapping(path = "/users/{id}/destroy_data")
    public ResponseEntity<Void> destroy(@ApiParam(value = "id", required = true)
                                        @PathVariable("id") Long id,
                                        @ApiParam(value = "authorization", required = true)
                                        @RequestHeader(value = "Authorization", required = true) String authorization) {
        log.info("Destroy user data, id={}", id);
        userRequestService.destroyUsersData(id);
        return ResponseEntity.noContent().build();
    }

}
