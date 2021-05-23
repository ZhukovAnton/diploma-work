package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.jpa.model.base.HasNameAndIcon;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "transactionable_examples")
@Data
public class TransactionableExampleEntity extends Rankable implements HasNameAndIcon, Base, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "transactionable_type")
    private String transactionableType;

    @Enumerated
    @Column(name = "basket_type")
    private BasketTypeEnum basketType;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "localized_key")
    private String nameKey;

    @Column(name = "create_by_default")
    private Boolean createByDefault;

    @Column(name = "country")
    private String country;

    @Column(name = "description_localized_key")
    private String descriptionKey;

    @Column(name = "prototype_key")
    private String prototypeKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionableExampleEntity that = (TransactionableExampleEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
