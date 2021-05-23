package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "account_connections")
@Data
@EqualsAndHashCode
@ToString
public class AccountConnectionEntity implements Base, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "source_type")
    @Enumerated(value = EnumType.STRING)
    private EntityTypeEnum sourceType;

    @OneToOne
    @JoinColumn(name = "account_id")
    private AccountEntity accountEntity;

    @ManyToOne
    @JoinColumn(name = "connection_id")
    private ConnectionEntity connectionEntity;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt = TimeUtil.now();

}
