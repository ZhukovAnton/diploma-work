package com.stanum.skrudzh.controller.form.attributes;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * BorrowingTransactionNestedAttributes
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class BorrowingTransactionAttributes {

    @ApiModelProperty(value = "")
    private Long id = null;

    @ApiModelProperty(value = "")
    private Long sourceId = null;

    @ApiModelProperty(value = "")
    private String sourceType = null;

    @ApiModelProperty(value = "")
    private Long destinationId = null;

    @ApiModelProperty(value = "")
    private String destinationType = null;

    public BorrowingTransactionAttributes id(Long id) {
        this.id = id;
        return this;
    }

    public BorrowingTransactionAttributes sourceId(Long sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    public BorrowingTransactionAttributes sourceType(String sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public BorrowingTransactionAttributes destinationId(Long destinationId) {
        this.destinationId = destinationId;
        return this;
    }

    public BorrowingTransactionAttributes destinationType(String destinationType) {
        this.destinationType = destinationType;
        return this;
    }

}

