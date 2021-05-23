package com.stanum.skrudzh.jpa.model;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@Data
@Where(clause = "deleted_at is null")
public abstract class Rankable implements Serializable {

    @Column(name = "row_order")
    private Integer rowOrder;

}
