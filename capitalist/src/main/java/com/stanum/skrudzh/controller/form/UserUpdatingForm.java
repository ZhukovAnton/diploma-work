package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.controller.form.attributes.PlannedIncomeSavingAttributes;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * UserUpdatingFormUser
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class UserUpdatingForm {

    private UserUF user;

    public UserUpdatingForm firstname(String firstname) {
        this.user.firstname = firstname;
        return this;
    }

    public UserUpdatingForm saltEdgeCustomerSecret(String saltEdgeCustomerSecret) {
        this.user.saltEdgeCustomerSecret = saltEdgeCustomerSecret;
        return this;
    }

    public UserUpdatingForm defaultCurrency(String defaultCurrency) {
        this.user.defaultCurrency = defaultCurrency;
        return this;
    }

    public UserUpdatingForm defaultPeriod(String defaultPeriod) {
        this.user.defaultPeriod = defaultPeriod;
        return this;
    }

    public UserUpdatingForm deviceToken(String deviceToken) {
        this.user.deviceToken = deviceToken;
        return this;
    }

    @Data
    public class UserUF {
        @ApiModelProperty(value = "")
        private String firstname = null;

        @ApiModelProperty(value = "")
        private String saltEdgeCustomerSecret = null;

        @ApiModelProperty(value = "")
        private String defaultCurrency = null;

        @ApiModelProperty(value = "")
        private String defaultPeriod = null;

        @ApiModelProperty(value = "")
        private String deviceToken = null;

        @ApiModelProperty(value = "")
        private Boolean hasActiveSubscription = null;

        @ApiModelProperty(value = "")
        private PlannedIncomeSavingAttributes plannedIncomeSavingAttributes = null;

    }
}

