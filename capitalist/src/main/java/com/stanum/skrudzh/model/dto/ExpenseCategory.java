package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
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
 * ExpenseCategory
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class ExpenseCategory {

    public ExpenseCategory(ExpenseCategoryEntity expenseCategoryEntity) {
        id = expenseCategoryEntity.getId();
        monthlyPlannedCents = expenseCategoryEntity.getMonthlyPlannedCents() != null
                ? expenseCategoryEntity.getMonthlyPlannedCents().longValue()
                : null;
        basketId = expenseCategoryEntity.getBasket() != null
                ? expenseCategoryEntity.getBasket().getId()
                : null;
        basketType = expenseCategoryEntity.getBasket() != null
                ? expenseCategoryEntity.getBasket().getBasketType()
                : null;
        createdAt = expenseCategoryEntity.getCreatedAt() != null
                ? ZonedDateTime.of(expenseCategoryEntity.getCreatedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        deletedAt = expenseCategoryEntity.getDeletedAt() != null
                ? ZonedDateTime.of(expenseCategoryEntity.getDeletedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
        iconUrl = expenseCategoryEntity.getIconUrl();
        name = expenseCategoryEntity.getName();
        isBorrowOrReturn = expenseCategoryEntity.getIsBorrow();
        rowOrder = expenseCategoryEntity.getRowOrder();
        creditId = expenseCategoryEntity.getCreditEntity() != null
                ? expenseCategoryEntity.getCreditEntity().getId()
                : null;
        isVirtual = expenseCategoryEntity.getIsVirtual();
        creationType = expenseCategoryEntity.getCreationType();
        description = expenseCategoryEntity.getDescription();
        prototypeKey = expenseCategoryEntity.getPrototypeKey();
    }

    @ApiModelProperty(value = "")
    private Long id = null;

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
    private Long monthlyPlannedCents = null;

    @ApiModelProperty(value = "")
    private Long spentCentsAtPeriod = null;

    @ApiModelProperty(value = "")
    private Long plannedCentsAtPeriod = null;

    @ApiModelProperty(value = "")
    private Long creditId = null;

    @ApiModelProperty(value = "")
    private Boolean isBorrowOrReturn = null;

    @ApiModelProperty(value = "")
    private List<Borrow> waitingLoans = new ArrayList<Borrow>();

    @ApiModelProperty(value = "")
    private Reminder reminder = null;

    @ApiModelProperty(value = "")
    private Integer rowOrder = null;

    @ApiModelProperty(value = "")
    private String description = null;

    @ApiModelProperty(value = "")
    private CreationTypeEnum creationType = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime createdAt;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime deletedAt;

    @ApiModelProperty(value = "")
    private String prototypeKey = null;

    private Boolean isVirtual;

    public ExpenseCategory id(Long id) {
        this.id = id;
        return this;
    }

    public ExpenseCategory basketId(Long basketId) {
        this.basketId = basketId;
        return this;
    }

    public ExpenseCategory basketType(BasketTypeEnum basketType) {
        this.basketType = basketType;
        return this;
    }

    public ExpenseCategory name(String name) {
        this.name = name;
        return this;
    }

    public ExpenseCategory iconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    public ExpenseCategory currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public ExpenseCategory monthlyPlannedCents(Long monthlyPlannedCents) {
        this.monthlyPlannedCents = monthlyPlannedCents;
        return this;
    }

    public ExpenseCategory spentCentsAtPeriod(Long spentCentsAtPeriod) {
        this.spentCentsAtPeriod = spentCentsAtPeriod;
        return this;
    }

    public ExpenseCategory plannedCentsAtPeriod(Long plannedCentsAtPeriod) {
        this.plannedCentsAtPeriod = plannedCentsAtPeriod;
        return this;
    }

    public ExpenseCategory creditId(Long creditId) {
        this.creditId = creditId;
        return this;
    }

    public ExpenseCategory isBorrowOrReturn(Boolean isBorrowOrReturn) {
        this.isBorrowOrReturn = isBorrowOrReturn;
        return this;
    }

    public ExpenseCategory waitingLoans(List<Borrow> waitingLoans) {
        this.waitingLoans = waitingLoans;
        return this;
    }

    public ExpenseCategory addWaitingLoansItem(Borrow waitingLoansItem) {
        this.waitingLoans.add(waitingLoansItem);
        return this;
    }

    public ExpenseCategory reminder(Reminder reminder) {
        this.reminder = reminder;
        return this;
    }

    public ExpenseCategory rowOrder(Integer rowOrder) {
        this.rowOrder = rowOrder;
        return this;
    }

    public ExpenseCategory prototypeKey(String prototypeKey) {
        this.prototypeKey = prototypeKey;
        return this;
    }
}

