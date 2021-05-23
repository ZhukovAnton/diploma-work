package com.stanum.skrudzh.controller.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * UserPasswordUpdatingFormUser
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class UserPasswordUpdatingForm {

    private PasswordUpdatingForm user;

    public UserPasswordUpdatingForm oldPassword(String oldPassword) {
        this.user.oldPassword = oldPassword;
        return this;
    }

    public UserPasswordUpdatingForm newPassword(String newPassword) {
        this.user.newPassword = newPassword;
        return this;
    }

    public UserPasswordUpdatingForm newPasswordConfirmation(String newPasswordConfirmation) {
        this.user.newPasswordConfirmation = newPasswordConfirmation;
        return this;
    }

    @Data
    public class PasswordUpdatingForm{
        @ApiModelProperty(value = "")
        private String oldPassword = null;

        @ApiModelProperty(value = "")
        private String newPassword = null;

        @ApiModelProperty(value = "")
        private String newPasswordConfirmation = null;

    }

}

