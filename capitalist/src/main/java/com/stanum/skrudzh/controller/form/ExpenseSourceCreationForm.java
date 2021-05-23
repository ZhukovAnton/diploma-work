package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.controller.form.saltedge.AccountConnectionAttributes;
import com.stanum.skrudzh.model.enums.CardTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * ExpenseSourceCreationFormExpenseSource
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class ExpenseSourceCreationForm {

    ExpenseSourceCF expenseSource;

    public ExpenseSourceCreationForm name(String name) {
        this.expenseSource.name = name;
        return this;
    }

    public ExpenseSourceCreationForm iconUrl(String iconUrl) {
        this.expenseSource.iconUrl = iconUrl;
        return this;
    }

    public ExpenseSourceCreationForm currency(String currency) {
        this.expenseSource.currency = currency;
        return this;
    }

    public ExpenseSourceCreationForm amountCents(Long amountCents) {
        this.expenseSource.amountCents = amountCents;
        return this;
    }

    public ExpenseSourceCreationForm creditLimitCents(Long creditLimitCents) {
        this.expenseSource.creditLimitCents = creditLimitCents;
        return this;
    }

    public ExpenseSourceCreationForm rowOrderPosition(Integer rowOrderPosition) {
        this.expenseSource.rowOrderPosition = rowOrderPosition;
        return this;
    }

    public ExpenseSourceCreationForm accountConnectionAttributes(AccountConnectionAttributes accountConnectionAttributes) {
        this.expenseSource.accountConnectionAttributes = accountConnectionAttributes;
        return this;
    }

    @Data
    public class ExpenseSourceCF {
        @ApiModelProperty(value = "")
        private String name = null;

        @ApiModelProperty(value = "")
        private String iconUrl = null;

        @ApiModelProperty(value = "")
        private String currency = null;

        @ApiModelProperty(value = "")
        private Long amountCents = null;

        @ApiModelProperty(value = "")
        private Long creditLimitCents = null;

        @ApiModelProperty(value = "")
        private Integer rowOrderPosition = null;

        private CardTypeEnum cardType;

        private String prototypeKey = null;

        @ApiModelProperty(value = "")
        private Integer maxFetchInterval;

        @ApiModelProperty(value = "")
        private AccountConnectionAttributes accountConnectionAttributes = null;
    }
}

