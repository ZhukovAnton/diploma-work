package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * CreditTypes
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class CreditTypes {

    @ApiModelProperty(value = "")
    private List<CreditType> creditTypes = new ArrayList<CreditType>();

    public CreditTypes creditTypes(List<CreditType> creditTypes) {
        this.creditTypes = creditTypes;
        return this;
    }

    public CreditTypes addCreditTypesItem(CreditType creditTypesItem) {
        this.creditTypes.add(creditTypesItem);
        return this;
    }

}

