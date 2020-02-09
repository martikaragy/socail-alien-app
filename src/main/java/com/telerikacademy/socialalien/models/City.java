package com.telerikacademy.socialalien.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Where(clause = "deletedStatus <> 'DELETED'")
@Table(name="cities")
public class City extends BaseClass {


    @NotNull
    @Column(name="name")
    @Size(min = 2, max = 50, message = "City name should be between 2 and 50 characters.")
    private String name;

    @ManyToOne
    @JoinColumn(name="planet")
    @JsonIgnore
    private Planet planet;

    @OneToMany
    @JoinColumn(name="cityOfBirth")
    @JsonIgnore
    private List<User> natives = new ArrayList<>();

    @OneToMany
    @JoinColumn(name="cityOfResidence")
    @JsonIgnore
    private List<User> residents = new ArrayList<>();

    public City(){}

    public City(String name, Planet planet){
        this.name = name;
        this.planet = planet;
        this.natives = new ArrayList<>();
        this.residents = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Planet getPlanet() {
        return planet;
    }

    public void setPlanet(Planet planet) {
        this.planet = planet;
    }

    public List<User> getNatives() {
        return natives;
    }

    public void setNatives(List<User> natives) {
        this.natives = natives;
    }

    public List<User> getResidents() {
        return residents;
    }

    public void setResidents(List<User> residents) {
        this.residents = residents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        City city = (City) o;
        return name.equals(city.name) &&
                planet.equals(city.planet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, planet);
    }
}
