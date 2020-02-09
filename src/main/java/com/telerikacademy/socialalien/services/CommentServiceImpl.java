package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.models.Comment;
import com.telerikacademy.socialalien.models.Post;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import com.telerikacademy.socialalien.repositories.contracts.CommentRepository;
import com.telerikacademy.socialalien.services.contracts.CommentService;
import com.telerikacademy.socialalien.services.contracts.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private PostService postService;

    public CommentServiceImpl(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    @Override
    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> getAllByUser(User user){
        return commentRepository.findAllByUser(user);
    }

    @Override
    public Comment findById(int id) {
        return commentRepository.findById(id).get();
    }
    @Override
    public void delete(Comment comment) {
        Comment commentDao = commentRepository.findById(comment.getId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Comment with id %d does not exist.", comment.getId())));
        commentDao.setDeletedStatus(DeletedStatusType.DELETED);
        commentDao.setDateDeleted(Timestamp.from(Instant.now()));
        commentRepository.save(commentDao);
    }


    @Override
    public void update(Comment comment) {
        Comment commentDao = commentRepository.findById(comment.getId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Comment with id %d does not exist.", comment.getId())));
        if (comment.getValue() != null) {
            commentDao.setValue(comment.getValue());
        }
        if (comment.getPost() != null) {
            commentDao.setPost(comment.getPost());
        }
        if (comment.getUser() != null) {
            commentDao.setUser(comment.getUser());
        }
        if (comment.getDeletedStatus() != null) {
            commentDao.setDeletedStatus(comment.getDeletedStatus());
        }if (comment.getDateDeleted() != null) {
            commentDao.setDateDeleted(comment.getDateDeleted());
        }

        commentRepository.save(commentDao);
    }

    @Transactional
    @Override
    public void create(Comment comment) {
        comment.setDeletedStatus(DeletedStatusType.ACTIVE);
        Post post= comment.getPost();
        post.getComments().add(comment);
        commentRepository.save(comment);
        postService.update(post);

    }

    @Override
    public List<Comment> findAllByPost(Post post) {
       return commentRepository.findAllByPost(post);
    }


}
