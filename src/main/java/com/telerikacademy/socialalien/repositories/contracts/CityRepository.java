package com.telerikacademy.socialalien.repositories.contracts;

import com.telerikacademy.socialalien.models.City;
import com.telerikacademy.socialalien.models.Planet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CityRepository extends JpaRepository<City,Integer> {
      Optional<City> findByNameIgnoreCase(String name);

      Optional<City> findByNameAndPlanet(String name, Planet planet);

      List<City> findAllByPlanet(Planet planet);

      boolean existsByName(String name);

}
