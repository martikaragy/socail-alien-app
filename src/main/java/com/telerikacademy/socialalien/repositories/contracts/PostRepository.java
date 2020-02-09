package com.telerikacademy.socialalien.repositories.contracts;

import com.telerikacademy.socialalien.models.Post;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.enums.PrivacyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {

    List<Post> findAllByUser(User user);

    Page<Post> findAllByUser(User user, Pageable pageable);

    List<Post> getAllByPrivacyType(PrivacyType privacyType);

    List<Post> findAllByUserAndPrivacyType(User user, PrivacyType privacyType);
}
