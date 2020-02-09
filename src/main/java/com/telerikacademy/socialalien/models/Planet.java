package com.telerikacademy.socialalien.models;

import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Where(clause = "deletedStatus <> 'DELETED'")
@Table(name="planets")
public class Planet extends BaseClass{

    @NotNull
    @Column(name="name")
    @Size(min = 2, max = 50, message = "Planet name should be between 2 and 50 characters." )
    private String name;

    public Planet(){}

    public Planet(String name){
        this.name = name;
    }

    @PrePersist
    public void setDeletedStatusToActive(){
        if (getDeletedStatus() == null) {
            setDeletedStatus(DeletedStatusType.ACTIVE);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Planet planet = (Planet) o;
        return name.equals(planet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
