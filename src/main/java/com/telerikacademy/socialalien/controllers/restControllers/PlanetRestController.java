package com.telerikacademy.socialalien.controllers.restControllers;

import com.telerikacademy.socialalien.models.Planet;
import com.telerikacademy.socialalien.services.contracts.PlanetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PlanetRestController {
    private PlanetService planetService;

    public PlanetRestController(PlanetService planetService){
        this.planetService = planetService;
    }

    @GetMapping("/planets")
    public List<Planet> getAllPlanets() {
        return planetService.getAllPlanets();
    }

    @GetMapping("/planets/{id}")
    public ResponseEntity<Planet> getPlanetById(@PathVariable int id){
        Optional<Planet> planetOptional =planetService.getPlanetById(id);
        return planetOptional.isPresent() ? ResponseEntity.ok(planetOptional.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping("/planets")
    public void createPlanet(@Valid @RequestBody Planet planet) {
        planetService.createPlanet(planet);
    }

    @PutMapping("/planets/{id}")
    public void updatePlanet(@PathVariable int id, @Valid @RequestBody Planet planet) {
        planetService.updatePlanetName(id, planet);
    }

    @DeleteMapping("/planets/{id}")
    public void deletePlanet(@PathVariable int id){
        planetService.deletePlanet(id);
    }



}
