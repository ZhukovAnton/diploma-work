package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.IncomeSource;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IncomeSourceResponse {
    private IncomeSource incomeSource;
}
