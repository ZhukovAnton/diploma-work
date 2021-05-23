package com.stanum.skrudzh.model.dto;

import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * Basket
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class Basket {

    public Basket(BasketEntity basketEntity) {
        id = basketEntity.getId();
        userId = basketEntity.getUser() != null
                ? basketEntity.getUser().getId()
                : null;
        basketType = basketEntity.getBasketType();
    }

    @ApiModelProperty(value = "")
    private Long id = null;

    @ApiModelProperty(value = "")
    private Long userId = null;

    @ApiModelProperty(value = "")
    private BasketTypeEnum basketType = null;

    @ApiModelProperty(value = "")
    private Long spentCentsAtPeriod = null;

    @ApiModelProperty(value = "")
    private String spentCurrency = null;

    @ApiModelProperty(value = "")
    private Currency currency = null;

    public Basket id(Long id) {
        this.id = id;
        return this;
    }

    public Basket userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Basket basketType(BasketTypeEnum basketType) {
        this.basketType = basketType;
        return this;
    }

    public Basket spentCentsAtPeriod(Long spentCentsAtPeriod) {
        this.spentCentsAtPeriod = spentCentsAtPeriod;
        return this;
    }

    public Basket spentCurrency(String spentCurrency) {
        this.spentCurrency = spentCurrency;
        return this;
    }

    public Basket currency(Currency currency) {
        this.currency = currency;
        return this;
    }
}

