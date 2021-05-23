package com.stanum.skrudzh.controller.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * LoanUpdatingForm
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class LoanUpdatingForm {

    @ApiModelProperty(value = "")
    private BorrowUpdatingForm loan = null;

    public LoanUpdatingForm loan(BorrowUpdatingForm loan) {
        this.loan = loan;
        return this;
    }

}

