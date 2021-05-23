package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.ConnectionDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ConnectionsResponse {
    private List<ConnectionDto> connections;
}
