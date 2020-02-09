package com.telerikacademy.socialalien.services.contracts;

import com.telerikacademy.socialalien.models.Comment;
import com.telerikacademy.socialalien.models.Post;
import com.telerikacademy.socialalien.models.User;

import java.util.List;

public interface CommentService {

    List<Comment> getAll();
    List<Comment> getAllByUser(User user);
    Comment findById(int id);
    void delete(Comment comment);
    void update(Comment comment);
    void create(Comment comment);
    List<Comment> findAllByPost(Post post);
}
