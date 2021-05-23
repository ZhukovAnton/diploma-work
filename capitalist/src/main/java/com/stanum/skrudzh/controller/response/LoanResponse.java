package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.Borrow;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * InlineResponse2007
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class LoanResponse {

    @ApiModelProperty(value = "")
    private Borrow loan = null;

    public LoanResponse loan(Borrow loan) {
        this.loan = loan;
        return this;
    }
}

