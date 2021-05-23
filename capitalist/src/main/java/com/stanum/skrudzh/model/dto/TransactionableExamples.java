package com.stanum.skrudzh.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class TransactionableExamples {

    private List<TransactionableExample> transactionableExamples;

    public TransactionableExamples(List<TransactionableExample> transactionableExamples) {
        this.transactionableExamples = transactionableExamples;
    }
}
