package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.ExpenseSource;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * ExpenseSourceResponse
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class ExpenseSourceResponse {

    @ApiModelProperty(value = "")
    private ExpenseSource expenseSource = null;

    public ExpenseSourceResponse expenseSource(ExpenseSource expenseSource) {
        this.expenseSource = expenseSource;
        return this;
    }
}

