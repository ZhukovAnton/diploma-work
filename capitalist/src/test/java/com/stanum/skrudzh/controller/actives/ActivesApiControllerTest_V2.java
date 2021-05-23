package com.stanum.skrudzh.controller.actives;

import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.form.ActiveCreationForm;
import com.stanum.skrudzh.controller.response.ActiveResponse;
import com.stanum.skrudzh.model.dto.Actives;
import com.stanum.skrudzh.utils.RequestUtil;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
public class ActivesApiControllerTest_V2 extends AbstractActivesApiControllerTest {

    @Override
    protected ActiveResponse createActive(ActiveCreationForm form) {
        return activesApiController.createActiveForUser(user.getId(), "", form).getBody();
    }

    @Override
    protected void setIosBuild() {
        RequestUtil.setIosBuild("v2");
    }

    @Test
    public void shouldGetActiveCreatedByUser() {
        String name = RandomString.make();
        ActiveCreationForm form = TestUtils.createActiveForm(name);
        ActiveResponse response = createActive(form);

        Actives actives = activesApiController.getActivesByUser(user.getId(), "").getBody();
        Assert.assertNotNull(actives);
        Assert.assertEquals(1, actives.getActives().size());
    }
}
