package com.stanum.skrudzh.controller.saltedge;

import com.stanum.skrudzh.controller.AccountController;
import com.stanum.skrudzh.controller.saltedge.AbstractSaltedgeTest;
import com.stanum.skrudzh.model.dto.Accounts;
import com.stanum.skrudzh.model.enums.NatureTypeEnum;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AccountControllerTest  extends AbstractSaltedgeTest {
    private static final String TEST_CONNECTION_ID = "303325766685297152";
    private static final String PROVIDER_ID = "68";

    @Autowired
    private AccountController accountController;

    @Test
    public void shouldFindAccountsByConnectionId() {
        Accounts accounts = accountController.indexAccounts("",
                user.getId(),
                TEST_CONNECTION_ID,
                null,
                null,
                null,
                NatureTypeEnum.account).getBody();

        Assert.assertEquals(5, accounts.getAccounts().size());
    }

    @Test
    public void shouldFindAccountsByProviderId() {
        Accounts accounts = accountController.indexAccounts("",
                user.getId(),
                null,
                null,
                PROVIDER_ID,
                null,
                NatureTypeEnum.account).getBody();

        Assert.assertEquals(5, accounts.getAccounts().size());
    }

    @Test
    public void shouldFindAccountsByParams() {
        Accounts accounts = accountController.indexAccounts("",
                user.getId(),
                null,
                null,
                null,
                null,
                NatureTypeEnum.account).getBody();

        Assert.assertEquals(5, accounts.getAccounts().size());
    }
}
