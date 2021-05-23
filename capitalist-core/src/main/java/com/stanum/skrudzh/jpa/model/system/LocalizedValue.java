package com.stanum.skrudzh.jpa.model.system;

import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.model.enums.MessageType;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "localized_values")
@Data
@ToString
public class LocalizedValue implements Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private MessageType type;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;

    @Column(name = "locale")
    private String locale;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalizedValue that = (LocalizedValue) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
