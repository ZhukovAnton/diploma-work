package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.model.enums.PlannedIncomeTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Where(clause = "deleted_at is null")
@Table(name = "active_types")
@EqualsAndHashCode
@Data
public class ActiveTypeEntity implements Base, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "localized_key")
    private String localizedKey;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "row_order")
    private Integer rowOrder;

    @Basic
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "default_planned_income_type")
    @Enumerated
    private PlannedIncomeTypeEnum defaultPlannedIncomeType;

    @Column(name = "is_goal_amount_required")
    private Boolean isGoalAmountRequired;

    @Column(name = "is_income_planned_default")
    private Boolean isIncomePlannedDefault;

    @Column(name = "only_buying_assets")
    private Boolean onlyBuyingAssets;

    @Column(name = "cost_title_localized_key")
    private String costTitleLocalizedKey;

    @Column(name = "monthly_payment_localized_key")
    private String monthlyPaymentLocalizedKey;

    @Column(name = "buying_assets_default")
    private Boolean buyingAssetsDefault;

    @Column(name = "buying_assets_title_localized_key")
    private String buyingAssetsTitleLocalizedKey;

}
