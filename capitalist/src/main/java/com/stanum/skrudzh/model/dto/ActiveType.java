package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.ActiveTypeEntity;
import com.stanum.skrudzh.model.enums.PlannedIncomeTypeEnum;
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
 * ActiveType
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
public class ActiveType {

    @ApiModelProperty(value = "")
    private Long id = null;

    @ApiModelProperty(value = "")
    private String name = null;

    @ApiModelProperty(value = "")
    private String localizedKey = null;

    @ApiModelProperty(value = "")
    private String localizedName = null;

    @ApiModelProperty(value = "")
    private PlannedIncomeTypeEnum defaultPlannedIncomeType = null;

    @ApiModelProperty(value = "")
    private Boolean isGoalAmountRequired = null;

    @ApiModelProperty(value = "")
    private Boolean isIncomePlannedDefault = null;

    @ApiModelProperty(value = "")
    private Boolean buyingAssetsDefault = null;

    @ApiModelProperty(value = "")
    private Boolean onlyBuyingAssets = null;

    @ApiModelProperty(value = "")
    private String costTitle = null;

    @ApiModelProperty(value = "")
    private String monthlyPaymentTitle = null;

    @ApiModelProperty(value = "")
    private String buyingAssetsTitle = null;

    @ApiModelProperty(value = "")
    private Integer rowOrder = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime deletedAt;

    public ActiveType(ActiveTypeEntity activeTypeEntity) {
        id = activeTypeEntity.getId();
        name = activeTypeEntity.getName();
        localizedKey = activeTypeEntity.getLocalizedKey();
        localizedName = ResourceBundle
                .getBundle("messages", RequestUtil.getLocale())
                .getString(localizedKey);
        defaultPlannedIncomeType = activeTypeEntity.getDefaultPlannedIncomeType();
        isGoalAmountRequired = activeTypeEntity.getIsGoalAmountRequired();
        isIncomePlannedDefault = activeTypeEntity.getIsIncomePlannedDefault();
        buyingAssetsDefault = activeTypeEntity.getBuyingAssetsDefault();
        onlyBuyingAssets = activeTypeEntity.getOnlyBuyingAssets();
        costTitle = activeTypeEntity.getCostTitleLocalizedKey() != null
                ? ResourceBundle.getBundle("messages", RequestUtil.getLocale())
                .getString(activeTypeEntity.getCostTitleLocalizedKey())
                : null;
        monthlyPaymentTitle = activeTypeEntity.getMonthlyPaymentLocalizedKey() != null
                ? ResourceBundle.getBundle("messages", RequestUtil.getLocale())
                .getString(activeTypeEntity.getMonthlyPaymentLocalizedKey())
                : null;
        buyingAssetsTitle = activeTypeEntity.getBuyingAssetsTitleLocalizedKey() != null
                ? ResourceBundle.getBundle("messages", RequestUtil.getLocale())
                .getString(activeTypeEntity.getBuyingAssetsTitleLocalizedKey())
                : null;
        rowOrder = activeTypeEntity.getRowOrder();
        deletedAt = activeTypeEntity.getDeletedAt() != null
                ? ZonedDateTime.of(activeTypeEntity.getDeletedAt().toLocalDateTime(), ZoneId.of("Z"))
                : null;
    }

    public ActiveType id(Long id) {
        this.id = id;
        return this;
    }

    public ActiveType name(String name) {
        this.name = name;
        return this;
    }

    public ActiveType localizedKey(String localizedKey) {
        this.localizedKey = localizedKey;
        return this;
    }

    public ActiveType localizedName(String localizedName) {
        this.localizedName = localizedName;
        return this;
    }

    public ActiveType defaultPlannedIncomeType(PlannedIncomeTypeEnum defaultPlannedIncomeType) {
        this.defaultPlannedIncomeType = defaultPlannedIncomeType;
        return this;
    }

    public ActiveType isGoalAmountRequired(Boolean isGoalAmountRequired) {
        this.isGoalAmountRequired = isGoalAmountRequired;
        return this;
    }

    public ActiveType isIncomePlannedDefault(Boolean isIncomePlannedDefault) {
        this.isIncomePlannedDefault = isIncomePlannedDefault;
        return this;
    }

    public ActiveType buyingAssetsDefault(Boolean buyingAssetsDefault) {
        this.buyingAssetsDefault = buyingAssetsDefault;
        return this;
    }

    public ActiveType onlyBuyingAssets(Boolean onlyBuyingAssets) {
        this.onlyBuyingAssets = onlyBuyingAssets;
        return this;
    }

    public ActiveType costTitle(String costTitle) {
        this.costTitle = costTitle;
        return this;
    }

    public ActiveType monthlyPaymentTitle(String monthlyPaymentTitle) {
        this.monthlyPaymentTitle = monthlyPaymentTitle;
        return this;
    }

    public ActiveType buyingAssetsTitle(String buyingAssetsTitle) {
        this.buyingAssetsTitle = buyingAssetsTitle;
        return this;
    }

    public ActiveType rowOrder(Integer rowOrder) {
        this.rowOrder = rowOrder;
        return this;
    }

}

