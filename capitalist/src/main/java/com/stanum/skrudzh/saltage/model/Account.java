package com.stanum.skrudzh.saltage.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class Account {
    private String id;
    private String name;
    private String nature;
    private BigDecimal balance;
    @JsonProperty("currency_code")
    private String currencyCode;
    @JsonProperty("connection_id")
    private String connectionId;
    private Extra extra;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private List<Transaction> transactions;

    @Data
    @ToString
    public static class Extra {
        @JsonProperty("account_name")
        private String accountName;

        @JsonProperty("card_type")
        private String cardType;
        private String[] cards;

        @JsonProperty("credit_limit")
        private BigDecimal creditLimit;
        private String status;

        @JsonProperty("interest_income")
        private BigDecimal interestIncome;

        @JsonProperty("interest_amount")
        private BigDecimal interestAmount;

        @JsonProperty("profit_amount")
        private BigDecimal profitAmount;

        @JsonProperty("profit_rate")
        private BigDecimal profitRate;

        @JsonProperty("asset_class")
        private BigDecimal assetClass;

        @JsonProperty("product_type")
        private BigDecimal productType;

        @JsonProperty("fund_holdings")
        private FundHoldings fundHoldings;

        @Data
        @ToString
        public static class FundHoldings {

            @JsonProperty("investment_percentage")
            private String investmentPercentage;
            private BigDecimal balance;
            @JsonProperty("bid_price")
            private BigDecimal bidPrice;
            private String value;

            @JsonDeserialize(using = LocalDateTimeDeserializer.class)
            @JsonSerialize(using = LocalDateTimeSerializer.class)
            @JsonProperty("value_date")
            private LocalDate valueDate;

            @JsonProperty("total_quantity")
            private String totalQuantity;

            @JsonProperty("available_quantity")
            private String availableQuantity;

            @JsonProperty("actual_price")
            private BigDecimal actualPrice;

            @JsonProperty("actual_value")
            private String actualValue;
        }
    }
}


