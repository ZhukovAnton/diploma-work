package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.model.enums.IconCategoryEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "icons")
@EqualsAndHashCode
@Data
public class IconEntity implements Base, Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "category")
    @Enumerated
    private IconCategoryEnum category;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}
