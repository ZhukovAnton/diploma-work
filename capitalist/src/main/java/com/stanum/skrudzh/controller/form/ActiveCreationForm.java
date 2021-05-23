package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.controller.form.attributes.ActiveTransactionAttributes;
import com.stanum.skrudzh.controller.form.saltedge.AccountConnectionAttributes;
import com.stanum.skrudzh.model.dto.Reminder;
import com.stanum.skrudzh.model.enums.PlannedIncomeTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * ActiveCreationFormActive
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class ActiveCreationForm {

    private ActiveCF active;

    public ActiveCreationForm name(String name) {
        this.active.name = name;
        return this;
    }

    public ActiveCreationForm iconUrl(String iconUrl) {
        this.active.iconUrl = iconUrl;
        return this;
    }

    public ActiveCreationForm currency(String currency) {
        this.active.currency = currency;
        return this;
    }

    public ActiveCreationForm activeTypeId(Long activeTypeId) {
        this.active.activeTypeId = activeTypeId;
        return this;
    }

    public ActiveCreationForm costCents(Long costCents) {
        this.active.costCents = costCents;
        return this;
    }

    public ActiveCreationForm alreadyPaidCents(Long alreadyPaidCents) {
        this.active.alreadyPaidCents = alreadyPaidCents;
        return this;
    }

    public ActiveCreationForm monthlyPaymentCents(Long monthlyPaymentCents) {
        this.active.monthlyPaymentCents = monthlyPaymentCents;
        return this;
    }

    public ActiveCreationForm isIncomePlanned(Boolean isIncomePlanned) {
        this.active.isIncomePlanned = isIncomePlanned;
        return this;
    }

    public ActiveCreationForm plannedIncomeType(PlannedIncomeTypeEnum plannedIncomeType) {
        this.active.plannedIncomeType = plannedIncomeType;
        return this;
    }

    public ActiveCreationForm monthlyPlannedIncomeCents(Long monthlyPlannedIncomeCents) {
        this.active.monthlyPlannedIncomeCents = monthlyPlannedIncomeCents;
        return this;
    }

    public ActiveCreationForm annualIncomePercent(Integer annualIncomePercent) {
        this.active.annualIncomePercent = annualIncomePercent;
        return this;
    }

    public ActiveCreationForm goalAmountCents(Long goalAmountCents) {
        this.active.goalAmountCents = goalAmountCents;
        return this;
    }

    public ActiveCreationForm rowOrderPosition(Integer rowOrderPosition) {
        this.active.rowOrderPosition = rowOrderPosition;
        return this;
    }

    public ActiveCreationForm reminderAttributes(Reminder reminderAttributes) {
        this.active.reminderAttributes = reminderAttributes;
        return this;
    }

    @Data
    public class ActiveCF {
        @ApiModelProperty(value = "")
        private String name = null;

        @ApiModelProperty(value = "")
        private String iconUrl = null;

        @ApiModelProperty(value = "")
        private String currency = null;

        @ApiModelProperty(value = "")
        private Long activeTypeId = null;

        @ApiModelProperty(value = "")
        private Long costCents = null;

        @ApiModelProperty(value = "")
        private Long alreadyPaidCents = null;

        @ApiModelProperty(value = "")
        private Long monthlyPaymentCents = null;

        @ApiModelProperty(value = "")
        private Boolean isIncomePlanned = false;

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
        private Reminder reminderAttributes = null;

        @ApiModelProperty(value = "")
        private AccountConnectionAttributes accountConnectionAttributes = null;

        @ApiModelProperty(value = "")
        private ActiveTransactionAttributes activeTransactionAttributes = null;

    }
}

