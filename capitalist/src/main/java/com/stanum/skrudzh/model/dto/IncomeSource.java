package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.model.enums.CreationTypeEnum;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * IncomeSource
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class IncomeSource {

    public IncomeSource(IncomeSourceEntity incomeSourceEntity) {
        id = incomeSourceEntity.getId();
        name = incomeSourceEntity.getName();
        iconUrl = incomeSourceEntity.getIconUrl();
        rowOrder = incomeSourceEntity.getRowOrder();
        monthlyPlannedCents = incomeSourceEntity.getMonthlyPlannedCents() != null
                ? incomeSourceEntity.getMonthlyPlannedCents().longValue()
                : null;
        isBorrowOrReturn = incomeSourceEntity.getIsBorrow();
        userId = incomeSourceEntity.getUser() != null
                ? incomeSourceEntity.getUser().getId()
                : null;
        activeId = incomeSourceEntity.getActive() != null
                ? incomeSourceEntity.getActive().getId()
                : null;
        createdAt = incomeSourceEntity.getCreatedAt() != null
                ? ZonedDateTime.of(incomeSourceEntity.getCreatedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        deletedAt = incomeSourceEntity.getDeletedAt() != null
                ? ZonedDateTime.of(incomeSourceEntity.getDeletedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        isChild = activeId != null;
        isVirtual = incomeSourceEntity.getIsVirtual();
        description = incomeSourceEntity.getDescription();
        creationType = incomeSourceEntity.getCreationType();
        prototypeKey = incomeSourceEntity.getPrototypeKey();
    }

    @ApiModelProperty(value = "")
    private Long id = null;

    @ApiModelProperty(value = "")
    private String name = null;

    @ApiModelProperty(value = "")
    private String iconUrl = null;

    @ApiModelProperty(value = "")
    private Currency currency = null;

    @ApiModelProperty(value = "")
    private Long monthlyPlannedCents = null;

    @ApiModelProperty(value = "")
    private Long plannedCentsAtPeriod = null;

    @ApiModelProperty(value = "")
    private Long gotCentsAtPeriod = null;

    @ApiModelProperty(value = "")
    private Long userId = null;

    @ApiModelProperty(value = "")
    private Integer rowOrder = null;

    @ApiModelProperty(value = "")
    private Boolean isChild = null;

    @ApiModelProperty(value = "")
    private Long activeId = null;

    @ApiModelProperty(value = "")
    private String description = null;

    @ApiModelProperty(value = "")
    private CreationTypeEnum creationType = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime createdAt = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime deletedAt = null;

    @ApiModelProperty(value = "")
    private Boolean isBorrowOrReturn = null;

    private Boolean isVirtual;

    @ApiModelProperty(value = "")
    private List<Borrow> waitingDebts = new ArrayList<Borrow>();

    @ApiModelProperty(value = "")
    private Reminder reminder = null;

    @ApiModelProperty(value = "")
    private String prototypeKey = null;

    public IncomeSource id(Long id) {
        this.id = id;
        return this;
    }

    public IncomeSource name(String name) {
        this.name = name;
        return this;
    }

    public IncomeSource iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public IncomeSource currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public IncomeSource monthlyPlannedCents(Long monthlyPlannedCents) {
        this.monthlyPlannedCents = monthlyPlannedCents;
        return this;
    }

    public IncomeSource plannedCentsAtPeriod(Long plannedCentsAtPeriod) {
        this.plannedCentsAtPeriod = plannedCentsAtPeriod;
        return this;
    }

    public IncomeSource gotCentsAtPeriod(Long gotCentsAtPeriod) {
        this.gotCentsAtPeriod = gotCentsAtPeriod;
        return this;
    }

    public IncomeSource userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public IncomeSource rowOrder(Integer rowOrder) {
        this.rowOrder = rowOrder;
        return this;
    }

    public IncomeSource isChild(Boolean isChild) {
        this.isChild = isChild;
        return this;
    }

    public IncomeSource activeId(Long activeId) {
        this.activeId = activeId;
        return this;
    }

    public IncomeSource isBorrowOrReturn(Boolean isBorrowOrReturn) {
        this.isBorrowOrReturn = isBorrowOrReturn;
        return this;
    }

    public IncomeSource waitingDebts(List<Borrow> waitingDebts) {
        this.waitingDebts = waitingDebts;
        return this;
    }

    public IncomeSource addWaitingDebtsItem(Borrow waitingDebtsItem) {
        this.waitingDebts.add(waitingDebtsItem);
        return this;
    }

    public IncomeSource reminder(Reminder reminder) {
        this.reminder = reminder;
        return this;
    }

    public IncomeSource prototypeKey(String prototypeKey) {
        this.prototypeKey = prototypeKey;
        return this;
    }


}

