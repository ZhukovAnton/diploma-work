package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "exchange_rates")
@EqualsAndHashCode
@NoArgsConstructor
@Data
public class ExchangeRateEntity implements Base, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Basic
    @Column(name = "from_currency")
    private String from;

    @Basic
    @Column(name = "to_currency")
    private String to;

    @Basic
    @Column(name = "rate")
    private BigDecimal rate;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt = TimeUtil.now();

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt = TimeUtil.now();

    @Column(name = "is_updated")
    private Boolean isUpdated = false;

    public ExchangeRateEntity(String from, String to) {
        this.from = from;
        this.to = to;
    }

}
