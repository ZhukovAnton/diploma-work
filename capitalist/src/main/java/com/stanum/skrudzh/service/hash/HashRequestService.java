package com.stanum.skrudzh.service.hash;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashRequestService {

    @Value("${threebaskets.admin-id}")
    private Integer adminId;

    private final HashManagementService hashManagementService;

    public void createHashForUsers() {
        if (!RequestUtil.getUser().getId().equals(adminId)) throw new AppException(HttpAppError.ACCESS_DENIED);
        hashManagementService.createHashesForUsers();
    }
}
