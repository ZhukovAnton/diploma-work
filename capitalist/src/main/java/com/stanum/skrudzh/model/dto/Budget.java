package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * Budget
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class Budget {

    @ApiModelProperty(value = "")
    private Long id = null;

    @ApiModelProperty(value = "")
    private Long incomesAtPeriodCents = null;

    @ApiModelProperty(value = "")
    private Long incomesPlannedAtPeriodCents = null;

    @ApiModelProperty(value = "")
    private Long expenseSourcesAmountCents = null;

    @ApiModelProperty(value = "")
    private Long safeActivesAmountCents = null;

    @ApiModelProperty(value = "")
    private Long riskActivesAmountCents = null;

    @ApiModelProperty(value = "")
    private Long activesAmountCents = null;

    @ApiModelProperty(value = "")
    private Long expensesAtPeriodCents = null;

    @ApiModelProperty(value = "")
    private Long expensesPlannedAtPeriodCents = null;

    @ApiModelProperty(value = "")
    private Currency currency = null;

    public Budget incomesAtPeriodCents(Long incomesAtPeriodCents) {
        this.incomesAtPeriodCents = incomesAtPeriodCents;
        return this;
    }

    public Budget incomesPlannedAtPeriodCents(Long incomesPlannedAtPeriodCents) {
        this.incomesPlannedAtPeriodCents = incomesPlannedAtPeriodCents;
        return this;
    }

    public Budget expenseSourcesAmountCents(Long expenseSourcesAmountCents) {
        this.expenseSourcesAmountCents = expenseSourcesAmountCents;
        return this;
    }

    public Budget safeActivesAmountCents(Long safeActivesAmountCents) {
        this.safeActivesAmountCents = safeActivesAmountCents;
        return this;
    }

    public Budget riskActivesAmountCents(Long riskActivesAmountCents) {
        this.riskActivesAmountCents = riskActivesAmountCents;
        return this;
    }

    public Budget activesAmountCents(Long activesAmountCents) {
        this.activesAmountCents = activesAmountCents;
        return this;
    }

    public Budget expensesAtPeriodCents(Long expensesAtPeriodCents) {
        this.expensesAtPeriodCents = expensesAtPeriodCents;
        return this;
    }

    public Budget expensesPlannedAtPeriodCents(Long expensesPlannedAtPeriodCents) {
        this.expensesPlannedAtPeriodCents = expensesPlannedAtPeriodCents;
        return this;
    }

    public Budget currency(Currency currency) {
        this.currency = currency;
        return this;
    }

}

