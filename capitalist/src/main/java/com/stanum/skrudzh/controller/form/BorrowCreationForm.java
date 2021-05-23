package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.controller.form.attributes.BorrowingTransactionAttributes;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;


/**
 * BorrowCreationForm
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class BorrowCreationForm {

    @ApiModelProperty(value = "")
    private String name = null;

    @ApiModelProperty(value = "")
    private String iconUrl = null;

    @ApiModelProperty(value = "")
    private String amountCurrency = null;

    @ApiModelProperty(value = "")
    private Long amountCents = null;

    @ApiModelProperty(value = "")
    private String comment = null;

    @ApiModelProperty(value = "")
    private LocalDateTime payday;

    @ApiModelProperty(value = "")
    private LocalDateTime borrowedAt;

    @ApiModelProperty(value = "")
    private Integer rowOrderPosition = null;

    @ApiModelProperty(value = "")
    private BorrowingTransactionAttributes borrowingTransactionAttributes = null;

    public BorrowCreationForm name(String name) {
        this.name = name;
        return this;
    }

    public BorrowCreationForm iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public BorrowCreationForm amountCurrency(String amountCurrency) {
        this.amountCurrency = amountCurrency;
        return this;
    }

    public BorrowCreationForm amountCents(Long amountCents) {
        this.amountCents = amountCents;
        return this;
    }

    public BorrowCreationForm comment(String comment) {
        this.comment = comment;
        return this;
    }

    public BorrowCreationForm borrowingTransactionAttributes(BorrowingTransactionAttributes borrowingTransactionAttributes) {
        this.borrowingTransactionAttributes = borrowingTransactionAttributes;
        return this;
    }

    public BorrowCreationForm rowOrderPosition(Integer rowOrderPosition) {
        this.rowOrderPosition = rowOrderPosition;
        return this;
    }

}

