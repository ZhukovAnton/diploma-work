package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.Customer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class CustomerResponse {
    @ApiModelProperty(value = "")
    private Customer saltEdgeCustomer = null;

    public CustomerResponse credit(Customer saltEdgeCustomer) {
        this.saltEdgeCustomer = saltEdgeCustomer;
        return this;
    }
}
