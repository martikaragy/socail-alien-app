package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.models.City;
import com.telerikacademy.socialalien.models.Planet;
import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import com.telerikacademy.socialalien.repositories.contracts.CityRepository;
import com.telerikacademy.socialalien.services.contracts.CityService;
import com.telerikacademy.socialalien.services.contracts.PlanetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CityServiceImpl implements CityService {
    private CityRepository cityRepository;
    private PlanetService planetService;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository, @Lazy PlanetService planetService) {
        this.cityRepository=cityRepository;
        this.planetService = planetService;
    }

    @Override
    public List<City> getAllCities(){
        List<City> cities = cityRepository.findAll();
        return cities;
    }

    @Override
    public Optional<City> getCityById(int id){
        return cityRepository.findById(id);
    }

    @Override
    public Optional <City> getCityByName(String name){
        return cityRepository.findByNameIgnoreCase(name);
    }

    @Override
    public List<City> getAllCitiesWithPlanet(Planet planet){
        return cityRepository.findAllByPlanet(planet);
    }

    @Transactional
    @Override
    public City getOrCreateCityWithNameAndPlanet(String cityName, String planetName){
        Optional<Planet> planetOptional = planetService.getPlanetByName(planetName);
        Planet planet = planetOptional.orElseGet(()->planetService.createPlanet(new Planet(planetName)));
        Optional<City> cityOptional = cityRepository.findByNameAndPlanet(cityName, planet);
        City city = cityOptional.orElseGet(
                () -> {
                    City cityDto = new City(cityName, planet);
                    cityDto.setDeletedStatus(DeletedStatusType.ACTIVE);
                    return cityRepository.save(cityDto);
                });
        return city;
    }

    @Override
    public City updateCityName(int cityId, City cityDto){
        Optional <City> cityOptional = cityRepository.findById(cityId);
        City cityDao = cityOptional.orElseThrow(() -> new EntityNotFoundException(String.format("City with id %d does not exist.", cityId)));
        cityDao.setName(cityDto.getName());
        return cityRepository.save(cityDao);
    }

    @Override
    public City updateCity(City city){
        return cityRepository.save(city);
    }

    @Override
    public void deleteCity(int id){
        Optional<City> cityOptional = cityRepository.findById(id);
        City cityDao = cityOptional.orElseThrow(() -> new EntityNotFoundException(String.format("City with id %d does not exist.", id)));
        cityDao.setDeletedStatus(DeletedStatusType.DELETED);
        cityDao.setDateDeleted(Timestamp.from(Instant.now()));
        cityRepository.save(cityDao);
    }

}
