package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.ConnectionDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionResponse {
    private ConnectionDto connection;
}
