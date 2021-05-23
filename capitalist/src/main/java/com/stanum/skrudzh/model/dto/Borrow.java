package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import com.stanum.skrudzh.model.dto.base.Ordered;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZoneId;
import java.time.ZonedDateTime;


/**
 * Borrow
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode(callSuper = true)
@ToString
@Data
public class Borrow extends Ordered {

    public Borrow(BorrowEntity borrowEntity) {
        id = borrowEntity.getId();
        userId = borrowEntity.getUser().getId();
        type = borrowEntity.getType();
        name = borrowEntity.getName();
        amountCents = borrowEntity.getAmountCents().longValue();
        comment = borrowEntity.getComment();
        isReturned = borrowEntity.getIsReturned();
        iconUrl = borrowEntity.getIconUrl();
        borrowedAt = borrowEntity.getBorrowedAt() != null
                ? ZonedDateTime.of(borrowEntity.getBorrowedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        payday = borrowEntity.getPayday() != null
                ? ZonedDateTime.of(borrowEntity.getPayday().toLocalDateTime(), ZoneId.of("Z"))
                : null;
    }

    @ApiModelProperty(value = "")
    private Long userId = null;

    @ApiModelProperty(value = "")
    private BorrowTypeEnum type = null;

    @ApiModelProperty(value = "")
    private String name = null;

    @ApiModelProperty(value = "")
    private Long amountCents = null;

    @ApiModelProperty(value = "")
    private Currency currency = null;

    @ApiModelProperty(value = "")
    private String comment = null;

    @ApiModelProperty(value = "")
    private Boolean isReturned = null;

    @ApiModelProperty(value = "")
    private Long amountCentsLeft = null;

    @ApiModelProperty(value = "")
    private Long returnedAmountCents = null;

    @ApiModelProperty(value = "")
    private String iconUrl = null;

    @ApiModelProperty(value = "")
    private Long borrowingTransactionId = null;

    @ApiModelProperty(value = "")
    private Reminder reminder = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime borrowedAt = null;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime payday = null;

    public Borrow id(Long id) {
        this.id = id;
        return this;
    }

    public Borrow userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Borrow type(BorrowTypeEnum type) {
        this.type = type;
        return this;
    }

    public Borrow name(String name) {
        this.name = name;
        return this;
    }

    public Borrow amountCents(Long amountCents) {
        this.amountCents = amountCents;
        return this;
    }

    public Borrow currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public Borrow comment(String comment) {
        this.comment = comment;
        return this;
    }

    public Borrow isReturned(Boolean isReturned) {
        this.isReturned = isReturned;
        return this;
    }

    public Borrow amountCentsLeft(Long amountCentsLeft) {
        this.amountCentsLeft = amountCentsLeft;
        return this;
    }

    public Borrow returnedAmountCents(Long returnedAmountCents) {
        this.returnedAmountCents = returnedAmountCents;
        return this;
    }

    public Borrow iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public Borrow borrowingTransactionId(Long borrowingTransactionId) {
        this.borrowingTransactionId = borrowingTransactionId;
        return this;
    }

    public Borrow reminder(Reminder reminder) {
        this.reminder = reminder;
        return this;
    }

}

