package com.stanum.skrudzh.service.saltedge.learn.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LearnSaltTr {
    private String id;

    @JsonProperty("category_code")
    private String categoryCode;
    private boolean immediate;

    public LearnSaltTr(String id, String categoryCode, boolean immediate) {
        this.id = id;
        this.categoryCode = categoryCode;
        this.immediate = immediate;
    }
}
