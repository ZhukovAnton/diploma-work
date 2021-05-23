package com.stanum.skrudzh.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ExchangeRates {

    private List<ExchangeRate> exchangeRates;

}
