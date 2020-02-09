package com.telerikacademy.socialalien.repositories.contracts;

import com.telerikacademy.socialalien.models.Like;
import com.telerikacademy.socialalien.models.Post;
import com.telerikacademy.socialalien.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like,Integer> {

    Like findByUserAndPost(User user, Post post);
    List<Like> findAllByPost(Post post);
    List<Like> findAllByUser(User user);
}
