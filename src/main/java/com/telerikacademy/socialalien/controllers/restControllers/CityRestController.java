package com.telerikacademy.socialalien.controllers.restControllers;

import com.telerikacademy.socialalien.models.City;
import com.telerikacademy.socialalien.models.dtos.CityDto;
import com.telerikacademy.socialalien.services.contracts.CityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CityRestController {
    private CityService cityService;

    public CityRestController(CityService cityService){
        this.cityService = cityService;
    }

    @GetMapping("/cities")
    public List<City> getAllCities() {
        return cityService.getAllCities();
    }

    @GetMapping("/cities/{id}")
    public ResponseEntity<City> getCityById(@PathVariable int id){
        Optional<City> cityOptional = cityService.getCityById(id);
        return cityOptional.isPresent() ? ResponseEntity.ok(cityOptional.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping("/cities")
    public void getOrCreateCityWithNameAndPlanet(@Valid @RequestBody CityDto dto) {
        cityService.getOrCreateCityWithNameAndPlanet(dto.getCityName(), dto.getPlanetName());
    }

    @PutMapping("/cities/{id}")
    public void updateCityName(@PathVariable int id, @Valid @RequestBody City city){
            cityService.updateCityName(id,city);
    }

    @DeleteMapping("/cities/{id}")
    public void deleteCity(@PathVariable int id){
            cityService.deleteCity(id);
    }


}
