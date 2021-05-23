package com.stanum.skrudzh.controller.saltedge;

import com.stanum.skrudzh.controller.AccountConnectionController;
import com.stanum.skrudzh.model.dto.AccountConnection;
import com.stanum.skrudzh.model.dto.AccountConnections;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AccountConnectionControllerTest extends AbstractSaltedgeTest {

    @Autowired
    private AccountConnectionController accountConnectionController;

    @Test
    @Disabled
    public void shouldFindAccountConnectionsByAccountId() {
        AccountConnections accountConnections = accountConnectionController
                .indexAccountConnections("", accountEntity.getId()).getBody();
        Assert.assertNotNull(accountConnections);
        Assert.assertEquals(1, accountConnections.getAccountConnections().size());
        Assert.assertEquals(ACCOUNT_ID, accountConnections.getAccountConnections().get(0).getAccount().getAccountId());
    }

    @Test
    @Disabled
    public void shouldFindAccountConnectionsByAccountConnectionId() {
        AccountConnection accountConnection = accountConnectionController.getAccountConnectionById(
                "",
                expenseSource.getSaltEdgeAccountConnection().getId()
        ).getBody().getAccountConnection();
    }
}
