package com.stanum.skrudzh.saltage.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Locked {
    private boolean locked;
    private String id;
}
