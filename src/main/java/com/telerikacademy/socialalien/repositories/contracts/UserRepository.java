package com.telerikacademy.socialalien.repositories.contracts;

import com.telerikacademy.socialalien.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    List<User> findAllByFirstNameContains(@Size(max = 50, message = "User's first name should be maximum 50 characters.") String firstName);
    List<User> findAllByLastNameContains(@Size(max = 50, message = "User's last name should be maximum 50 characters.") String lastName);
    List<User> findAllByEmailContains(@Email(message = "Invalid email address.") @NotEmpty(message = "Please provide email address.") String email);
    List<User> findAllByUsernameContains(@NotEmpty(message = "Please provide username.") @Size(min = 2, max = 50, message = "Username should be between 2 and 50 characters.") String username);
}

