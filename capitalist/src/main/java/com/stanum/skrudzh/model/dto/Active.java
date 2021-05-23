package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.model.enums.PlannedIncomeTypeEnum;
import com.stanum.skrudzh.model.dto.base.Ordered;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZoneId;
import java.time.ZonedDateTime;


/**
 * Active
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class Active extends Ordered {

    @ApiModelProperty(value = "")
    private Long basketId = null;

    @ApiModelProperty(value = "")
    private BasketTypeEnum basketType = null;

    @ApiModelProperty(value = "")
    private String name = null;

    @ApiModelProperty(value = "")
    private String iconUrl = null;

    @ApiModelProperty(value = "")
    private Currency currency = null;

    @ApiModelProperty(value = "")
    private Long costCents = null;

    @ApiModelProperty(value = "")
    private Long monthlyPaymentCents = null;

    @ApiModelProperty(value = "")
    private Long paymentCentsAtPeriod = null;

    @ApiModelProperty(value = "")
    private Long investedAtPeriodCents = null;

    @ApiModelProperty(value = "")
    private Long boughtAtPeriodCents = null;

    @ApiModelProperty(value = "")
    private Long spentAtPeriodCents = null;

    @ApiModelProperty(value = "")
    private Integer annualIncomePercent = null;

    @ApiModelProperty(value = "")
    private Long monthlyPlannedIncomeCents = null;

    @ApiModelProperty(value = "")
    private PlannedIncomeTypeEnum plannedIncomeType = null;

    @ApiModelProperty(value = "")
    private Boolean isIncomePlanned = null;

    @ApiModelProperty(value = "")
    private Long goalAmountCents = null;

    @ApiModelProperty(value = "")
    private Long alreadyPaidCents = null;

    @ApiModelProperty(value = "")
    private Integer rowOrder = null;

    @ApiModelProperty(value = "")
    private Long activeTypeId = null;

    @ApiModelProperty(value = "")
    private Integer maxFetchInterval = null;

    @ApiModelProperty(value = "")
    private Reminder reminder = null;

    @ApiModelProperty(value = "")
    private IncomeSource incomeSource = null;

    @ApiModelProperty(value = "")
    private ActiveType activeType = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime createdAt = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime deletedAt = null;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime fetchFromDate = null;

    private AccountConnection saltEdgeAccountConnection;

    private boolean isActiveOpen;

    private Long fullSaleProfit;

    public Active(ActiveEntity activeEntity) {
        id = activeEntity.getId();
        costCents = activeEntity.getCostCents() != null
                ? activeEntity.getCostCents().longValue()
                : 0L;
        alreadyPaidCents = activeEntity.getAlreadyPaidCents() != null
                ? activeEntity.getAlreadyPaidCents().longValue()
                : 0L;
        annualIncomePercent = activeEntity.getAnnualIncomePercent();
        createdAt = activeEntity.getCreatedAt() != null
                ? ZonedDateTime.of(activeEntity.getCreatedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        deletedAt = activeEntity.getDeletedAt() != null
                ? ZonedDateTime.of(activeEntity.getDeletedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        goalAmountCents = activeEntity.getGoalAmountCents() != null
                ? activeEntity.getGoalAmountCents().longValue()
                : null;
        iconUrl = activeEntity.getIconUrl();
        isIncomePlanned = activeEntity.getIsIncomePlanned();
        monthlyPaymentCents = activeEntity.getMonthlyPaymentCents() != null
                ? activeEntity.getMonthlyPaymentCents().longValue()
                : null;
        monthlyPlannedIncomeCents = activeEntity.getMonthlyPlannedIncomeCents() != null
                ? activeEntity.getMonthlyPlannedIncomeCents().longValue()
                : null;
        name = activeEntity.getName();
        plannedIncomeType = activeEntity.getPlannedIncomeType();
        rowOrder = activeEntity.getRowOrder();
        activeType = activeEntity.getActiveTypeEntity() != null
                ? new ActiveType(activeEntity.getActiveTypeEntity())
                : null;
        activeTypeId = activeEntity.getId();
        basketId = activeEntity.getBasketEntity() != null
                ? activeEntity.getBasketEntity().getId()
                : null;
        basketType = activeEntity.getBasketEntity() != null
                ? activeEntity.getBasketEntity().getBasketType()
                : null;
        maxFetchInterval = activeEntity.getMaxFetchInterval();
    }

    public Active id(Long id) {
        this.id = id;
        return this;
    }

    public Active basketId(Long basketId) {
        this.basketId = basketId;
        return this;
    }

    public Active basketType(BasketTypeEnum basketType) {
        this.basketType = basketType;
        return this;
    }

    public Active name(String name) {
        this.name = name;
        return this;
    }

    public Active iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public Active currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public Active costCents(Long costCents) {
        this.costCents = costCents;
        return this;
    }

    public Active monthlyPaymentCents(Long monthlyPaymentCents) {
        this.monthlyPaymentCents = monthlyPaymentCents;
        return this;
    }

    public Active paymentCentsAtPeriod(Long paymentCentsAtPeriod) {
        this.paymentCentsAtPeriod = paymentCentsAtPeriod;
        return this;
    }

    public Active investedAtPeriodCents(Long investedAtPeriodCents) {
        this.investedAtPeriodCents = investedAtPeriodCents;
        return this;
    }

    public Active boughtAtPeriodCents(Long boughtAtPeriodCents) {
        this.boughtAtPeriodCents = boughtAtPeriodCents;
        return this;
    }

    public Active spentAtPeriodCents(Long spentAtPeriodCents) {
        this.spentAtPeriodCents = spentAtPeriodCents;
        return this;
    }

    public Active annualIncomePercent(Integer annualIncomePercent) {
        this.annualIncomePercent = annualIncomePercent;
        return this;
    }

    public Active monthlyPlannedIncomeCents(Long monthlyPlannedIncomeCents) {
        this.monthlyPlannedIncomeCents = monthlyPlannedIncomeCents;
        return this;
    }

    public Active plannedIncomeType(PlannedIncomeTypeEnum plannedIncomeType) {
        this.plannedIncomeType = plannedIncomeType;
        return this;
    }

    public Active isIncomePlanned(Boolean isIncomePlanned) {
        this.isIncomePlanned = isIncomePlanned;
        return this;

    }

    public Active goalAmountCents(Long goalAmountCents) {
        this.goalAmountCents = goalAmountCents;
        return this;
    }

    public Active alreadyPaidCents(Long alreadyPaidCents) {
        this.alreadyPaidCents = alreadyPaidCents;
        return this;
    }

    public Active rowOrder(Integer rowOrder) {
        this.rowOrder = rowOrder;
        return this;
    }

    public Active activeType(ActiveType activeType) {
        this.activeType = activeType;
        return this;
    }

    public Active reminder(Reminder reminder) {
        this.reminder = reminder;
        return this;
    }

    public Active incomeSource(IncomeSource incomeSource) {
        this.incomeSource = incomeSource;
        return this;
    }

}

