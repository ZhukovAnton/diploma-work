package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Currencies
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Currencies {

    @ApiModelProperty(value = "")
    private List<Currency> currencies = new ArrayList<Currency>();

    public Currencies currencies(List<Currency> currencies) {
        this.currencies = currencies;
        return this;
    }

    public Currencies addCurrenciesItem(Currency currenciesItem) {
        this.currencies.add(currenciesItem);
        return this;
    }
}

