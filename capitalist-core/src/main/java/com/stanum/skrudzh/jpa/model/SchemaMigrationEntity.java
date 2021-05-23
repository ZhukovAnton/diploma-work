package com.stanum.skrudzh.jpa.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "schema_migrations")
@EqualsAndHashCode
@Data
public class SchemaMigrationEntity implements Serializable {

    @Id
    @Column(name = "version")
    private String version;

}
