package com.stanum.skrudzh.controller.form.saltedge.callback;

import lombok.Data;

import java.sql.Timestamp;

/**
 * Example:  "meta": {
 * "version": "5",
 * "time": "2020-09-22T10:58:44Z"
 * }
 */

@Data
public class Meta {
    private String version;
    private Timestamp time;
}
