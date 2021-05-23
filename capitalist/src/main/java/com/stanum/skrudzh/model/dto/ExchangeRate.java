package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;


/**
 * ExchangeRate
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class ExchangeRate {

    @ApiModelProperty(value = "")
    private String from = null;

    @ApiModelProperty(value = "")
    private String to = null;

    @ApiModelProperty(value = "")
    private BigDecimal rate;

    public ExchangeRate from(String from) {
        this.from = from;
        return this;
    }

    public ExchangeRate to(String to) {
        this.to = to;
        return this;
    }
}

