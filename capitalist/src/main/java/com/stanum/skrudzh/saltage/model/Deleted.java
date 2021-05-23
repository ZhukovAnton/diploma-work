package com.stanum.skrudzh.saltage.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Deleted {
    private boolean deleted;
    private String id;
}
