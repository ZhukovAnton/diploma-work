package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.jpa.model.base.HasUser;
import com.stanum.skrudzh.model.enums.ConnectionStatusEnum;
import com.stanum.skrudzh.model.enums.LastStageStatusEnum;
import com.stanum.skrudzh.model.enums.SaltedgeSessionTypeEnum;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "connections")
@EqualsAndHashCode
@Data
@ToString
public class ConnectionEntity implements Base, HasUser, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "salt_edge_connection_id", unique = true)
    private String saltEdgeConnectionId;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "customer_id")
    private String customerId;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt = TimeUtil.now();

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt = TimeUtil.now();

    @Column(name = "secret")
    private String secret;

    @Column(name = "provider_code")
    private String providerCode;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "provider_logo_url")
    private String providerLogoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ConnectionStatusEnum status;

    @Enumerated
    @Column(name = "last_stage_status")
    private LastStageStatusEnum lastStageStatus;

    @Column(name = "interactive")
    private Boolean interactive;

    @Column(name = "next_refresh_possible_at")
    private Timestamp nextRefreshPossibleAt;

    @Column(name = "session_url")
    private String sessionUrl;

    @Column(name = "session_url_expires_at")
    private Timestamp sessionUrlExpiresAt;

    @Basic
    @Column(name = "last_success_at")
    private Timestamp lastSuccessAt;

    @Column(name = "session_type")
    @Enumerated(value = EnumType.STRING)
    private SaltedgeSessionTypeEnum sessionType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "required_interactive_fields_names")
    private String requiredInteractiveFieldsNames;
}
