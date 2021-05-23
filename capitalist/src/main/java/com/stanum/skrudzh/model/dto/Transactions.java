package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * Transactions
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class Transactions {

    @ApiModelProperty(value = "")
    private List<Transaction> transactions = new ArrayList<Transaction>();

    public Transactions transactions(List<Transaction> transactions) {
        this.transactions = transactions;
        return this;
    }

    public Transactions addTransactionsItem(Transaction transactionsItem) {
        this.transactions.add(transactionsItem);
        return this;
    }

}

