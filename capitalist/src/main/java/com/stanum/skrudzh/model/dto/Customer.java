package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Customer
 */
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class Customer {

    @ApiModelProperty(value = "")
    private String id;

    @ApiModelProperty(value = "")
    private String secret;

    public Customer id(String id) {
        this.id = id;
        return this;
    }

    public Customer secret(String secret) {
        this.secret = secret;
        return this;
    }

}
