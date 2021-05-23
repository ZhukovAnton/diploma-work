package com.stanum.skrudzh.controller.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;


/**
 * TransactionUpdatingFormTransaction
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class TransactionUpdatingForm {

    private TransactionUF transaction;

    public TransactionUpdatingForm sourceId(Long sourceId) {
        this.transaction.sourceId = sourceId;
        return this;
    }

    public TransactionUpdatingForm sourceType(String sourceType) {
        this.transaction.sourceType = sourceType;
        return this;
    }

    public TransactionUpdatingForm destinationId(Long destinationId) {
        this.transaction.destinationId = destinationId;
        return this;
    }

    public TransactionUpdatingForm destinationType(String destinationType) {
        this.transaction.destinationType = destinationType;
        return this;
    }

    public TransactionUpdatingForm amountCents(Long amountCents) {
        this.transaction.amountCents = amountCents;
        return this;
    }

    public TransactionUpdatingForm amountCurrency(String amountCurrency) {
        this.transaction.amountCurrency = amountCurrency;
        return this;
    }

    public TransactionUpdatingForm convertedAmountCents(Long convertedAmountCents) {
        this.transaction.convertedAmountCents = convertedAmountCents;
        return this;
    }

    public TransactionUpdatingForm convertedAmountCurrency(String convertedAmountCurrency) {
        this.transaction.convertedAmountCurrency = convertedAmountCurrency;
        return this;
    }

    public TransactionUpdatingForm comment(String comment) {
        this.transaction.comment = comment;
        return this;
    }

    public TransactionUpdatingForm borrowId(Long borrowId) {
        this.transaction.borrowId = borrowId;
        return this;
    }

    @Data
    public class TransactionUF {
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
        private LocalDateTime gotAt;

        @ApiModelProperty(value = "")
        private Boolean buyingAsset = null;

        @ApiModelProperty(value = "")
        private Long borrowId = null;

        @ApiModelProperty(value = "")
        private Boolean updateSimilarTransactions = null;
    }
}

