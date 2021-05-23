package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * Currency
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@NoArgsConstructor
public class Currency {

    @ApiModelProperty(value = "")
    private String disambiguateSymbol = null;

    @ApiModelProperty(value = "")
    private String htmlEntity = null;

    @ApiModelProperty(value = "")
    private String isoCode = null;

    @ApiModelProperty(value = "")
    private String isoNumeric = null;

    @ApiModelProperty(value = "")
    private String name = null;

    @ApiModelProperty(value = "")
    private String translatedName = null;

    @ApiModelProperty(value = "")
    private String symbol = null;

    @ApiModelProperty(value = "")
    private Integer smallestDenomination = null;

    @ApiModelProperty(value = "")
    private String subunit = null;

    @ApiModelProperty(value = "")
    private Integer subunitToUnit = null;

    @ApiModelProperty(value = "")
    private String decimalMark = null;

    @ApiModelProperty(value = "")
    private Boolean symbolFirst = null;

    @ApiModelProperty(value = "")
    private Integer priority = null;

    @ApiModelProperty(value = "")
    private String thousandsSeparator = null;

    public Currency isoCode(String isoCode) {
        this.isoCode = isoCode;
        return this;
    }

    public Currency name(String name) {
        this.name = name;
        return this;
    }

    public Currency translatedName(String translatedName) {
        this.translatedName = translatedName;
        return this;
    }

    public Currency symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public Currency subunitToUnit(Integer subunitToUnit) {
        this.subunitToUnit = subunitToUnit;
        return this;
    }

    public Currency decimalMark(String decimalMark) {
        this.decimalMark = decimalMark;
        return this;
    }

    public Currency symbolFirst(Boolean symbolFirst) {
        this.symbolFirst = symbolFirst;
        return this;
    }

    public Currency priority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public Currency thousandsSeparator(String thousandsSeparator) {
        this.thousandsSeparator = thousandsSeparator;
        return this;
    }

}

