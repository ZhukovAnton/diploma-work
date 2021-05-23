package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.CreditTypeEntity;
import com.stanum.skrudzh.model.enums.PeriodSuperUnitEnum;
import com.stanum.skrudzh.model.enums.PeriodUnitEnum;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;


/**
 * CreditType
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class CreditType {

    public CreditType(CreditTypeEntity creditTypeEntity) {
        id = creditTypeEntity.getId();
        name = creditTypeEntity.getName();
        localizedKey = creditTypeEntity.getLocalizedKey();
        localizedName = ResourceBundle.getBundle("messages", RequestUtil.getLocale()).getString(localizedKey);
        periodUnit = creditTypeEntity.getPeriodUnit();
        periodSuperUnit = creditTypeEntity.getPeriodSuperUnit();
        minValue = creditTypeEntity.getMinValue();
        maxValue = creditTypeEntity.getMaxValue();
        defaultValue = creditTypeEntity.getDefaultValue();
        hasMonthlyPayments = creditTypeEntity.getHasMonthlyPayments();
        unitsInSuperUnit = creditTypeEntity.getUnitsInSuperUnit();
        rowOrder = creditTypeEntity.getRowOrder();
        isDefault = creditTypeEntity.getIsDefault();
        deletedAt = creditTypeEntity.getDeletedAt() != null
                ? ZonedDateTime.of(creditTypeEntity.getDeletedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
    }

    @ApiModelProperty(value = "")
    private Long id = null;

    @ApiModelProperty(value = "")
    private String name = null;

    @ApiModelProperty(value = "")
    private String localizedKey = null;

    @ApiModelProperty(value = "")
    private String localizedName = null;

    @ApiModelProperty(value = "")
    private PeriodUnitEnum periodUnit = null;

    @ApiModelProperty(value = "")
    private PeriodSuperUnitEnum periodSuperUnit = null;

    @ApiModelProperty(value = "")
    private Integer minValue = null;

    @ApiModelProperty(value = "")
    private Integer maxValue = null;

    @ApiModelProperty(value = "")
    private Integer defaultValue = null;

    @ApiModelProperty(value = "")
    private Boolean hasMonthlyPayments = null;

    @ApiModelProperty(value = "")
    private Integer unitsInSuperUnit = null;

    @ApiModelProperty(value = "")
    private Integer rowOrder = null;

    @ApiModelProperty(value = "")
    private Boolean isDefault = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime deletedAt;

    public CreditType id(Long id) {
        this.id = id;
        return this;
    }

    public CreditType name(String name) {
        this.name = name;
        return this;
    }

    public CreditType localizedKey(String localizedKey) {
        this.localizedKey = localizedKey;
        return this;
    }

    public CreditType localizedName(String localizedName) {
        this.localizedName = localizedName;
        return this;
    }

    public CreditType periodUnit(PeriodUnitEnum periodUnit) {
        this.periodUnit = periodUnit;
        return this;
    }

    public CreditType periodSuperUnit(PeriodSuperUnitEnum periodSuperUnit) {
        this.periodSuperUnit = periodSuperUnit;
        return this;
    }

    public CreditType minValue(Integer minValue) {
        this.minValue = minValue;
        return this;
    }

    public CreditType maxValue(Integer maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public CreditType defaultValue(Integer defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public CreditType hasMonthlyPayments(Boolean hasMonthlyPayments) {
        this.hasMonthlyPayments = hasMonthlyPayments;
        return this;
    }

    public CreditType unitsInSuperUnit(Integer unitsInSuperUnit) {
        this.unitsInSuperUnit = unitsInSuperUnit;
        return this;
    }

    public CreditType rowOrder(Integer rowOrder) {
        this.rowOrder = rowOrder;
        return this;
    }

    public CreditType isDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

}

