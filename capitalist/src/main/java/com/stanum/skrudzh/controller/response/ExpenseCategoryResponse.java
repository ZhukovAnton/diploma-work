package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.ExpenseCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * ExpenseCategoryResponse
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class ExpenseCategoryResponse {

    @ApiModelProperty(value = "")
    private ExpenseCategory expenseCategory = null;

    public ExpenseCategoryResponse expenseCategory(ExpenseCategory expenseCategory) {
        this.expenseCategory = expenseCategory;
        return this;
    }

}

