package com.stanum.skrudzh.migrations;

import com.stanum.skrudzh.converters.Encryptor;
import com.stanum.skrudzh.jpa.model.TransactionableExampleEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.model.system.Migration;
import com.stanum.skrudzh.jpa.repository.TransactionableExampleRepository;
import com.stanum.skrudzh.jpa.repository.system.MigrationsRepository;
import com.stanum.skrudzh.service.income_source.IncomeSourceManagementService;
import com.stanum.skrudzh.service.user.UserFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class Migrator {
    private static final int BATCH_LIMIT = 100;
    private static final String ENCRYPT_FLAG = "encrypt";
    private static final String SALES_INCOME_SOURCE_FLAG = "sales";

    private static final String UPDATE_USER = "UPDATE users SET email = ?, firstname = ?, lastname = ? where id = ?";
    private static final String GET_USER_INFO = "SELECT id, email,firstname, lastname from users";

    private static final String UPDATE_CARDS = "UPDATE accounts SET cards = ?  where id = ?";
    private static final String GET_CARDS = "SELECT id, cards from accounts";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserFinder userFinder;

    @Autowired
    private Encryptor encryptor;

    @Autowired
    private IncomeSourceManagementService incomeSourceManagementService;

    @Autowired
    private TransactionableExampleRepository transactionableExampleRepository;

    @Autowired
    private MigrationsRepository migrationsRepository;

    @Value("${threebaskets.migration.encrypt}")
    private boolean initEncryption;

    @Value("${threebaskets.migration.sales-income}")
    private boolean initSalesIncome;

    @PostConstruct
    public void migrate() throws Exception {
        boolean result = encryptData();
        if(result) {
            addSalesIncomeSource();
        }
    }

    private boolean encryptData() throws Exception {
        log.info("Start migration");
        if(!initEncryption) {
            log.info("Init encryption disabled");
            return true;
        }
        if(getFlag(ENCRYPT_FLAG)) {
            log.info("Migration {} have already done", ENCRYPT_FLAG);
            return true;
        }
        boolean success = false;
        log.info("Encrypt data");
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try {
            updateUserData(connection);
            updateCards(connection);

            log.info("Update encrypt flag");
            connection.commit();
            log.info("Migration finished");
            success = true;
        } catch (Exception e) {
            log.error("Rollback migration");
            log.error("Exception while updating data", e);
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }
        if(success) {
           updateFlag(ENCRYPT_FLAG);
        }
        log.info("Migration result = {}", success);
        return success;
    }

    private boolean getFlag(String name) {
        Optional<Migration> migration = migrationsRepository.findByName(name);
        return migration.isPresent() && migration.get().getVal();
    }

    private void updateFlag(String name) {
        log.info("Update flag {}", name);

        Migration result = new Migration();
        result.setName(name);
        result.setVal(true);

        migrationsRepository.save(result);
    }

    @Transactional
    public void addSalesIncomeSource() {
        log.info("Start adding Sales Income source");
        if(!initSalesIncome) {
            log.info("Init sales income disabled");
            return;
        }
        if(getFlag(SALES_INCOME_SOURCE_FLAG)) {
            log.info("Migration {} have already done", SALES_INCOME_SOURCE_FLAG);
            return;
        }

        boolean success = false;
        List<Long> errorUserIds = new ArrayList<>();
        try {
            List<UserEntity> users = userFinder.findAll();
            Optional<TransactionableExampleEntity> sales = transactionableExampleRepository.findByName("Sales");
            if (sales.isEmpty()) {
                log.error("Sales income source not found");
                return;
            }
            for (UserEntity userEntity : users) {
                if (userEntity.getOnBoarded()) {
                    try {
                        incomeSourceManagementService.createDefaultIncomeSource(userEntity, sales.get());
                    } catch (Throwable e) {
                        errorUserIds.add(userEntity.getId());
                        log.error("Can't create sales income source for userId={}", userEntity.getId(), e);
                    }
                }
            }
            success = true;
        } catch (Throwable th) {
            log.error("Exception while adding default income source", th);
        }

        if(!errorUserIds.isEmpty()) {
            log.warn("Can't create Income Source for users: {}", errorUserIds);
        }

        if(success) {
            updateFlag(SALES_INCOME_SOURCE_FLAG);
        }
    }

    private void updateUserData(Connection connection) throws SQLException {
        log.info("Updating user data");
        PreparedStatement psUpdate = connection.prepareStatement(UPDATE_USER);

        PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_INFO);
        ResultSet rs = preparedStatement.executeQuery();

        int batchCounter = 0;
        while(rs.next()) {
            String firstName = rs.getString("firstname");
            String lastName = rs.getString("lastname");
            String email = rs.getString("email");
            long id = rs.getLong("id");

            encryptValue(psUpdate, email.toLowerCase(), 1);
            encryptValue(psUpdate, firstName, 2);
            encryptValue(psUpdate, lastName, 3);
            psUpdate.setLong(4, id);

            psUpdate.addBatch();
            batchCounter++;

            if(batchCounter >= BATCH_LIMIT) {
                log.info("Execute batch, size={}", batchCounter);
                psUpdate.executeBatch();
                batchCounter = 0;
            }
        }

        if(batchCounter > 0) {
            log.info("Execute batch, size = {}", batchCounter);
            psUpdate.executeBatch();
        }
    }

    private void updateCards(Connection connection) throws SQLException {
        log.info("Updating cards");
        PreparedStatement psUpdate = connection.prepareStatement(UPDATE_CARDS);

        PreparedStatement preparedStatement = connection.prepareStatement(GET_CARDS);
        ResultSet rs = preparedStatement.executeQuery();

        int batchCounter = 0;
        while(rs.next()) {
            String cards = rs.getString("cards");
            long id = rs.getLong("id");

            encryptValue(psUpdate, cards, 1);
            psUpdate.setLong(2, id);

            psUpdate.addBatch();
            batchCounter++;

            if(batchCounter >= BATCH_LIMIT) {
                log.info("Execute batch");
                psUpdate.executeBatch();
                batchCounter = 0;
            }
        }

        if(batchCounter > 0) {
            log.info("Execute batch, size = {}", batchCounter);
            psUpdate.executeBatch();
        }
    }

    private void encryptValue(PreparedStatement ps, String value, Integer index) throws SQLException {
        if(value != null) {
            String encryptedValue = encryptor.convertToDatabaseColumn(value);
            ps.setString(index, encryptedValue);
        } else {
            ps.setNull(index, Types.VARCHAR);
        }
    }
}