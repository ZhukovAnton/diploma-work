package com.stanum.skrudzh.controller.form.attributes;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * CreditingTransactionNestedAttributes
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class CreditingTransactionAttributes {

    @ApiModelProperty(value = "")
    private Long id = null;

    @ApiModelProperty(value = "")
    private Long destinationId = null;

    @ApiModelProperty(value = "")
    private String destinationType = null;

    public CreditingTransactionAttributes id(Long id) {
        this.id = id;
        return this;
    }

    public CreditingTransactionAttributes destinationId(Long destinationId) {
        this.destinationId = destinationId;
        return this;
    }

    public CreditingTransactionAttributes destinationType(String destinationType) {
        this.destinationType = destinationType;
        return this;
    }

}

