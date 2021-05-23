package com.stanum.skrudzh.jpa.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "ar_internal_metadata")
@EqualsAndHashCode
@Data
public class ArInternalMetadataEntity implements Serializable {
    @Id
    @Column(name = "key")
    private String key;

    @Basic
    @Column(name = "value")
    private String value;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

}
