package com.stanum.skrudzh.metrics;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class Metric {
    private MetricType metricType;
    private Long value;
    private Date date;

    public Metric(MetricType metricType, Long startTime, Date date) {
        this.metricType = metricType;
        this.value = System.currentTimeMillis() - startTime;
        this.date = date;
    }

    public Metric(MetricType metricType, Long startTime) {
        this.metricType = metricType;
        this.value = System.currentTimeMillis() - startTime;
        this.date = new Date();
    }
}
