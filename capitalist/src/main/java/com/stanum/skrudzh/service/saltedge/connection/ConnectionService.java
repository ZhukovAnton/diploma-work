package com.stanum.skrudzh.service.saltedge.connection;

import com.stanum.skrudzh.controller.form.ConnectionCreationForm;
import com.stanum.skrudzh.controller.form.ConnectionRefreshForm;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;

public interface ConnectionService {

    ConnectionEntity createConnectionEntity(UserEntity userEntity, ConnectionCreationForm.ConnectionCF form);

    void destroyConnection(ConnectionEntity connectionEntity, boolean withTransactions);

    void refreshConnection(ConnectionEntity connectionEntity, ConnectionRefreshForm.ConnectionRF form);

    void refreshConnectionByStatus(ConnectionEntity connectionEntity);

    void refreshAccounts(ConnectionEntity connectionEntity);

    void interactive(ConnectionEntity connectionEntity, ConnectionRefreshForm.ConnectionRF form);

    void save(ConnectionEntity connectionEntity);
}
