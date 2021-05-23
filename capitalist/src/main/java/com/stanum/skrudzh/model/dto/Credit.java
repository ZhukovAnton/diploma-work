package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.CreditEntity;
import com.stanum.skrudzh.model.dto.base.Ordered;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZoneId;
import java.time.ZonedDateTime;


/**
 * Credit
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode(callSuper = true)
@ToString
@Data
public class Credit extends Ordered {

    public Credit(CreditEntity credictEntity) {
        id = credictEntity.getId();
        userId = credictEntity.getUser().getId();
        name = credictEntity.getName();
        iconUrl = credictEntity.getIconUrl();
        amountCents = credictEntity.getAmountCents().longValue();
        returnAmountCents = credictEntity.getReturnAmountCents().longValue();
        monthlyPaymentCents = credictEntity.getMonthlyPaymentCents() != null
                ? credictEntity.getMonthlyPaymentCents().longValue()
                : null;
        period = credictEntity.getPeriod();
        isPaid = credictEntity.getIsPaid();
        gotAt = credictEntity.getGotAt() != null
                ? ZonedDateTime.of(credictEntity.getGotAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        deletedAt = credictEntity.getDeletedAt() != null
                ? ZonedDateTime.of(credictEntity.getDeletedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
    }

    @ApiModelProperty(value = "")
    private Long userId = null;

    @ApiModelProperty(value = "")
    private String name = null;

    @ApiModelProperty(value = "")
    private String iconUrl = null;

    @ApiModelProperty(value = "")
    private Currency currency = null;

    @ApiModelProperty(value = "")
    private Long amountCents = null;

    @ApiModelProperty(value = "")
    private Long returnAmountCents = null;

    @ApiModelProperty(value = "")
    private Long paidAmountCents = null;

    @ApiModelProperty(value = "")
    private Long amountLeftCents = null;

    @ApiModelProperty(value = "")
    private Long monthlyPaymentCents = null;

    @ApiModelProperty(value = "")
    private Integer period = null;

    @ApiModelProperty(value = "")
    private Boolean isPaid = null;

    @ApiModelProperty(value = "")
    private Long expenseCategoryId = null;

    @ApiModelProperty(value = "")
    private Long creditingTransactionId = null;

    @ApiModelProperty(value = "")
    private CreditType creditType = null;

    @ApiModelProperty(value = "")
    private Reminder reminder = null;

    @ApiModelProperty(value = "")
    private ExpenseCategory expenseCategory = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime gotAt = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime deletedAt = null;

    public Credit id(Long id) {
        this.id = id;
        return this;
    }

    public Credit userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Credit name(String name) {
        this.name = name;
        return this;
    }

    public Credit iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public Credit currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public Credit amountCents(Long amountCents) {
        this.amountCents = amountCents;
        return this;
    }

    public Credit returnAmountCents(Long returnAmountCents) {
        this.returnAmountCents = returnAmountCents;
        return this;
    }

    public Credit paidAmountCents(Long paidAmountCents) {
        this.paidAmountCents = paidAmountCents;
        return this;
    }

    public Credit amountLeftCents(Long amountLeftCents) {
        this.amountLeftCents = amountLeftCents;
        return this;
    }

    public Credit monthlyPaymentCents(Long monthlyPaymentCents) {
        this.monthlyPaymentCents = monthlyPaymentCents;
        return this;
    }

    public Credit period(Integer period) {
        this.period = period;
        return this;
    }

    public Credit isPaid(Boolean isPaid) {
        this.isPaid = isPaid;
        return this;
    }

    public Credit expenseCategoryId(Long expenseCategoryId) {
        this.expenseCategoryId = expenseCategoryId;
        return this;
    }

    public Credit creditingTransactionId(Long creditingTransactionId) {
        this.creditingTransactionId = creditingTransactionId;
        return this;
    }

    public Credit creditType(CreditType creditType) {
        this.creditType = creditType;
        return this;
    }

    public Credit reminder(Reminder reminder) {
        this.reminder = reminder;
        return this;
    }

    public Credit expenseCategory(ExpenseCategory expenseCategory) {
        this.expenseCategory = expenseCategory;
        return this;
    }
}

