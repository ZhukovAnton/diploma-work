package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.Transaction;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * TransactionResponse
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class TransactionResponse {

    @ApiModelProperty(value = "")
    private Transaction transaction = null;

    public TransactionResponse transaction(Transaction transaction) {
        this.transaction = transaction;
        return this;
    }
}
