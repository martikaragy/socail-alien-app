package com.telerikacademy.socialalien.services.contracts;

import com.telerikacademy.socialalien.models.Planet;

import java.util.List;
import java.util.Optional;

public interface PlanetService {
    List<Planet> getAllPlanets();
    Optional<Planet> getPlanetById(int id);
    Optional<Planet> getPlanetByName(String name);
    Planet createPlanet(Planet planet);
    Planet updatePlanet(Planet planet);
    Planet updatePlanetName(int planetId, Planet planet);
    void deletePlanet(int id);
    boolean existsByName(String name);
}
