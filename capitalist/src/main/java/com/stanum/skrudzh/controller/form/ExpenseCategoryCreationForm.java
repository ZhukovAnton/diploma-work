package com.stanum.skrudzh.controller.form;

import com.stanum.skrudzh.model.dto.Reminder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * ExpenseCategoryCreationFormExpenseCategory
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class ExpenseCategoryCreationForm {

    private ExpenseCategoryCF expenseCategory;

    public ExpenseCategoryCreationForm name(String name) {
        this.expenseCategory.name = name;
        return this;
    }

    public ExpenseCategoryCreationForm iconUrl(String iconUrl) {
        this.expenseCategory.iconUrl = iconUrl;
        return this;
    }

    public ExpenseCategoryCreationForm currency(String currency) {
        this.expenseCategory.currency = currency;
        return this;
    }

    public ExpenseCategoryCreationForm monthlyPlannedCents(Long monthlyPlannedCents) {
        this.expenseCategory.monthlyPlannedCents = monthlyPlannedCents;
        return this;
    }

    public ExpenseCategoryCreationForm rowOrderPosition(Integer rowOrderPosition) {
        this.expenseCategory.rowOrderPosition = rowOrderPosition;
        return this;
    }

    public ExpenseCategoryCreationForm reminderAttributes(Reminder reminderAttributes) {
        this.expenseCategory.reminderAttributes = reminderAttributes;
        return this;
    }

    public ExpenseCategoryCreationForm prototypeKey(String prototypeKey) {
        this.expenseCategory.prototypeKey = prototypeKey;
        return this;
    }

    @Data
    public class ExpenseCategoryCF {

        @ApiModelProperty(value = "")
        private String name = null;

        @ApiModelProperty(value = "")
        private String iconUrl = null;

        @ApiModelProperty(value = "")
        private String currency = null;

        @ApiModelProperty(value = "")
        private String description = null;

        @ApiModelProperty(value = "")
        private Long monthlyPlannedCents = null;

        @ApiModelProperty(value = "")
        private Integer rowOrderPosition = null;

        @ApiModelProperty(value = "")
        private Reminder reminderAttributes = null;

        @ApiModelProperty(value = "")
        private String prototypeKey = null;
    }


}

