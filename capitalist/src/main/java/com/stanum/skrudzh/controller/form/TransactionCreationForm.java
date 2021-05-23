package com.stanum.skrudzh.controller.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;


/**
 * TransactionCreationFormTransaction
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class TransactionCreationForm {

    private TransactionCF transaction;

    public TransactionCreationForm sourceId(Long sourceId) {
        this.transaction.sourceId = sourceId;
        return this;
    }

    public TransactionCreationForm sourceType(String sourceType) {
        this.transaction.sourceType = sourceType;
        return this;
    }

    public TransactionCreationForm destinationId(Long destinationId) {
        this.transaction.destinationId = destinationId;
        return this;
    }

    public TransactionCreationForm destinationType(String destinationType) {
        this.transaction.destinationType = destinationType;
        return this;
    }

    public TransactionCreationForm amountCents(Long amountCents) {
        this.transaction.amountCents = amountCents;
        return this;
    }

    public TransactionCreationForm amountCurrency(String amountCurrency) {
        this.transaction.amountCurrency = amountCurrency;
        return this;
    }

    public TransactionCreationForm convertedAmountCents(Long convertedAmountCents) {
        this.transaction.convertedAmountCents = convertedAmountCents;
        return this;
    }

    public TransactionCreationForm convertedAmountCurrency(String convertedAmountCurrency) {
        this.transaction.convertedAmountCurrency = convertedAmountCurrency;
        return this;
    }

    public TransactionCreationForm comment(String comment) {
        this.transaction.comment = comment;
        return this;
    }

    public TransactionCreationForm returningBorrowId(Long returningBorrowId) {
        this.transaction.returningBorrowId = returningBorrowId;
        return this;
    }

    public TransactionCreationForm buyingAsset(Boolean buyingAsset) {
        this.transaction.buyingAsset = buyingAsset;
        return this;
    }

    @Data
    public class TransactionCF {
        @ApiModelProperty(value = "")
        private Long sourceId = null;

        @ApiModelProperty(value = "")
        private String sourceType = null;

        @ApiModelProperty(value = "")
        private Long destinationId = null;

        @ApiModelProperty(value = "")
        private String destinationType = null;

        @ApiModelProperty(value = "")
        private Long amountCents = null;

        @ApiModelProperty(value = "")
        private String amountCurrency = null;

        @ApiModelProperty(value = "")
        private Long convertedAmountCents = null;

        @ApiModelProperty(value = "")
        private String convertedAmountCurrency = null;

        @ApiModelProperty(value = "")
        private String comment = null;

        @ApiModelProperty(value = "")
        private LocalDateTime gotAt = null;

        @ApiModelProperty(value = "")
        private Long returningBorrowId = null;

        @ApiModelProperty(value = "")
        private Boolean buyingAsset = null;
    }
}

