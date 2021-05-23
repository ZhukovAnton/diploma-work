package com.stanum.skrudzh.utils;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

public class TimeUtilTest {

    @Test
    public void beginningOfPreviousMonth() {
        Timestamp timestamp = TimeUtil.beginningOfPreviousMonth();
        System.out.println(timestamp);
    }
}
