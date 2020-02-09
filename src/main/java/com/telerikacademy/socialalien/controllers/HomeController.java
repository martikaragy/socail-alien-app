package com.telerikacademy.socialalien.controllers;


import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.exceptions.InvalidInputException;
import com.telerikacademy.socialalien.models.Comment;
import com.telerikacademy.socialalien.models.Post;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.dtos.UserDto;
import com.telerikacademy.socialalien.services.contracts.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HomeController {

    private PostService postService;
    private UserService userService;
    private CommentService commentService;
    private UserPrincipalService userPrincipalService;
    private LikeService likeService;

    public HomeController(PostService postService, UserService userService, CommentService commentService, UserPrincipalService userPrincipalService, LikeService likeService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.userPrincipalService = userPrincipalService;
        this.likeService = likeService;
    }

    @ModelAttribute(name="loggedUser")
    public void addAttribute(Model model) {
        model.addAttribute("loggedUser", userPrincipalService.getUserPrincipal());
    }

    @GetMapping("/")
    public String showHomepage(Model model){

        model.addAttribute("ff",null);
        String username = getPrinciple();
        List<Post> posts= postService.createNewsFeed(username);
        model.addAttribute("posts",posts);
        model.addAttribute("newComment", new Comment());
        if(username.equals("anonymousUser"))
            username="user";
        model.addAttribute("user",userService.getUserByUsername(username).get());
        model.addAttribute("post", new Post());
        return "index";
        }

    @PostMapping("/newsfeed/post/{userId}")
    public String createPost(@ModelAttribute Post post, @PathVariable Integer userId, Model model) {
        post.setUser(userService.getUserById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", userId))));
        try {
            postService.create(post);
        } catch (InvalidInputException e) {
            model.addAttribute("exceptionMessage", e.getMessage());
        }
        return "redirect:/";
    }


    @PostMapping("/comment/{principal}/{postId}")
    public String createComment(@ModelAttribute Comment comment, @PathVariable String principal, @PathVariable Integer postId, HttpServletRequest request)
    {

        User user = userService.getUserByUsername(principal).get();
        comment.setUser(userService.getUserById(user.getId()).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", user.getId()))));
        comment.setPost(postService.findById(postId));
        commentService.create(comment);


        return "redirect:/";
    }

    @PostMapping("/newsfeed/like")
    public String LikeOrDislikePost(@RequestParam String user, @RequestParam int post){

        likeService.createLikeOnPost(user,post);

        return "redirect:/";
    }

    @PostMapping("/newsfeed/search")
    public String process(Model model, @RequestParam String sourceText) {
        model.addAttribute("users",userService.getUsersBySubstring(sourceText));
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("exceptionMessage", "");

        String username = getPrinciple();
        List<Post> posts= postService.createNewsFeed(username);
        model.addAttribute("posts",posts);
        model.addAttribute("newComment", new Comment());
        if(username.equals("anonymousUser"))
            username="user";
        model.addAttribute("user",userService.getUserByUsername(username).get());
        model.addAttribute("post", new Post());

        return "index";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleMissingEntity() {
        return "notFound";
    }

    private String getPrinciple() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }


}




