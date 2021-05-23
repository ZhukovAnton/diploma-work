package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.jpa.model.base.HasUser;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.OrderType;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "ordering")
@Data
public class OrderEntity implements Base, HasUser, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Basic
    @Enumerated(value = EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType;

    @Basic
    @Enumerated(value = EnumType.STRING)
    @Column(name = "entity_type")
    private EntityTypeEnum entityType;

    @Basic
    @Column(name = "entity_id")
    private Long entityId;

    @Basic
    @Column
    private Long orderPosition;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
