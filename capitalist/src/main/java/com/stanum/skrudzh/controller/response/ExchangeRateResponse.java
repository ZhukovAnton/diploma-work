package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.ExchangeRate;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * ExchangeRateResponse
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class ExchangeRateResponse {

    @ApiModelProperty(value = "")
    private ExchangeRate exchangeRate = null;

    public ExchangeRateResponse exchangeRate(ExchangeRate exchangeRate) {
        this.exchangeRate = exchangeRate;
        return this;
    }

}

