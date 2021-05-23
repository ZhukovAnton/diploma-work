package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.Session;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionResponse {
    private Session session;
}
