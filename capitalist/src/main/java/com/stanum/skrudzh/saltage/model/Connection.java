package com.stanum.skrudzh.saltage.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class Connection {
    private String id;//": "1227",
    private String country_code;//": "XF",

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;//": "2020-03-12T15:59:31Z",

    @JsonProperty("customer_id")
    private String customerId;//": "905",

    @JsonProperty("daily_refresh")
    private String dailyRefresh;//": false,

    private String secret;//";: "AtQX6Q8vRyMrPjUVtW7J_O1n06qYQ25bvUJ8CIC80-8",

    @JsonProperty("show_consent_confirmation")
    private boolean showConsentConfirmation;//": false,

    @JsonProperty("last_consent_id")
    private String lastConsentId;//": "102492",

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("last_success_at")
    private LocalDateTime lastSuccessAt;//": "2020-03-13T15:19:31Z",

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("next_refresh_possible_at")
    private LocalDateTime nextRefreshPossibleAt;//": "2020-03-13T16:59:31Z",

    @JsonProperty("provider_id")
    private String providerId;//": "1234",

    @JsonProperty("provider_code")
    private String providerCode;//": "fakebank_simple_xf",

    @JsonProperty("provider_name")
    private String providerName;//": "Fakebank Simple",

    private String status;//": "active",

    @JsonProperty("store_credentials")
    private boolean storeCredentials;// true,

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;//": "2020-03-13T15:19:31Z"

    private List<Account> accounts;

    @JsonProperty("last_attempt")
    private Attempt lastAttempt;

    @Data
    public static class Attempt {

        @JsonProperty("daily_refresh")
        private Boolean dailyRefresh;

        private Boolean interactive;

        @JsonProperty("last_stage")
        private Stage lastStage;

        @Data
        public static class Stage {
            private String name;
        }

    }
}
