package com.stanum.skrudzh.saltage.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@ToString
public class Transaction {
    private String id;
    private boolean duplicated;
    private String mode;
    private String status;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonProperty("made_on")
    private LocalDate madeOn;//": "2013-05-03",
    private BigDecimal amount;//": -200.0,
    @JsonProperty("currency_code")
    private String currencyCode;//": "USD",
    private String description;//": "test transaction",
    private String category;//": "advertising",
    private Extra extra;
    @JsonProperty("account_id")
    private String accountId;//": "100",

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;//2020-03-11T15:59:31Z",

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;//2020-03-12T15:59:31Z"

    @Data
    @ToString
    public static class Extra {
        @JsonProperty("original_amount")
        private BigDecimal originalAmount;//": -3974.60,
        @JsonProperty("account_balance_snapshot")
        private BigDecimal accountBalanceSnapshot;//": -3974.60,
        @JsonProperty("original_currency_code")
        private String originalCurrencyCode;//": "CZK",
        @JsonProperty("categorization_confidence")
        private BigDecimal categorizationConfidence;//": "0-1",
        private String payee;//": "CZK",
        @JsonProperty("merchant_id")
        private String merchantId;//": "CZK",

        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonProperty("posting_date")
        private LocalDate postingDate;//": "2013-05-07",

        @JsonDeserialize(using = LocalTimeDeserializer.class)
        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonProperty("posting_time")
        private LocalTime postingTime;

        @JsonDeserialize(using = LocalTimeDeserializer.class)
        @JsonSerialize(using = LocalTimeSerializer.class)
        private LocalTime time;//23:56:12"
    }
}
