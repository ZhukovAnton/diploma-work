package com.stanum.skrudzh.controller.saltedge;

import com.stanum.skrudzh.TestUtils;
import com.stanum.skrudzh.controller.ConnectionApiController;
import com.stanum.skrudzh.controller.form.ConnectionRefreshForm;
import com.stanum.skrudzh.controller.response.ConnectionResponse;
import com.stanum.skrudzh.controller.saltedge.AbstractSaltedgeTest;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.repository.ConnectionsRepository;
import com.stanum.skrudzh.model.enums.ConnectionStatusEnum;
import com.stanum.skrudzh.service.saltedge.account.AccountFinder;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
public class ConnectionApiControllerTest extends AbstractSaltedgeTest {
    private static final String TEST_CONNECTION_ID = "302971264186714816";
    private static final String TEST_CONNECTION_ID2 = "303612330166454668";
    private static final String TEST_CONNECTION_ID3 = "303323957791033999";

    private static final String TEST_CONNECTION_UPDATE1 = "304137505563740552";
    private static final String TEST_CONNECTION_UPDATE2 = "304138030027901750";

    @Autowired
    private ConnectionApiController connectionApiController;

    @Autowired
    private ConnectionsRepository connectionsRepository;

    @Autowired
    private AccountFinder accountFinder;

    @Test
    public void shouldCreateConnectionEntity() {
        ConnectionResponse response = connectionApiController.createConnectionEntity(user.getId(), "", TestUtils.connectionCreateForm(TEST_CONNECTION_ID)).getBody();
        ConnectionEntity connectionEntity = connectionsRepository.findById(response.getConnection().getId()).get();
        Assert.assertNotNull(connectionEntity);
    }

    @Test
    public void shouldReturnConnectionById() {
        ConnectionResponse response = connectionApiController.createConnectionEntity(user.getId(), "", TestUtils.connectionCreateForm(TEST_CONNECTION_ID2)).getBody();
        ConnectionResponse result = connectionApiController.getConnectionById(response.getConnection().getId(), "").getBody();
        Assert.assertNotNull(result.getConnection());
    }

    @Test
    public void shouldRefreshConnection() {
        ConnectionResponse response = connectionApiController.createConnectionEntity(user.getId(), "", TestUtils.connectionCreateForm(TEST_CONNECTION_ID3)).getBody();

        ConnectionRefreshForm form = new ConnectionRefreshForm();
        ConnectionRefreshForm.ConnectionRF rf = new ConnectionRefreshForm.ConnectionRF();
        rf.setSaltEdgeConnectionId(TEST_CONNECTION_ID3);
        form.setConnection(rf);
        connectionApiController.refreshConnection("", response.getConnection().getId(), form);
    }

    @Test
    public void shouldSetDeletedStatus_ifConnectionDoesNotExist() {
        ConnectionResponse response = connectionApiController.createConnectionEntity(user.getId(), "", TestUtils.connectionCreateForm(TEST_CONNECTION_ID3)).getBody();

        ConnectionRefreshForm form = new ConnectionRefreshForm();
        ConnectionRefreshForm.ConnectionRF rf = new ConnectionRefreshForm.ConnectionRF();
        rf.setSaltEdgeConnectionId("NotExistingConnection");
        form.setConnection(rf);
        connectionApiController.refreshConnection("", response.getConnection().getId(), form);

        ConnectionEntity connectionEntity = connectionsRepository.findById(response.getConnection().getId()).get();
        Assert.assertEquals(ConnectionStatusEnum.deleted, connectionEntity.getStatus());
    }

    @Test
    @Disabled
    public void shouldUpdateAccounts_ifExist() {
        String oldAccountId = "304137573276584544";
        String newAccountId = "304138130330486918";

        ConnectionResponse response = connectionApiController.createConnectionEntity(user.getId(), "",
        TestUtils.connectionCreateForm(TEST_CONNECTION_UPDATE1)).getBody();

        ConnectionEntity connectionEntity = connectionsRepository.findById(response.getConnection().getId()).get();
        Set<AccountEntity> accountsByConnection = accountFinder.findAccountsByConnection(connectionEntity);
        Assert.assertEquals(5, accountsByConnection.size());

        boolean containsOldAccount = false;
        boolean containsNewAccount = false;
        for(AccountEntity acc : accountsByConnection) {
            if(acc.getAccountId().equals(oldAccountId)) {
                containsOldAccount = true;
            }
            if(acc.getAccountId().equals(newAccountId)) {
                containsNewAccount = true;
            }
        }

        Assert.assertTrue(containsOldAccount);
        Assert.assertFalse(containsNewAccount);

        ConnectionRefreshForm form = new ConnectionRefreshForm();
        ConnectionRefreshForm.ConnectionRF rf = new ConnectionRefreshForm.ConnectionRF();
        rf.setSaltEdgeConnectionId(TEST_CONNECTION_UPDATE2);
        form.setConnection(rf);
        connectionApiController.refreshConnection("", response.getConnection().getId(), form);

        ConnectionEntity connectionEntity2 = connectionsRepository.findById(response.getConnection().getId()).get();
        Set<AccountEntity> accountsByConnection2 = accountFinder.findAccountsByConnection(connectionEntity2);
        Assert.assertEquals(5, accountsByConnection2.size());

        containsNewAccount = false;
        containsOldAccount = false;
        for(AccountEntity acc : accountsByConnection2) {
            if(acc.getAccountId().equals(oldAccountId)) {
                containsOldAccount = true;
            }
            if(acc.getAccountId().equals(newAccountId)) {
                containsNewAccount = true;
            }
        }
        Assert.assertFalse(containsOldAccount);
        Assert.assertTrue(containsNewAccount);

    }
}
