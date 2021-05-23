package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.controller.form.saltedge.AccountConnectionAttributes;
import com.stanum.skrudzh.model.dto.Reminder;
import com.stanum.skrudzh.model.enums.PlannedIncomeTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * ActiveUpdatingFormActive
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class ActiveUpdatingForm {

    private ActiveUF active;

    public ActiveUpdatingForm name(String name) {
        this.active.name = name;
        return this;
    }

    public ActiveUpdatingForm iconUrl(String iconUrl) {
        this.active.iconUrl = iconUrl;
        return this;
    }

    public ActiveUpdatingForm currency(String currency) {
        this.active.currency = currency;
        return this;
    }

    public ActiveUpdatingForm costCents(Long costCents) {
        this.active.costCents = costCents;
        return this;
    }

    public ActiveUpdatingForm monthlyPaymentCents(Long monthlyPaymentCents) {
        this.active.monthlyPaymentCents = monthlyPaymentCents;
        return this;
    }

    public ActiveUpdatingForm isIncomePlanned(Boolean isIncomePlanned) {
        this.active.isIncomePlanned = isIncomePlanned;
        return this;
    }

    public ActiveUpdatingForm plannedIncomeType(PlannedIncomeTypeEnum plannedIncomeType) {
        this.active.plannedIncomeType = plannedIncomeType;
        return this;
    }

    public ActiveUpdatingForm monthlyPlannedIncomeCents(Long monthlyPlannedIncomeCents) {
        this.active.monthlyPlannedIncomeCents = monthlyPlannedIncomeCents;
        return this;
    }

    public ActiveUpdatingForm annualIncomePercent(Integer annualIncomePercent) {
        this.active.annualIncomePercent = annualIncomePercent;
        return this;
    }

    public ActiveUpdatingForm goalAmountCents(Long goalAmountCents) {
        this.active.goalAmountCents = goalAmountCents;
        return this;
    }

    public ActiveUpdatingForm rowOrderPosition(Integer rowOrderPosition) {
        this.active.rowOrderPosition = rowOrderPosition;
        return this;
    }

    public ActiveUpdatingForm reminderAttributes(Reminder reminderAttributes) {
        this.active.reminderAttributes = reminderAttributes;
        return this;
    }

    @Data
    public class ActiveUF {
        @ApiModelProperty(value = "")
        private String name = null;

        @ApiModelProperty(value = "")
        private String iconUrl = null;

        @ApiModelProperty(value = "")
        private String currency = null;

        @ApiModelProperty(value = "")
        private Long costCents = null;

        @ApiModelProperty(value = "")
        private Long monthlyPaymentCents = null;

        @ApiModelProperty(value = "")
        private Boolean isIncomePlanned = null;

        @ApiModelProperty(value = "")
        private PlannedIncomeTypeEnum plannedIncomeType = null;

        @ApiModelProperty(value = "")
        private Long monthlyPlannedIncomeCents = null;

        @ApiModelProperty(value = "")
        private Integer annualIncomePercent = null;

        @ApiModelProperty(value = "")
        private Long goalAmountCents = null;

        @ApiModelProperty(value = "")
        private Integer rowOrderPosition = null;

        @ApiModelProperty(value = "")
        private Integer maxFetchInterval = null;

        @ApiModelProperty(value = "")
        private Reminder reminderAttributes = null;

        @ApiModelProperty(value = "")
        private AccountConnectionAttributes accountConnectionAttributes;
    }

}

