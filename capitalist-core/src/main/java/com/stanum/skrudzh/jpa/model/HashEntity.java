package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.jpa.model.base.HasUser;
import com.stanum.skrudzh.model.enums.HashableTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "hashes")
@Data
@EqualsAndHashCode
public class HashEntity implements Base, HasUser, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "salt_edge_category")
    private String saltEdgeCategory;

    @Column(name = "prototype_key")
    private String prototypeKey;

    @Column(name = "hashable_id")
    private Long hashableId;

    @Column(name = "hashable_type")
    @Enumerated(value = EnumType.STRING)
    private HashableTypeEnum hashableType;

    @Column(name = "hashable_currency")
    private String hashableCurrency;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
