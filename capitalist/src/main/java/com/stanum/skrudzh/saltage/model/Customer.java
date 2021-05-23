package com.stanum.skrudzh.saltage.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Customer {
    @JsonProperty("id")
    private String customerId;
    private String identifier;
    private String secret;
}
