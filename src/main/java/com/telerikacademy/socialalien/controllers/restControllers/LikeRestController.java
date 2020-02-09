package com.telerikacademy.socialalien.controllers.restControllers;

import com.telerikacademy.socialalien.models.Like;
import com.telerikacademy.socialalien.services.contracts.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikeRestController {

    private LikeService likeService;

    @Autowired
    public LikeRestController(LikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping
    public List<Like> getAll() {

        return likeService.getAll();
    }

    @GetMapping("/{id}")
    public Like getById(@Valid @PathVariable int id) {
        return likeService.findById(id);
    }

    @PostMapping("/create")
    public Like createComment(@RequestBody Like like) {
        likeService.create(like);
        return likeService.findById(like.getId());
    }
    @PutMapping
    public Like update(@RequestBody @Valid Like like) {
        likeService.update(like);
        return likeService.findById(like.getId());
    }
    @DeleteMapping
    public Like delete(@RequestBody @Valid Like like) {
        likeService.delete(like);
        return likeService.findById(like.getId());
    }
}
