package com.stanum.skrudzh.controller.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * PasswordResetCodeCreationFormPasswordResetCode
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class PasswordResetCodeCreationForm {

    private SendPasswordResetCodeForm passwordResetCode;

    public PasswordResetCodeCreationForm email(String email) {
        this.passwordResetCode.email = email;
        return this;
    }

    @Data
    public class SendPasswordResetCodeForm {
        @ApiModelProperty(value = "")
        private String email = null;
    }
}

