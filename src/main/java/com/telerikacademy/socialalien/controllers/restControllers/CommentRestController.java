package com.telerikacademy.socialalien.controllers.restControllers;


import com.telerikacademy.socialalien.models.Comment;
import com.telerikacademy.socialalien.services.contracts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    private CommentService commentService;

    @Autowired
    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<Comment> getAll() {
        return commentService.getAll();
    }

    @GetMapping("/{id}")
    public Comment getById(@Valid @PathVariable int id) {
        return commentService.findById(id);
    }

    @PostMapping("/create")
    public Comment createComment(@Valid @RequestBody Comment comment) {
        commentService.create(comment);
        return commentService.findById(comment.getId());
    }
    @PutMapping
    public Comment update(@RequestBody Comment comment) {
        commentService.update(comment);
        return commentService.findById(comment.getId());
    }
    @DeleteMapping
    public Comment delete(@RequestBody Comment comment) {
        commentService.delete(comment);
        return commentService.findById(comment.getId());
    }
}
