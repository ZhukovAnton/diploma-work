package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * AccountConnections
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class Accounts {

    @ApiModelProperty(value = "")
    private List<Account> accounts = new ArrayList<Account>();

    public Accounts accountConnections(List<Account> accounts) {
        this.accounts = accounts;
        return this;
    }

    public Accounts addAccountConnectionsItem(Account accountConnectionsItem) {
        this.accounts.add(accountConnectionsItem);
        return this;
    }

}

