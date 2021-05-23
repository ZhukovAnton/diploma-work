package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.controller.form.saltedge.AccountConnectionAttributes;
import com.stanum.skrudzh.model.enums.CardTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * ExpenseSourceUpdatingFormExpenseSource
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class ExpenseSourceUpdatingForm {

    private ExpenseSourceUF expenseSource;

    public ExpenseSourceUpdatingForm name(String name) {
        this.expenseSource.name = name;
        return this;
    }

    public ExpenseSourceUpdatingForm iconUrl(String iconUrl) {
        this.expenseSource.iconUrl = iconUrl;
        return this;
    }

    public ExpenseSourceUpdatingForm amountCents(Long amountCents) {
        this.expenseSource.amountCents = amountCents;
        return this;
    }

    public ExpenseSourceUpdatingForm creditLimitCents(Long creditLimitCents) {
        this.expenseSource.creditLimitCents = creditLimitCents;
        return this;
    }

    public ExpenseSourceUpdatingForm rowOrderPosition(Integer rowOrderPosition) {
        this.expenseSource.rowOrderPosition = rowOrderPosition;
        return this;
    }

    public ExpenseSourceUpdatingForm accountConnectionAttributes(AccountConnectionAttributes accountConnectionAttributes) {
        this.expenseSource.accountConnectionAttributes = accountConnectionAttributes;
        return this;
    }

    public ExpenseSourceUpdatingForm currency(String currency) {
        this.expenseSource.currency = currency;
        return this;
    }

    public ExpenseSourceUpdatingForm prototypeKey(String prototypeKey) {
        this.expenseSource.prototypeKey = prototypeKey;
        return this;
    }

    @Data
    public class ExpenseSourceUF {
        @ApiModelProperty(value = "")
        private String name = null;

        @ApiModelProperty(value = "")
        private String iconUrl = null;

        @ApiModelProperty(value = "")
        private Long amountCents = null;

        @ApiModelProperty(value = "")
        private Long creditLimitCents = null;

        @ApiModelProperty(value = "")
        private CardTypeEnum cardType;

        @ApiModelProperty(value = "")
        private Integer rowOrderPosition = null;

        @ApiModelProperty(value = "")
        private Integer maxFetchInterval = null;

        @ApiModelProperty(value = "")
        private String currency = null;

        @ApiModelProperty(value = "")
        private String prototypeKey = null;

        @ApiModelProperty(value = "")
        private AccountConnectionAttributes accountConnectionAttributes = null;
    }
}

