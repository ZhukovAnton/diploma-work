package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * ExpenseSources
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class ExpenseSources {

    @ApiModelProperty(value = "")
    private List<ExpenseSource> expenseSources = new ArrayList<ExpenseSource>();

    public ExpenseSources expenseSources(List<ExpenseSource> expenseSources) {
        this.expenseSources = expenseSources;
        return this;
    }

    public ExpenseSources addExpenseSourcesItem(ExpenseSource expenseSourcesItem) {
        this.expenseSources.add(expenseSourcesItem);
        return this;
    }
}

