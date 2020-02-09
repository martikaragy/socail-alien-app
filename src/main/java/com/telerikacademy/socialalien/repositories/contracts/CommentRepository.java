package com.telerikacademy.socialalien.repositories.contracts;

import com.telerikacademy.socialalien.models.Comment;
import com.telerikacademy.socialalien.models.Post;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment,Integer> {

    List<Comment> findAllByUser(User user);
    List<Comment> findAllByPost(Post post);

    @Query("update Comment c set c.deletedStatus = ?1 where c.id = ?2")
        void mydelete( DeletedStatusType status,int id);

    @Query("update Comment c set c.value = ?1 where c.id = ?2")
    void update(String value,int id);


}
