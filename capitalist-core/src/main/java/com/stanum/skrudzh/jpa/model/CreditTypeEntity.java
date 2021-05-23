package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.model.enums.PeriodSuperUnitEnum;
import com.stanum.skrudzh.model.enums.PeriodUnitEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Where(clause = "deleted_at is null")
@Table(name = "credit_types")
@EqualsAndHashCode
@Data
public class CreditTypeEntity implements Base, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "localized_key")
    private String localizedKey;

    @Enumerated
    @Column(name = "period_unit")
    private PeriodUnitEnum periodUnit;

    @Column(name = "min_value")
    private Integer minValue;

    @Column(name = "max_value")
    private Integer maxValue;

    @Column(name = "default_value")
    private Integer defaultValue;

    @Column(name = "has_monthly_payments")
    private Boolean hasMonthlyPayments;

    @Enumerated
    @Column(name = "period_super_unit")
    private PeriodSuperUnitEnum periodSuperUnit;

    @Column(name = "units_in_super_unit")
    private Integer unitsInSuperUnit;

    @Column(name = "row_order")
    private Integer rowOrder;

    @Basic
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "is_default")
    private Boolean isDefault;

}
