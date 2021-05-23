package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.*;
import com.stanum.skrudzh.model.enums.CreationTypeEnum;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.HashableTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "income_sources")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EqualsAndHashCode(callSuper = true)
@Data
public class IncomeSourceEntity
        extends Rankable
        implements Hashable, HasUser, Transactionable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "currency")
    private String currency;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Basic
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "monthly_planned_cents")
    private BigDecimal monthlyPlannedCents;

    @Column(name = "is_borrow_or_return")
    private Boolean isBorrow;

    @Column(name = "is_virtual")
    private Boolean isVirtual;

    @Column(name = "creation_type")
    @Enumerated
    private CreationTypeEnum creationType = CreationTypeEnum.by_default;

    @Column(name = "description")
    private String description;

    @Column(name = "prototype_key")
    private String prototypeKey;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "active_id")
    private ActiveEntity active;

    @Transient
    private EntityTypeEnum entityType = EntityTypeEnum.IncomeSource;

    @Override
    public HashableTypeEnum getHashableType() {
        return HashableTypeEnum.IncomeSource;
    }
}
