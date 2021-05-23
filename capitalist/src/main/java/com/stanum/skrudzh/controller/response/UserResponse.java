package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private User user;

}
