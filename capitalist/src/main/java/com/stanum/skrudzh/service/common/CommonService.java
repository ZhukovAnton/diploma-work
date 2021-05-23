package com.stanum.skrudzh.service.common;

import com.stanum.skrudzh.jpa.model.UserEntity;

public interface CommonService {
    void destroyUsersData(UserEntity userEntity);
}
