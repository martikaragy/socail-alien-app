package com.telerikacademy.socialalien.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telerikacademy.socialalien.models.enums.DeletedStatusType;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class BaseClass {

    @PositiveOrZero
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "deletedStatus")
    private DeletedStatusType deletedStatus;

    @JsonIgnore
    @Column(name = "dateCreated")
    private Timestamp dateCreated;

    @JsonIgnore
    @Column(name = "dateDeleted")
    private Timestamp dateDeleted;

    @JsonIgnore
    @Column(name="comment")
    private String comment;


    public BaseClass() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DeletedStatusType getDeletedStatus() {
        return deletedStatus;
    }

    public void setDeletedStatus(DeletedStatusType status) {
        this.deletedStatus = status;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Timestamp getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(Timestamp dateDeleted) {
        this.dateDeleted = dateDeleted;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}