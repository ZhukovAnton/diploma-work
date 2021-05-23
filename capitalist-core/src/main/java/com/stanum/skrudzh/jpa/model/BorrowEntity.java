package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.jpa.model.base.HasNameAndIcon;
import com.stanum.skrudzh.jpa.model.base.HasUser;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "borrows")
@EqualsAndHashCode
@Data
public class BorrowEntity implements Base, HasNameAndIcon, HasUser, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private BorrowTypeEnum type;

    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "borrowed_at")
    private Timestamp borrowedAt;

    @Basic
    @Column(name = "payday")
    private Timestamp payday;

    @Column(name = "comment")
    private String comment;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "amount_cents")
    private BigDecimal amountCents;

    @Column(name = "amount_currency")
    private String amountCurrency;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Basic
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "is_returned")
    private Boolean isReturned;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
