package com.telerikacademy.socialalien.models.dtos;

import javax.validation.constraints.NotNull;

public class CityDto {
    @NotNull
    private String cityName;
    @NotNull
    private String planetName;

    public CityDto() {
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPlanetName() {
        return planetName;
    }

    public void setPlanetName(String planetName) {
        this.planetName = planetName;
    }
}
