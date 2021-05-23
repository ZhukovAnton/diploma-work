package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * Loans
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class Loans {

    @ApiModelProperty(value = "")
    private List<Borrow> loans = new ArrayList<Borrow>();

    public Loans loans(List<Borrow> loans) {
        this.loans = loans;
        return this;
    }

    public Loans addLoansItem(Borrow loansItem) {
        this.loans.add(loansItem);
        return this;
    }

}

