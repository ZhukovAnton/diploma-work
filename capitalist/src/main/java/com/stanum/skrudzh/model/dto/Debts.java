package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * Debts
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class Debts {

    @ApiModelProperty(value = "")
    private List<Borrow> debts = new ArrayList<Borrow>();

    public Debts debts(List<Borrow> debts) {
        this.debts = debts;
        return this;
    }

    public Debts addDebtsItem(Borrow debtsItem) {
        this.debts.add(debtsItem);
        return this;
    }

}

