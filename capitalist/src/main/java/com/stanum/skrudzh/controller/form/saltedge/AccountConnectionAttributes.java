package com.stanum.skrudzh.controller.form.saltedge;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * AccountConnectionNestedAttributes
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class AccountConnectionAttributes {

    @ApiModelProperty(value = "account connection id")
    private Long id = null;

    @ApiModelProperty(value = "account api id")
    private Long accountId = null;

    @ApiModelProperty(value = "connection api id")
    private Long connectionId = null;

    @ApiModelProperty(value = "true - detach account connection from expense source or active. " +
            "null or false - do nothing")
    private Boolean destroy = null;

}

