package com.stanum.skrudzh.saltedge;

import com.stanum.skrudzh.saltage.SaltedgeAPI;
import com.stanum.skrudzh.saltage.model.Response;
import com.stanum.skrudzh.service.saltedge.learn.data.LearnResponse;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SaltedgeApiTest {

    @Autowired
    private SaltedgeAPI saltedgeAPI;

    @Test
    public void testLearn() throws Exception {
        Response<LearnResponse> learn = saltedgeAPI.learn.learn(
                "302895468231264277",
                "337130402961426453",
                "education");
        Assert.assertTrue(learn.getData().get(0).getLearned());
    }
}
