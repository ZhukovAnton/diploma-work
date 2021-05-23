package com.stanum.skrudzh.metrics;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

@Service
@Slf4j
public class MetricsService {
    private String SAVE_METRIC;
    private HikariDataSource ds;
    private boolean initialized = false;

    @Value("${threebaskets.metrics.enabled}")
    private Boolean metricsEnabled;

    @Value("${threebaskets.metrics.url}")
    private String url;

    @Value("${threebaskets.metrics.username}")
    private String username;

    @Value("${threebaskets.metrics.password}")
    private String password;

    @Value("${threebaskets.metrics.table}")
    private String table;

    @PostConstruct
    public void init() {
        try {
            SAVE_METRIC = String.format("INSERT INTO %S (name, value, date) values(?,?,?)", table);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.setMaximumPoolSize(30);
            ds = new HikariDataSource(config);
            initialized = true;
        } catch (Exception e) {
            log.error("Error while init metric datasource", e);
        }
    }

    @Async
    public void saveMetric(MetricType type, long start) {
        Metric metric = new Metric(type, start);
        if(!metricsEnabled) {
            log.info("Metrics disabled");
            return;
        }
        if(!initialized) {
            log.warn("Metrics datasource is not initialized");
            return;
        }

        try (Connection connection = ds.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(SAVE_METRIC);
            ps.setString(1, metric.getMetricType().name());
            ps.setLong(2, metric.getValue());
            ps.setTimestamp(3, new Timestamp(metric.getDate().getTime()));

            ps.executeUpdate();
        } catch (Exception e) {
            log.error("Error while save metrics", e);
        }
    }

    @Async
    public void error(MetricType type) {
        if(!metricsEnabled) {
            log.info("Metrics disabled");
            return;
        }
        if(!initialized) {
            log.warn("Metrics datasource is not initialized");
            return;
        }
        try {
            Connection connection = ds.getConnection();
            PreparedStatement ps = connection.prepareStatement(SAVE_METRIC);
            ps.setString(1, type.name());
            ps.setLong(2, 0L);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            ps.executeUpdate();
        } catch (Exception e) {
            log.error("Error while save metrics", e);
        }
    }

}
