package com.telerikacademy.socialalien.services.contracts;


import com.telerikacademy.socialalien.models.Like;
import com.telerikacademy.socialalien.models.Post;
import com.telerikacademy.socialalien.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeService  {

     List<Like> getAll();
     Like findById(int id);

    List<Like> findAllByUser(User user);
     void delete(Like like);
     void update(Like like);
     void create(Like like);
    void createLikeOnPost(String userName, int postId);
     List<Like> findAllByPost(Post post);

}
