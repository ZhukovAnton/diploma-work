package com.stanum.skrudzh.controller.actives;

import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.form.ActiveCreationForm;
import com.stanum.skrudzh.controller.response.ActiveResponse;
import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.model.dto.Actives;
import com.stanum.skrudzh.model.dto.Basket;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ActivesApiControllerTest_V1 extends AbstractActivesApiControllerTest {

    @Override
    protected ActiveResponse createActive(ActiveCreationForm form) {
        return activesApiController.basketsBasketIdActivesPost(getBasket().getId(), "", form).getBody();
    }

    @Override
    protected void setIosBuild() {
        //Do nothing
    }

    @Test
    public void shouldGetActiveByBasket() {
        String name = RandomString.make();
        ActiveCreationForm form = TestUtils.createActiveForm(name);
        Basket basket = getUserBaskets().get(0);
        ActiveResponse response = createActive(form);

        ActiveEntity activeEntity = activeRepository.findById(response.getActive().getId()).get();
        Actives actives = activesApiController.getActivesByBasket(basket.getId(), "").getBody();
        Assert.assertNotNull(actives);
        Assert.assertEquals(1, actives.getActives().size());
    }
}
