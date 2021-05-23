package com.stanum.skrudzh.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * Baskets
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class Baskets {

    @ApiModelProperty(value = "")
    private List<Basket> baskets = new ArrayList<Basket>();

    public Baskets baskets(List<Basket> baskets) {
        this.baskets = baskets;
        return this;
    }

    public Baskets addBasketsItem(Basket basketsItem) {
        this.baskets.add(basketsItem);
        return this;
    }
}

