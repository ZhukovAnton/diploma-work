package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.jpa.model.base.HasNameAndIcon;
import com.stanum.skrudzh.jpa.model.base.HasUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "credits")
@EqualsAndHashCode
@Data
public class CreditEntity implements Base, HasNameAndIcon, HasUser,  Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "icon_url")
    private String iconUrl;

    @Basic
    @Column(name = "currency")
    private String currency;

    @Column(name = "return_amount_cents")
    private BigDecimal returnAmountCents;

    @Column(name = "already_paid_cents")
    private BigDecimal alreadyPaidCents;

    @Column(name = "monthly_payment_cents")
    private BigDecimal monthlyPaymentCents;

    @Basic
    @Column(name = "got_at")
    private Timestamp gotAt;

    @Basic
    @Column(name = "period")
    private Integer period;

    @Basic
    @Column(name = "is_paid")
    private Boolean isPaid;

    @Basic
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "amount_cents")
    private BigDecimal amountCents;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "credit_type_id")
    private CreditTypeEntity creditTypeEntity;
}
