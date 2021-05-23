package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Connectable;
import com.stanum.skrudzh.jpa.model.base.HasUser;
import com.stanum.skrudzh.jpa.model.base.Transactionable;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.PlannedIncomeTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "actives")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EqualsAndHashCode(callSuper = true)
@Data
public class ActiveEntity extends Rankable implements Connectable, HasUser, Transactionable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "currency")
    private String currency;

    @Basic
    @Column(name = "cost_cents")
    private BigDecimal costCents;

    @Column(name = "annual_income_percent")
    private Integer annualIncomePercent;

    @Basic
    @Column(name = "monthly_payment_cents")
    private BigDecimal monthlyPaymentCents;

    @Basic
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "icon_url")
    private String iconUrl;

    @Basic
    @Column(name = "monthly_planned_income_cents")
    private BigDecimal monthlyPlannedIncomeCents;

    @Column(name = "planned_income_type")
    @Enumerated
    private PlannedIncomeTypeEnum plannedIncomeType;

    @Column(name = "is_income_planned")
    private Boolean isIncomePlanned;

    @Basic
    @Column(name = "goal_amount_cents")
    private BigDecimal goalAmountCents;

    @Basic
    @Column(name = "already_paid_cents")
    private BigDecimal alreadyPaidCents;

    @Column(name = "max_fetch_interval")
    private Integer maxFetchInterval;

    @ManyToOne
    @JoinColumn(name = "basket_id")
    private BasketEntity basketEntity;

    @ManyToOne
    @JoinColumn(name = "active_type_id")
    private ActiveTypeEntity activeTypeEntity;

    @OneToOne
    @JoinColumn(name = "account_connection_id")
    private AccountConnectionEntity accountConnectionEntity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Transient
    private EntityTypeEnum entityType = EntityTypeEnum.Active;

    public BigDecimal getBalance() { return getCostCents(); }

    public void setBalance(BigDecimal newBalance) { setCostCents(newBalance);}
}
