package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.*;
import com.stanum.skrudzh.model.enums.CardTypeEnum;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "expense_sources")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EqualsAndHashCode(callSuper = true)
@Data
public class ExpenseSourceEntity extends Rankable implements Base, Connectable, Transactionable, HasUser, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "icon_url")
    private String iconUrl;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "amount_cents")
    private BigDecimal amountCents;

    @Column(name = "currency")
    private String currency;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "is_debt")
    private Boolean isDebt;

    @Column(name = "credit_limit_cents")
    private BigDecimal creditLimitCents;

    @Column(name = "prototype_key")
    private String prototypeKey;

    @Column(name = "is_virtual")
    private Boolean isVirtual;

    @Column(name = "max_fetch_interval")
    private Integer maxFetchInterval;

    @Column(name = "card_type")
    @Enumerated(EnumType.ORDINAL)
    private CardTypeEnum cardType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "account_connection_id")
    private AccountConnectionEntity accountConnectionEntity;

    @Transient
    private EntityTypeEnum entityType = EntityTypeEnum.ExpenseSource;

    public BigDecimal getBalance() { return getAmountCents(); }

    public void setBalance(BigDecimal newBalance) { setAmountCents(newBalance); }
}
