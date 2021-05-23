package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.model.dto.Reminder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;


/**
 * CreditUpdatingFormCredit
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class CreditUpdatingForm {

    private CreditUF credit;

    public CreditUpdatingForm name(String name) {
        this.credit.name = name;
        return this;
    }

    public CreditUpdatingForm iconUrl(String iconUrl) {
        this.credit.iconUrl = iconUrl;
        return this;
    }

    public CreditUpdatingForm amountCents(Long amountCents) {
        this.credit.amountCents = amountCents;
        return this;
    }

    public CreditUpdatingForm returnAmountCents(Long returnAmountCents) {
        this.credit.returnAmountCents = returnAmountCents;
        return this;
    }

    public CreditUpdatingForm monthlyPaymentCents(Long monthlyPaymentCents) {
        this.credit.monthlyPaymentCents = monthlyPaymentCents;
        return this;
    }

    public CreditUpdatingForm period(Integer period) {
        this.credit.period = period;
        return this;
    }

    public CreditUpdatingForm reminderAttributes(Reminder reminderAttributes) {
        this.credit.reminderAttributes = reminderAttributes;
        return this;
    }

    public CreditUpdatingForm rowOrderPosition(Integer rowOrderPosition) {
        this.credit.rowOrderPosition = rowOrderPosition;
        return this;
    }

    @Data
    public class CreditUF {
        @ApiModelProperty(value = "")
        private String name = null;

        @ApiModelProperty(value = "")
        private String iconUrl = null;

        @ApiModelProperty(value = "")
        private Long amountCents = null;

        @ApiModelProperty(value = "")
        private Long returnAmountCents = null;

        @ApiModelProperty(value = "")
        private Long monthlyPaymentCents = null;

        @ApiModelProperty(value = "")
        private LocalDateTime gotAt;

        @ApiModelProperty(value = "")
        private Integer period = null;

        @ApiModelProperty(value = "")
        private Reminder reminderAttributes = null;

        @ApiModelProperty(value = "")
        private Integer rowOrderPosition = null;
    }

}

