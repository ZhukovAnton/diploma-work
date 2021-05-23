package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.converters.Encryptor;
import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
@EqualsAndHashCode
@Data
@NoArgsConstructor
public class UserEntity implements Base, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "firstname")
    @Convert(converter = Encryptor.class)
    private String firstname;

    @Column(name = "lastname")
    @Convert(converter = Encryptor.class)
    private String lastname;

    @Column(name = "email")
    @Convert(converter = Encryptor.class)
    private String email;

    @Column(name = "password_digest")
    private String passwordDigest;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "guest")
    private Boolean guest;

    @Column(name = "email_confirmation_code")
    private String emailConfirmationCode;

    @Basic
    @Column(name = "email_confirmed_at")
    private Timestamp emailConfirmedAt;

    @Column(name = "password_reset_code")
    private String passwordResetCode;

    @Column(name = "password_reset_attempts")
    private Integer passwordResetAttempts;

    @Basic
    @Column(name = "password_reset_code_created_at")
    private Timestamp passwordResetCodeCreatedAt;

    @Column(name = "locale")
    private String locale;

    @Column(name = "default_currency")
    private String defaultCurrency;

    @Basic
    @Enumerated
    @Column(name = "default_period")
    private PeriodEnum defaultPeriod;

    @Column(name = "device_token")
    private String deviceToken;

    @Column(name = "salt_edge_customer_secret")
    private String saltEdgeCustomerSecret;

    @Column(name = "salt_edge_customer_id")
    private String saltEdgeCustomerId;

    @Column(name = "onboarded")
    private Boolean onBoarded;

    @Column(name = "has_active_subscription")
    private Boolean hasActiveSubscription;

    @Column(name = "planned_saving_percent")
    private BigDecimal plannedSavingPercent;

    @Column(name = "region")
    private String region;

}
