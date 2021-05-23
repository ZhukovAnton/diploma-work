package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.model.enums.CardTypeEnum;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;


/**
 * ExpenseSource
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class ExpenseSource {

    public ExpenseSource(ExpenseSourceEntity expenseSourceEntity) {
        amountCents = expenseSourceEntity.getAmountCents() != null
                ? expenseSourceEntity.getAmountCents().longValue()
                : null;
        id = expenseSourceEntity.getId();
        creditLimitCents = expenseSourceEntity.getCreditLimitCents() != null
                ? expenseSourceEntity.getCreditLimitCents().longValue()
                : null;
        isVirtual = expenseSourceEntity.getIsVirtual();
        name = expenseSourceEntity.getName();
        iconUrl = expenseSourceEntity.getIconUrl();
        rowOrder = expenseSourceEntity.getRowOrder();
        maxFetchInterval = expenseSourceEntity.getMaxFetchInterval();
        cardType = expenseSourceEntity.getCardType();
        userId = expenseSourceEntity.getUser() != null
                ? expenseSourceEntity.getUser().getId()
                : null;
        createdAt = expenseSourceEntity.getCreatedAt() != null
                ? ZonedDateTime.of(expenseSourceEntity.getCreatedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        deletedAt = expenseSourceEntity.getDeletedAt() != null
                ? ZonedDateTime.of(expenseSourceEntity.getDeletedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;

    }

    @ApiModelProperty(value = "")
    private Long id = null;

    @ApiModelProperty(value = "")
    private Long userId = null;

    @ApiModelProperty(value = "")
    private String name = null;

    @ApiModelProperty(value = "")
    private String iconUrl = null;

    @ApiModelProperty(value = "")
    private String amount = null;

    @ApiModelProperty(value = "")
    private Long amountCents = null;

    @ApiModelProperty(value = "")
    private Currency currency = null;

    @ApiModelProperty(value = "")
    private Long creditLimitCents = null;

    @ApiModelProperty(value = "")
    private Integer rowOrder = null;

    @ApiModelProperty(value = "")
    private Boolean isVirtual = null;

    @ApiModelProperty(value = "")
    private Integer maxFetchInterval = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime deletedAt;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime fetchFromDate;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    //To maintain backward compatibility
    private ZonedDateTime fetchDataFrom;

    @ApiModelProperty(value = "")
    private List<String> providerCodes = null;

    @ApiModelProperty(value = "")
    private Boolean hasTransactions = null;

    @ApiModelProperty(value = "")
    private String prototypeKey = null;

    private CardTypeEnum cardType;

    private AccountConnection saltEdgeAccountConnection;

    public ExpenseSource id(Long id) {
        this.id = id;
        return this;
    }

    public ExpenseSource userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public ExpenseSource name(String name) {
        this.name = name;
        return this;
    }

    public ExpenseSource iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public ExpenseSource amount(String amount) {
        this.amount = amount;
        return this;
    }

    public ExpenseSource amountCents(Long amountCents) {
        this.amountCents = amountCents;
        return this;
    }

    public ExpenseSource currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public ExpenseSource creditLimitCents(Long creditLimitCents) {
        this.creditLimitCents = creditLimitCents;
        return this;
    }

    public ExpenseSource rowOrder(Integer rowOrder) {
        this.rowOrder = rowOrder;
        return this;
    }

    public ExpenseSource isVirtual(Boolean isVirtual) {
        this.isVirtual = isVirtual;
        return this;
    }

    public ExpenseSource providerCodes(List<String> providerCodes) {
        this.providerCodes = providerCodes;
        return this;
    }

    public ExpenseSource hasTransactions(Boolean hasTransactions) {
        this.hasTransactions = hasTransactions;
        return this;
    }

}

