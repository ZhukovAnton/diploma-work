package com.stanum.skrudzh.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.utils.constant.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;


/**
 * User
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2020-01-23T12:04:59.069+03:00")
@EqualsAndHashCode
@ToString
@Data
@NoArgsConstructor
public class User {

    public User(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.email = userEntity.getEmail();
        this.firstname = userEntity.getFirstname();
        this.lastname = userEntity.getLastname();
        this.guest = userEntity.getGuest();
        this.defaultPeriod = userEntity.getDefaultPeriod();
        this.registrationConfirmed = userEntity.getEmailConfirmedAt() != null;
        this.saltEdgeCustomerSecret = userEntity.getSaltEdgeCustomerSecret();
        this.onboarded = userEntity.getOnBoarded();
        this.hasActiveSubscription = userEntity.getHasActiveSubscription();
        if(userEntity.getSaltEdgeCustomerId() != null) {
            this.saltEdgeCustomer = new Customer(userEntity.getSaltEdgeCustomerId(), userEntity.getSaltEdgeCustomerSecret());
        }
    }

    @ApiModelProperty(value = "")
    private Long id = null;

    @ApiModelProperty(value = "")
    private String email = null;

    @ApiModelProperty(value = "")
    private String firstname = null;

    @ApiModelProperty(value = "")
    private String lastname = null;

    @ApiModelProperty(value = "")
    private Boolean guest = null;

    @ApiModelProperty(value = "")
    private Boolean registrationConfirmed = null;

    @ApiModelProperty(value = "")
    private Long joyBasketId = null;

    @ApiModelProperty(value = "")
    private Long riskBasketId = null;

    @ApiModelProperty(value = "")
    private Long safeBasketId = null;

    @ApiModelProperty(value = "")
    private Currency defaultCurrency = null;

    @ApiModelProperty(value = "")
    private PeriodEnum defaultPeriod = null;

    @ApiModelProperty(value = "")
    private String saltEdgeCustomerSecret = null;

    @ApiModelProperty(value = "")
    private boolean onboarded;

    @ApiModelProperty(value = "")
    private boolean hasActiveSubscription;

    @ApiModelProperty(value = "")
    private Customer saltEdgeCustomer = null;

    @ApiModelProperty(value = "")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private ZonedDateTime oldestTransactionGotAt;

    @ApiModelProperty(value = "")
    private final String iosMinVersion = Constants.IOS_MIN_VERSION;

    @ApiModelProperty(value = "")
    private final String iosMinBuild = Constants.IOS_MIN_BUILD;

    public User id(Long id) {
        this.id = id;
        return this;
    }

    public User email(String email) {
        this.email = email;
        return this;
    }

    public User firstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public User lastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public User guest(Boolean guest) {
        this.guest = guest;
        return this;
    }

    public User registrationConfirmed(Boolean registrationConfirmed) {
        this.registrationConfirmed = registrationConfirmed;
        return this;
    }

    public User joyBasketId(Long joyBasketId) {
        this.joyBasketId = joyBasketId;
        return this;
    }

    public User riskBasketId(Long riskBasketId) {
        this.riskBasketId = riskBasketId;
        return this;
    }

    public User safeBasketId(Long safeBasketId) {
        this.safeBasketId = safeBasketId;
        return this;
    }

    public User defaultCurrency(Currency defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
        return this;
    }

    public User defaultPeriod(PeriodEnum defaultPeriod) {
        this.defaultPeriod = defaultPeriod;
        return this;
    }

    public User saltEdgeCustomerSecret(String saltEdgeCustomerSecret) {
        this.saltEdgeCustomerSecret = saltEdgeCustomerSecret;
        return this;
    }

    public User saltedgeCustomer(Customer saltEdgeCustomer) {
        this.saltEdgeCustomer = saltEdgeCustomer;
        return this;
    }
}

