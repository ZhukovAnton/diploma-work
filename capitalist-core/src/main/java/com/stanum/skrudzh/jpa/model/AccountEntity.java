package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.converters.Encryptor;
import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.model.enums.AccountNatureEnum;
import com.stanum.skrudzh.model.enums.CardTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "accounts")
@Data
@EqualsAndHashCode
@ToString
public class AccountEntity implements Base, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "nature")
    @Enumerated(EnumType.ORDINAL)
    private AccountNatureEnum nature;

    @Column(name = "currency_code")
    private String currencyCode;

    @Basic
    @Column(name = "balance")
    private BigDecimal balance;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "card_type")
    @Enumerated(EnumType.STRING)
    private CardTypeEnum cardType;

    @Column(name = "cards")
    @Convert(converter = Encryptor.class)
    private String cards;

    @Column(name = "credit_limit")
    private BigDecimal creditLimit;

    @Column(name = "status")
    private String status;

    @Column(name = "interest_income")
    private Long interestIncome;

    @Column(name = "interest_amount")
    private Long interestAmount;

    @Column(name = "profit_amount")
    private Long profitAmount;

    @Column(name = "profit_rate")
    private Long profitRate;

    @Column(name = "asset_class")
    private Long assetClass;

    @Column(name = "product_type")
    private Long productType;

    @Column(name = "account_full_name")
    private String accountFullName;

    @Column(name = "fund_holding_investment_percentage")
    private String fundHoldingInvestmentPercentage;

    @Column(name = "fund_holding_balance")
    private BigDecimal fundHoldingBalance;

    @Column(name = "fund_holding_bid_price")
    private BigDecimal fundHoldingBidPrice;

    @Column(name = "fund_holding_value")
    private String fundHoldingValue;

    @Column(name = "fund_holding_value_date")
    private Timestamp fundHoldingValueDate;

    @Column(name = "fund_holding_total_quality")
    private String fundHoldingTotalQuality;

    @Column(name = "fund_holding_available_quality")
    private String fundHoldingAvailableQuality;

    @Column(name = "fund_holding_actual_price")
    private BigDecimal fundHoldingActualPrice;

    @Column(name = "fund_holding_actual_value")
    private String fundHoldingActualValue;


    @ManyToOne
    @JoinColumn(name = "connection_id")
    private ConnectionEntity connectionEntity;

}
