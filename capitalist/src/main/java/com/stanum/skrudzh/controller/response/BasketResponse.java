package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.Basket;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * InlineResponse2001
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@AllArgsConstructor
public class BasketResponse {

    @ApiModelProperty(value = "")
    private Basket basket = null;

    public BasketResponse basket(Basket basket) {
        this.basket = basket;
        return this;
    }


}

