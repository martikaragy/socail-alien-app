package com.telerikacademy.socialalien.repositories.contracts;

import com.telerikacademy.socialalien.models.Authority;
import com.telerikacademy.socialalien.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface AuthorityRepository extends JpaRepository<Authority,Integer> {

    List<Authority> findByUser(User user);

    Optional<Authority> findByUsernameAndAuthority(String username, String authority);


}
