package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.jpa.model.base.HasUser;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "baskets")
@EqualsAndHashCode
@Data
public class BasketEntity implements Base, HasUser, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated
    @Column(name = "basket_type")
    private BasketTypeEnum basketType;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
