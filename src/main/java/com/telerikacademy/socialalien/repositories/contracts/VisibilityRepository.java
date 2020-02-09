package com.telerikacademy.socialalien.repositories.contracts;

import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisibilityRepository extends JpaRepository<Visibility, Integer> {
         Optional<Visibility> findByUser(User user);
}
