package com.stanum.skrudzh.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Limits {
    @Value("${threebaskets.limits.transactions}")
    private Integer transactionLimit;

    @Value("${threebaskets.limits.assets}")
    private Integer assetsLimit;

    @Value("${threebaskets.limits.available-pending-days}")
    private Integer availablePendingDays;
}
