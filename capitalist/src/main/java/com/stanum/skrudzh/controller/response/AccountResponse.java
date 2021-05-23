package com.stanum.skrudzh.controller.response;

import com.stanum.skrudzh.model.dto.Account;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountResponse {
    private Account account;
}
