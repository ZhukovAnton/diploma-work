package com.stanum.skrudzh.controller.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * SessionCredentialsFormSession
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class SessionCredentialsForm {

    private SessionForm session;

    public SessionCredentialsForm email(String email) {
        this.session.email = email;
        return this;
    }

    public SessionCredentialsForm password(String password) {
        this.session.password = password;
        return this;
    }

    @Data
    public class SessionForm {
        @ApiModelProperty(value = "")
        private String email = null;

        @ApiModelProperty(value = "")
        private String password = null;
    }
}

