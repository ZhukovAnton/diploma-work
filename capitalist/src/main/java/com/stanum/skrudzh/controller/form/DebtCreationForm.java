package com.stanum.skrudzh.controller.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * DebtCreationForm
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class DebtCreationForm {

    @ApiModelProperty(value = "")
    private BorrowCreationForm debt = null;

    public DebtCreationForm debt(BorrowCreationForm debt) {
        this.debt = debt;
        return this;
    }

}

