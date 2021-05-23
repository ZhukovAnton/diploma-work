package com.stanum.skrudzh.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class AccountConnections {
    private List<AccountConnection> accountConnections;
}
