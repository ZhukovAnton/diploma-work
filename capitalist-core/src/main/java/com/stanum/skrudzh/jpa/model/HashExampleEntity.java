package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.model.enums.HashableTypeEnum;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "hash_examples")
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HashExampleEntity implements Base, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "salt_edge_categories")
    private String saltEdgeCategories;

    @Column(name = "prototype_key")
    private String prototypeKey;

    @Column(name = "country")
    private String country;

    @Column(name = "hashable_type")
    @Enumerated(value = EnumType.STRING)
    private HashableTypeEnum hashableType;

}
