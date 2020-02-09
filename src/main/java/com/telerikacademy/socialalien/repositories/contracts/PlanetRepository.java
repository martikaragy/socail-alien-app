package com.telerikacademy.socialalien.repositories.contracts;

import com.telerikacademy.socialalien.models.Planet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanetRepository extends JpaRepository<Planet,Integer> {

    Optional<Planet> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

}
