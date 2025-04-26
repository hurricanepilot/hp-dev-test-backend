package com.hurricanepilot.hmctsdt.persistence.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import com.hurricanepilot.hmctsdt.constants.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "task")
@Getter
@Setter
public class TaskEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 80)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Status status = Status.NEW;

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(nullable = false)
    private ZonedDateTime dueDateTime;

    protected TaskEntity() {
        /* required by JPA */
    }

    public TaskEntity(String title, ZonedDateTime dueDateTime) {
        this(title, null, dueDateTime);
    }

    public TaskEntity(String title, String description, ZonedDateTime dueDateTime) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.dueDateTime = dueDateTime;
    }
}
