package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.controller.form.attributes.CreditingTransactionAttributes;
import com.stanum.skrudzh.model.dto.Reminder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;


/**
 * CreditCreationFormCredit
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class CreditCreationForm {

    private CreditCF credit;

    public CreditCreationForm name(String name) {
        this.credit.name = name;
        return this;
    }

    public CreditCreationForm iconUrl(String iconUrl) {
        this.credit.iconUrl = iconUrl;
        return this;
    }

    public CreditCreationForm currency(String currency) {
        this.credit.currency = currency;
        return this;
    }

    public CreditCreationForm creditTypeId(Long creditTypeId) {
        this.credit.creditTypeId = creditTypeId;
        return this;
    }

    public CreditCreationForm amountCents(Long amountCents) {
        this.credit.amountCents = amountCents;
        return this;
    }

    public CreditCreationForm returnAmountCents(Long returnAmountCents) {
        this.credit.returnAmountCents = returnAmountCents;
        return this;
    }

    public CreditCreationForm alreadyPaidCents(Long alreadyPaidCents) {
        this.credit.alreadyPaidCents = alreadyPaidCents;
        return this;
    }

    public CreditCreationForm monthlyPaymentCents(Long monthlyPaymentCents) {
        this.credit.monthlyPaymentCents = monthlyPaymentCents;
        return this;
    }

    public CreditCreationForm period(Integer period) {
        this.credit.period = period;
        return this;
    }

    public CreditCreationForm reminderAttributes(Reminder reminderAttributes) {
        this.credit.reminderAttributes = reminderAttributes;
        return this;
    }

    public CreditCreationForm creditingTransactionAttributes(CreditingTransactionAttributes creditingTransactionAttributes) {
        this.credit.creditingTransactionAttributes = creditingTransactionAttributes;
        return this;
    }

    public CreditCreationForm rowOrderPosition(Integer rowOrderPosition) {
        this.credit.rowOrderPosition = rowOrderPosition;
        return this;
    }

    @Data
    public class CreditCF {

        @ApiModelProperty(value = "")
        private String name = null;

        @ApiModelProperty(value = "")
        private String iconUrl = null;

        @ApiModelProperty(value = "")
        private String currency = null;

        @ApiModelProperty(value = "")
        private Long creditTypeId = null;

        @ApiModelProperty(value = "")
        private Long amountCents = null;

        @ApiModelProperty(value = "")
        private Long returnAmountCents = null;

        @ApiModelProperty(value = "")
        private Long alreadyPaidCents = null;

        @ApiModelProperty(value = "")
        private Long monthlyPaymentCents = null;

        @ApiModelProperty(value = "")
        private Integer period = null;

        @ApiModelProperty(value = "")
        private Reminder reminderAttributes = null;

        @ApiModelProperty(value = "")
        private Integer rowOrderPosition = null;

        @ApiModelProperty(value = "")
        private CreditingTransactionAttributes creditingTransactionAttributes = null;

        @ApiModelProperty(value = "")
        private LocalDateTime gotAt;

    }
}

