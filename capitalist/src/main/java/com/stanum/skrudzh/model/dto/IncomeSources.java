package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * IncomeSources
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class IncomeSources {

    @ApiModelProperty(value = "")
    private List<IncomeSource> incomeSources = new ArrayList<IncomeSource>();

    public IncomeSources incomeSources(List<IncomeSource> incomeSources) {
        this.incomeSources = incomeSources;
        return this;
    }

    public IncomeSources addIncomeSourcesItem(IncomeSource incomeSourcesItem) {
        this.incomeSources.add(incomeSourcesItem);
        return this;
    }
}

