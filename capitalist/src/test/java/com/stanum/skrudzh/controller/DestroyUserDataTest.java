package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DestroyUserDataTest extends IntegrationTest {

    @Autowired
    private UsersApiController usersApiController;

    @Test
    public void shouldDestroyUsersData() throws Exception {
        usersApiController.destroy(userEntity.getId(), "");
    }
}
