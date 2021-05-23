package com.stanum.skrudzh.jpa.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "providers_meta")
@Data
public class ProviderMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Basic
    @Column(name = "prototype_key")
    private String prototypeKey;

    @Basic
    @Column(name = "provider_codes")
    private String providerCodes;

    @Basic
    @Column(name = "disabled")
    private Boolean disabled;
}
