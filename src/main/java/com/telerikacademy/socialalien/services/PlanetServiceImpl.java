package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.exceptions.DuplicateEntityException;
import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.models.City;
import com.telerikacademy.socialalien.models.Planet;
import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import com.telerikacademy.socialalien.repositories.contracts.PlanetRepository;
import com.telerikacademy.socialalien.services.contracts.CityService;
import com.telerikacademy.socialalien.services.contracts.PlanetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class PlanetServiceImpl implements PlanetService {
    private PlanetRepository planetRepository;
    private CityService cityService;

    @Autowired
    public PlanetServiceImpl(PlanetRepository planetRepository, CityService cityService) {
        this.cityService = cityService;
        this.planetRepository = planetRepository;
    }

    @Override
    public List<Planet> getAllPlanets(){
        List<Planet> planets = planetRepository.findAll();
        return planets;
    }

    @Override
    public Optional<Planet> getPlanetById(int id){
        return planetRepository.findById(id);
    }

    @Override
    public Optional<Planet> getPlanetByName(String name){
        return planetRepository.findByNameIgnoreCase(name);
    }

    @Override
    public Planet createPlanet(Planet planet){
        if(planetRepository.existsByName(planet.getName())){
            throw new DuplicateEntityException(String.format("Planet with name %s already exists.", planet.getName()));
        }
        planet.setDeletedStatus(DeletedStatusType.ACTIVE);
        return planetRepository.save(planet);
    }

    @Override
    public Planet updatePlanet(Planet planet){
        return planetRepository.save(planet);
    }

    @Override
    public Planet updatePlanetName(int planetId, Planet planet){
        Optional <Planet> planetOptional = getPlanetById(planetId);
        Planet planetDao = planetOptional.orElseThrow(() -> new EntityNotFoundException(
                String.format("Planet with id %d does not exist.", planetId)));
        String newName = planet.getName();
        planetDao.setName(newName);
        return planetRepository.save(planetDao);
    }

    @Transactional
    @Override
    public void deletePlanet(int id){
        Optional<Planet> planetOptional = getPlanetById(id);
        Planet planetDao = planetOptional.orElseThrow(() -> new EntityNotFoundException(
                String.format("Planet with id %d does not exist.", id)));
        planetDao.setDeletedStatus(DeletedStatusType.DELETED);
        planetDao.setDateDeleted(Timestamp.from(Instant.now()));
        planetRepository.save(planetDao);
        List<City> citiesWithPlanet = cityService.getAllCitiesWithPlanet(planetDao);
        citiesWithPlanet.forEach(city -> {city.setDeletedStatus(DeletedStatusType.DELETED); city.setDateDeleted(Timestamp.from(Instant.now()));});
    }

    @Override
    public boolean existsByName(String name){
        return planetRepository.existsByName(name);
    }

}
