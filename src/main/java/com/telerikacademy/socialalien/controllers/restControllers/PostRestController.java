package com.telerikacademy.socialalien.controllers.restControllers;

import com.telerikacademy.socialalien.models.Post;
import com.telerikacademy.socialalien.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    private PostService postService;

    @Autowired
    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<Post> getAll() {

        return postService.getAll();
    }

    @GetMapping("/{id}")
    public Post getById(@Valid @PathVariable int id) {
        return postService.findById(id);
    }

    @PostMapping("/create")
    public Post create(@Valid @RequestBody Post post) {
        postService.create(post);
        return postService.findById(post.getId());
    }

    @PutMapping
    public Post update(@RequestBody Post post) {
        postService.update(post);
        return postService.findById(post.getId());
    }

    @DeleteMapping
    public Post delete(@RequestBody Post post) {
        postService.delete(post);
        return postService.findById(post.getId());
    }

}
