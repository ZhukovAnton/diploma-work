package com.stanum.skrudzh.utils.provider;

import lombok.Data;

@Data
public class Provider {
    private String id;
    private String code;
    private String name;
    private String logo_url;
    private String country_code;
    private String home_url;
}
