package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.model.enums.AccountNatureEnum;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;


/**
 * AccountConnection
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class Account {

    public Account(AccountEntity accountEntity) {
        accountFullName = accountEntity.getAccountFullName();
        saltEdgeConnectionId = accountEntity.getConnectionEntity().getSaltEdgeConnectionId();
        accountId = accountEntity.getAccountId();
        accountName = accountEntity.getAccountName();
        assetClass = accountEntity.getAssetClass();
        balance = accountEntity.getBalance() != null
                ? accountEntity.getBalance().longValue()
                : 0L;
        cardType = accountEntity.getCardType() != null
                ? accountEntity.getCardType().toString()
                : null;
        cards = accountEntity.getCards() != null && !accountEntity.getCards().isBlank()
                ? Arrays.asList(accountEntity.getCards().split(Constants.CARDS_DELIMITER))
                : null;
        createdAt = accountEntity.getCreatedAt() != null
                ? ZonedDateTime.of(accountEntity.getCreatedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        updatedAt = accountEntity.getUpdatedAt() != null
                ? ZonedDateTime.of(accountEntity.getUpdatedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        creditLimit = accountEntity.getCreditLimit() != null
                ? accountEntity.getCreditLimit().longValue()
                : null;
        fundHoldingActualPrice = accountEntity.getFundHoldingActualPrice();
        fundHoldingAvailableQuality = accountEntity.getFundHoldingAvailableQuality();
        fundHoldingBalance = accountEntity.getFundHoldingBalance() != null
                ? accountEntity.getFundHoldingBalance().longValue()
                : null;
        fundHoldingActualValue = accountEntity.getFundHoldingActualValue();
        fundHoldingBidPrice = accountEntity.getFundHoldingBidPrice() != null
                ? accountEntity.getFundHoldingBidPrice().longValue()
                : null;
        fundHoldingInvestmentPercentage = accountEntity.getFundHoldingInvestmentPercentage();
        fundHoldingTotalQuality = accountEntity.getFundHoldingTotalQuality();
        fundHoldingValue = accountEntity.getFundHoldingValue();
        fundHoldingValueDate = accountEntity.getFundHoldingValueDate() != null
                ? accountEntity.getFundHoldingValueDate().toLocalDateTime().toLocalDate()
                : null;
        id = accountEntity.getId();
        interestAmount = accountEntity.getInterestAmount();
        interestIncome = accountEntity.getInterestIncome();
        nature = accountEntity.getNature();
        profitAmount = accountEntity.getProfitAmount();
        productType = accountEntity.getProductType();
        profitRate = accountEntity.getProfitRate();
        status = accountEntity.getStatus();
    }

    @ApiModelProperty(value = "db entity id")
    private Long id = null;

    @ApiModelProperty(value = "SaltEdge account id")
    private String accountId = null;

    @ApiModelProperty(value = "")
    private String accountName = null;

    @ApiModelProperty(value = "")
    private Long balance = null;

    @ApiModelProperty(value = "")
    private Currency currency = null;

    @ApiModelProperty(value = "")
    private String saltEdgeConnectionId = null;

    @ApiModelProperty(value = "")
    private AccountNatureEnum nature = null;

    @ApiModelProperty(value = "")
    private String cardType;

    @ApiModelProperty(value = "")
    private List<String> cards;

    @ApiModelProperty(value = "")
    private Long creditLimit;

    @ApiModelProperty(value = "")
    private String status;

    @ApiModelProperty(value = "")
    private Long interestIncome;

    @ApiModelProperty(value = "")
    private Long interestAmount;

    @ApiModelProperty(value = "")
    private Long profitAmount;

    @ApiModelProperty(value = "")
    private Long profitRate;

    @ApiModelProperty(value = "")
    private Long assetClass;

    @ApiModelProperty(value = "")
    private Long productType;

    @ApiModelProperty(value = "")
    private String accountFullName;

    @ApiModelProperty(value = "")
    private String fundHoldingInvestmentPercentage;

    @ApiModelProperty(value = "")
    private Long fundHoldingBalance;

    @ApiModelProperty(value = "")
    private Long fundHoldingBidPrice;

    @ApiModelProperty(value = "")
    private String fundHoldingValue;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDate fundHoldingValueDate;

    @ApiModelProperty(value = "")
    private String fundHoldingTotalQuality;

    @ApiModelProperty(value = "")
    private String fundHoldingAvailableQuality;

    @ApiModelProperty(value = "")
    private BigDecimal fundHoldingActualPrice;

    @ApiModelProperty(value = "")
    private String fundHoldingActualValue;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime updatedAt;

    public Account id(Long id) {
        this.id = id;
        return this;
    }

    public Account accountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public Account accountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public Account balance(Long balance) {
        this.balance = balance;
        return this;
    }

    public Account currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public Account saltEdgeConnectionId(String saltEdgeConnectionId) {
        this.saltEdgeConnectionId = saltEdgeConnectionId;
        return this;
    }

    public Account nature(AccountNatureEnum nature) {
        this.nature = nature;
        return this;
    }
}

