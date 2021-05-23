package com.stanum.skrudzh.admin;

import com.stanum.skrudzh.admin.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest
@EntityScan("com.stanum.skrudzh")
class AdminApplicationTests {

    @Autowired
    private TestService testService;

    @Test
    void contextLoads() {
    }

}
