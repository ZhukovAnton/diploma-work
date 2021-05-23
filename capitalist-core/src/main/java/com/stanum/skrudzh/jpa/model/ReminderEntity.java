package com.stanum.skrudzh.jpa.model;

import com.stanum.skrudzh.jpa.model.base.Base;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Where(clause = "deleted_at is null")
@Table(name = "reminders")
@EqualsAndHashCode
@Data
public class ReminderEntity implements Base, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "remindable_type")
    private String remindableType;

    @Column(name = "remindable_id")
    private Long remindableId;

    @Basic
    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "recurrence_rule")
    private String recurrenceRule;

    @Column(name = "message")
    private String message;

    @Basic
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
