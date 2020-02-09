package com.telerikacademy.socialalien.services.contracts;

import com.telerikacademy.socialalien.models.City;
import com.telerikacademy.socialalien.models.Planet;

import java.util.List;
import java.util.Optional;

public interface CityService {
    List<City> getAllCities();
    Optional<City> getCityById(int id);
    Optional<City> getCityByName(String name);
    List<City> getAllCitiesWithPlanet(Planet planet);
    City getOrCreateCityWithNameAndPlanet(String cityName, String planetName);
    City updateCity(City city);
    City updateCityName(int cityId,City city);
    void deleteCity(int id);
}
