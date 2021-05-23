package com.stanum.skrudzh.service.saltedge.learn.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LearnData {

    @JsonProperty("customer_id")
    private String customerId;

    private List<LearnSaltTr> transactions;
}
