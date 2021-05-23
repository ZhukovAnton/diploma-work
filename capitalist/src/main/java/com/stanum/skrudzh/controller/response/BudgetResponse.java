package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.Budget;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * BudgetResponse
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class BudgetResponse {

    @ApiModelProperty(value = "")
    private Budget budget = null;

    public BudgetResponse budget(Budget budget) {
        this.budget = budget;
        return this;
    }

}

