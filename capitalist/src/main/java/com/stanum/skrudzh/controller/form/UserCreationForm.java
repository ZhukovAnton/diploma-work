package com.stanum.skrudzh.controller.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Email;


/**
 * UserCreationFormUser
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class UserCreationForm {

    private UserCF user;

    public UserCreationForm firstname(String firstname) {
        this.user.firstname = firstname;
        return this;
    }

    public UserCreationForm lastname(String lastname) {
        this.user.lastname = lastname;
        return this;
    }

    public UserCreationForm email(String email) {
        this.user.email = email;
        return this;
    }

    public UserCreationForm password(String password) {
        this.user.password = password;
        return this;
    }

    public UserCreationForm passwordConfirmation(String passwordConfirmation) {
        this.user.passwordConfirmation = passwordConfirmation;
        return this;
    }

    @Data
    public class UserCF {
        @ApiModelProperty(value = "")
        private String firstname = null;

        @ApiModelProperty(value = "")
        private String lastname = null;

        @ApiModelProperty(value = "")
        @Email
        private String email = null;

        @ApiModelProperty(value = "")
        private String password = null;

        @ApiModelProperty(value = "")
        private String passwordConfirmation = null;
    }
}

