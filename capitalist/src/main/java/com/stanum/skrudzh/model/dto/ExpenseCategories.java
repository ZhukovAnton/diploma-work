package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * ExpenseCategories
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class ExpenseCategories {

    @ApiModelProperty(value = "")
    private List<ExpenseCategory> expenseCategories = new ArrayList<ExpenseCategory>();

    public ExpenseCategories expenseCategories(List<ExpenseCategory> expenseCategories) {
        this.expenseCategories = expenseCategories;
        return this;
    }

    public ExpenseCategories addExpenseCategoriesItem(ExpenseCategory expenseCategoriesItem) {
        this.expenseCategories.add(expenseCategoriesItem);
        return this;
    }

}

