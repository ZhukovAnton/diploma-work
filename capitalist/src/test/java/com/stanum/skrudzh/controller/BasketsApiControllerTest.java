package com.stanum.skrudzh.controller;

import com.stanum.skrudzh.IntegrationTest;
import com.stanum.skrudzh.controller.response.BasketResponse;
import com.stanum.skrudzh.model.dto.Basket;
import com.stanum.skrudzh.model.dto.Baskets;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class BasketsApiControllerTest extends IntegrationTest {

    @Autowired
    private BasketsApiController basketsApiController;

    @Test
    public void shouldReturnBasketsForUser() {
        ResponseEntity<Baskets> response = basketsApiController.getBasketsForUser(user.getId(), "");
        Baskets baskets = response.getBody();

        Assert.assertNotNull(baskets);
        Assert.assertEquals(3, baskets.getBaskets().size());
    }

    @Test
    public void shouldReturnBasketsById() {
        ResponseEntity<Baskets> response = basketsApiController.getBasketsForUser(user.getId(), "");
        Basket basket = response.getBody().getBaskets().get(0);

        BasketResponse basketResponse = basketsApiController.basketsIdGet(basket.getId(), "").getBody();
        Assert.assertNotNull(basketResponse);
        Assert.assertEquals(basket, basketResponse.getBasket());
    }
}
