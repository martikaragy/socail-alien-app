package com.telerikacademy.socialalien.services.contracts;

import com.telerikacademy.socialalien.models.Post;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.enums.PrivacyType;

import java.util.List;

public interface PostService {

     List<Post> getAll();
     List<Post> getAllByUser(User user);
     Post findById(int id);
     void delete(Post post);
     void update(Post post);
     void create(Post post);
     List<Post>getPostsByPrivacyType(PrivacyType privacyType);
     List<Post> createNewsFeed(String principleName);
     List<Post> createUserFeed(String principleName, int userId);

    List<Post> findAllByUserId(Integer id);
}
