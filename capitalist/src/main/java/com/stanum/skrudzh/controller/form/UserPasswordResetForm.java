package com.stanum.skrudzh.controller.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Email;


/**
 * UserPasswordResetFormUser
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class UserPasswordResetForm {

    private PasswordResetForm user;

    public UserPasswordResetForm email(String email) {
        this.user.email = email;
        return this;
    }

    public UserPasswordResetForm passwordResetCode(String passwordResetCode) {
        this.user.passwordResetCode = passwordResetCode;
        return this;
    }

    public UserPasswordResetForm password(String password) {
        this.user.password = password;
        return this;
    }

    public UserPasswordResetForm passwordConfirmation(String passwordConfirmation) {
        this.user.passwordConfirmation = passwordConfirmation;
        return this;
    }

    @Data
    public class PasswordResetForm {

        @ApiModelProperty(value = "")
        @Email
        private String email = null;

        @ApiModelProperty(value = "")
        private String passwordResetCode = null;

        @ApiModelProperty(value = "")
        private String password = null;

        @ApiModelProperty(value = "")
        private String passwordConfirmation = null;

    }

}

