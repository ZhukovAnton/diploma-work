package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import com.stanum.skrudzh.model.enums.TransactionTypeEnum;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZoneId;
import java.time.ZonedDateTime;


/**
 * Transaction
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")

@EqualsAndHashCode
@ToString
@Data
public class Transaction {

    public Transaction(TransactionEntity transactionEntity) {
        id = transactionEntity.getId();
        userId = transactionEntity.getUser() != null
                ? transactionEntity.getUser().getId()
                : null;
        transactionType = transactionEntity.getTransactionType();
        sourceId = transactionEntity.getSourceId();
        sourceIconUrl = transactionEntity.getSourceIconUrl();
        sourceTitle = transactionEntity.getSourceTitle();
        sourceType = transactionEntity.getSourceType();
        destinationId = transactionEntity.getDestinationId();
        destinationIconUrl = transactionEntity.getDestinationIconUrl();
        destinationTitle = transactionEntity.getDestinationTitle();
        destinationType = transactionEntity.getDestinationType();
        amountCents = transactionEntity.getAmountCents() != null
                ? transactionEntity.getAmountCents().longValue()
                : 0L;
        basketType = transactionEntity.getBasketType();
        borrowType = transactionEntity.getBorrowType();
        buyingAsset = transactionEntity.getBuyingAsset();
        comment = transactionEntity.getComment();
        convertedAmountCents = transactionEntity.getConvertedAmountCents() != null
                ? transactionEntity.getConvertedAmountCents().longValue()
                : 0L;
        deletedAt = transactionEntity.getDeletedAt() != null
                ? ZonedDateTime.of(transactionEntity.getDeletedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        gotAt = transactionEntity.getGotAt() != null
                ? ZonedDateTime.of(transactionEntity.getGotAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        payday = transactionEntity.getPayday() != null
                ? ZonedDateTime.of(transactionEntity.getPayday().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        isReturned = transactionEntity.getIsReturned();
        whom = transactionEntity.getWhom();
        saltEdgeTransactionId = transactionEntity.getSaltEdgeTransactionId();
        accountId = transactionEntity.getAccountEntity() != null
                ? transactionEntity.getAccountEntity().getId()
                : null;
        isActiveSource = transactionEntity.getIsActiveSource();
        isVirtualSource = transactionEntity.getIsVirtualSource();
        isBorrowOrReturnSource = transactionEntity.getIsBorrowOrReturnSource();
        isVirtualDestination = transactionEntity.getIsVirtualDestination();
        isBorrowOrReturnDestination = transactionEntity.getIsBorrowOrReturnDestination();
        profit = transactionEntity.getProfit() != null
                ? transactionEntity.getProfit().longValue()
                : null;
        saltEdgeTransactionStatus = transactionEntity.getSaltEdgeTransactionStatus() != null
                ? transactionEntity.getSaltEdgeTransactionStatus().toString()
                : null;
        isDuplicated = transactionEntity.getIsDuplicated();
        isChangeable = transactionEntity.getIsChangeable();
        isAutoCategorized = transactionEntity.getIsAutoCategorized();
    }

    @ApiModelProperty(value = "")
    private Long id = null;

    @ApiModelProperty(value = "")
    private Long userId = null;

    @ApiModelProperty(value = "")
    private TransactionTypeEnum transactionType = null;

    @ApiModelProperty(value = "")
    private Long sourceId = null;

    @ApiModelProperty(value = "")
    private String sourceType = null;

    @ApiModelProperty(value = "")
    private Long destinationId = null;

    @ApiModelProperty(value = "")
    private String destinationType = null;

    @ApiModelProperty(value = "")
    private String sourceTitle = null;

    @ApiModelProperty(value = "")
    private String sourceIconUrl = null;

    @ApiModelProperty(value = "")
    private String destinationTitle = null;

    @ApiModelProperty(value = "")
    private String destinationIconUrl = null;

    @ApiModelProperty(value = "")
    private Long amountCents = null;

    @ApiModelProperty(value = "")
    private Currency amountCurrency = null;

    @ApiModelProperty(value = "")
    private Long convertedAmountCents = null;

    @ApiModelProperty(value = "")
    private Currency convertedAmountCurrency = null;

    @ApiModelProperty(value = "")
    private String comment = null;

    @ApiModelProperty(value = "")
    private BasketTypeEnum basketType = null;

    @ApiModelProperty(value = "")
    private BorrowTypeEnum borrowType = null;

    @ApiModelProperty(value = "")
    private String whom = null;

    @ApiModelProperty(value = "")
    private Boolean isReturned = null;

    @ApiModelProperty(value = "")
    private Boolean buyingAsset = null;

    @ApiModelProperty(value = "")
    private Borrow borrow = null;

    @ApiModelProperty(value = "")
    private Credit credit = null;

    @ApiModelProperty(value = "")
    private Borrow returningBorrow = null;

    @ApiModelProperty(value = "")
    private Active active = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime deletedAt;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime gotAt;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime payday;

    private String saltEdgeTransactionId;

    private Long accountId;

    private Boolean isVirtualSource;

    private Boolean isVirtualDestination;

    private Boolean isBorrowOrReturnSource;

    private Boolean isBorrowOrReturnDestination;

    private Boolean isActiveSource;

    private Long profit;

    private Long sourceActiveId;

    private String sourceActiveTitle;

    private String sourceActiveIconUrl;

    private Long sourceIncomeSourceId;

    private String saltEdgeTransactionStatus;

    private Boolean isDuplicated;

    private Boolean isChangeable;

    private Boolean isAutoCategorized;

    public Transaction id(Long id) {
        this.id = id;
        return this;
    }

    public Transaction userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Transaction transactionType(TransactionTypeEnum transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public Transaction sourceId(Long sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    public Transaction sourceType(String sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public Transaction destinationId(Long destinationId) {
        this.destinationId = destinationId;
        return this;
    }

    public Transaction destinationType(String destinationType) {
        this.destinationType = destinationType;
        return this;
    }

    public Transaction sourceTitle(String sourceTitle) {
        this.sourceTitle = sourceTitle;
        return this;
    }

    public Transaction sourceIconUrl(String sourceIconUrl) {
        this.sourceIconUrl = sourceIconUrl;
        return this;
    }

    public Transaction destinationTitle(String destinationTitle) {
        this.destinationTitle = destinationTitle;
        return this;
    }

    public Transaction destinationIconUrl(String destinationIconUrl) {
        this.destinationIconUrl = destinationIconUrl;
        return this;
    }

    public Transaction amountCents(Long amountCents) {
        this.amountCents = amountCents;
        return this;
    }

    public Transaction amountCurrency(Currency amountCurrency) {
        this.amountCurrency = amountCurrency;
        return this;
    }

    public Transaction convertedAmountCents(Long convertedAmountCents) {
        this.convertedAmountCents = convertedAmountCents;
        return this;
    }

    public Transaction convertedAmountCurrency(Currency convertedAmountCurrency) {
        this.convertedAmountCurrency = convertedAmountCurrency;
        return this;
    }

    public Transaction comment(String comment) {
        this.comment = comment;
        return this;
    }

    public Transaction basketType(BasketTypeEnum basketType) {
        this.basketType = basketType;
        return this;
    }

    public Transaction borrowType(BorrowTypeEnum borrowType) {
        this.borrowType = borrowType;
        return this;
    }

    public Transaction whom(String whom) {
        this.whom = whom;
        return this;
    }

    public Transaction isReturned(Boolean isReturned) {
        this.isReturned = isReturned;
        return this;
    }

    public Transaction buyingAsset(Boolean buyingAsset) {
        this.buyingAsset = buyingAsset;
        return this;
    }

    public Transaction borrow(Borrow borrow) {
        this.borrow = borrow;
        return this;
    }

    public Transaction credit(Credit credit) {
        this.credit = credit;
        return this;
    }

    public Transaction returningBorrow(Borrow returningBorrow) {
        this.returningBorrow = returningBorrow;
        return this;
    }
}

