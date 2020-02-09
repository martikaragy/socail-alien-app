package com.telerikacademy.socialalien.controllers;

import com.telerikacademy.socialalien.exceptions.DuplicateEntityException;
import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.exceptions.InvalidDbStateException;
import com.telerikacademy.socialalien.exceptions.OperationNotAllowedException;
import com.telerikacademy.socialalien.models.*;
import com.telerikacademy.socialalien.models.dtos.UserDto;
import com.telerikacademy.socialalien.models.enums.ConnectionType;
import com.telerikacademy.socialalien.services.contracts.*;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
@Controller
public class UserController {
    private UserService userService;
    private UserInfoService userInfoService;
    private UserPrincipalService userPrincipalService;
    private PostService postService;
    private ConnectionService connectionService;
    private CommentService commentService;
    private LikeService likeService;
    private VisibilityService visibilityService;
    private FileService fileService;

    @Autowired
    public UserController(
            UserService userService,
            UserInfoService userInfoService,
            UserPrincipalService userPrincipalService,
            PostService postService,
            ConnectionService connectionService,
            CommentService commentService,
            VisibilityService visibilityService,
            FileService fileService,
            LikeService likeService) {
        this.userService = userService;
        this.userInfoService = userInfoService;
        this.userPrincipalService = userPrincipalService;
        this.postService = postService;
        this.connectionService = connectionService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.visibilityService = visibilityService;
        this.fileService = fileService;
    }

    @GetMapping("/users/{id}")
    public String getUserPage(@PathVariable int id, Model model) {
        User user = userService.getUserById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", id)));
        User userPrincipal = userPrincipalService.getUserPrincipal();
        List<Post> posts = postService.createUserFeed(userPrincipal.getUsername(),id);
        model.addAttribute("posts", posts);
        addAttributes(model, user, userPrincipal);
        return "userPage";
    }

    @PreAuthorize("isPrincipal(#id) or hasRole('ROLE_ADMIN')")
    @GetMapping("/users/{id}/update")
    public String getUserUpdatePage(@PathVariable int id, Model model) {
        User user = userService.getUserById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", id)));
        User userPrincipal = userPrincipalService.getUserPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("loggedUser", userPrincipal);
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("visibilityUpdate", new Visibility());
        model.addAttribute("userVisibility", visibilityService.getVisibilityForUser(user).orElse(new Visibility()));
        return "userAdminPage";
    }

    @PreAuthorize("isPrincipal(#id)")
    @GetMapping("/users/{id}/requests")
    public String getFriendsRequestsPage(@PathVariable int id, Model model, @PageableDefault(size = 10) Pageable pageable) {
        User user = userService.getUserById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", id)));
        User userPrincipal = userPrincipalService.getUserPrincipal();
        model.addAttribute("loggedUser", userPrincipal);
        model.addAttribute("user", user);
        model.addAttribute("isProfilePage", userInfoService.checkIfPageMatchesProfilePage(user, userPrincipal));
        model.addAttribute("page", connectionService.getConnectionByReceiverUserAndConnectionStatus(user, ConnectionType.REQUESTED, pageable));
        return "friendRequestsPage";
    }

    @PreAuthorize("isPrincipal(#userId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/post/update/{userId}/{postId}")
    public String getUpdatePostPage(@ModelAttribute Post post, @PathVariable int userId, @PathVariable int postId, Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("postDao", postService.findById(postId));
        model.addAttribute("userDao", userService.getUserById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id %d does not exist.", userId))));
        model.addAttribute("postId", postId);
        model.addAttribute("userId", userId);
        return "updatePostPage";
    }

    @PreAuthorize("isPrincipal(#commentUserId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/comment/update/{commentId}/{commentUserId}/{postId}")
    public String getUpdateCommentPage(
            @ModelAttribute Comment comment,
            @PathVariable int commentId,
            @PathVariable int commentUserId,
            @PathVariable int postId,
            Model model) {
        model.addAttribute("comment", new Comment());
        model.addAttribute("commentId", commentId);
        model.addAttribute("postId", postId);
        model.addAttribute("commentUserId", commentUserId);
        model.addAttribute("commentDao", commentService.findById(commentId));
        model.addAttribute("postDao", postService.findById(postId));
        model.addAttribute("userDaoComment", userService.getUserById(commentUserId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id %d does not exist.", commentUserId))));
        return "updateCommentPage";
    }

    @PreAuthorize("isPrincipal(#id)")
    @PostMapping("/users/{id}/send/request/{receiverId}")
    public String createConnection(@PathVariable int id, @PathVariable int receiverId, Model model) {
        User userPrincipal = userPrincipalService.getUserPrincipal();
        User user = userService.getUserById(receiverId).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", id)));
        model.addAttribute("posts", postService.createUserFeed(userPrincipal.getUsername(),user.getId()));
        try {
            connectionService.create(user, userPrincipal);
        } catch (DuplicateEntityException e) {
            model.addAttribute("exceptionMessage", e.getMessage());
        }
        addAttributes(model, user, userPrincipal);
        return "userPage";
    }

    @PreAuthorize("isPrincipal(#userId)")
    @PostMapping("/post/{userId}")
    public String createPost(@ModelAttribute Post post, @PathVariable Integer userId) {
        post.setUser(userService.getUserById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", userId))));
        postService.create(post);
        return "redirect:/users/" + userId;
    }

    @PreAuthorize("isPrincipal(#userId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/post/update/{userId}/{postId}")
    public String updatePost(@ModelAttribute Post post, @PathVariable int userId, @PathVariable int postId, Model model) {
        try {
            postService.update(post);
        } catch (EntityNotFoundException e) {
            model.addAttribute("exceptionMessage", e.getMessage());
            model.addAttribute("postId", postId);
            model.addAttribute("userId", userId);
            return "updatePostPage";
        }
        return "redirect:/";
    }

    @PreAuthorize("isPrincipal(#commentUserId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/comment/update/{commentId}/{commentUserId}/{postId}")
    public String updateComment(
            @ModelAttribute Comment comment,
            @PathVariable int commentId,
            @PathVariable int commentUserId,
            @PathVariable int postId,
            Model model) {
        try {
            commentService.update(comment);
        } catch (EntityNotFoundException e) {
            model.addAttribute("exceptionMessage", e.getMessage());
            model.addAttribute("comment", new Comment());
            model.addAttribute("commentId", commentId);
            model.addAttribute("postId", postId);
            model.addAttribute("commentUserId", commentUserId);
            model.addAttribute("commentDao", commentService.findById(commentId));
            model.addAttribute("postDao", postService.findById(postId));
            model.addAttribute("userDaoComment", userService.getUserById(commentUserId).orElseThrow(
                    () -> new EntityNotFoundException(String.format("User with id %d does not exist.", commentUserId))));
            return "updateCommentPage";
        }
        return "redirect:/";
    }

    @PreAuthorize("isPrincipal(#id)")
    @PostMapping("/users/{id}/unfriend/{receiverId}")
    public String deleteConnection(@PathVariable int id, @PathVariable int receiverId, Model model) {
        User userPrincipal = userPrincipalService.getUserPrincipal();
        User user = userService.getUserById(receiverId).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", id)));
        try {
            connectionService.deleteFriendship(user, userPrincipal);
            model.addAttribute("message", "You have unfriended user " + user.getUsername());
            addAttributes(model, user, userPrincipal);
            model.addAttribute("posts", postService.createUserFeed(userPrincipal.getUsername(),user.getId()));
        } catch (OperationNotAllowedException e) {
            model.addAttribute("exceptionMessage", e.getMessage());
        }
        return "userPage";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/comment/{principal}/{postId}/{userId}")
    public String createComment(@ModelAttribute Comment comment, @PathVariable String principal, @PathVariable Integer postId, @PathVariable Integer userId)
    {

        User user = userService.getUserByUsername(principal).get();
        comment.setUser(userService.getUserById(user.getId()).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", user.getId()))));
        comment.setPost(postService.findById(postId));
        commentService.create(comment);
        return "redirect:/users/" + userId.toString();
    }

    @PreAuthorize("isPrincipal(#id) or hasRole('ROLE_ADMIN')")
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
        } catch (OperationNotAllowedException e) {
            redirectAttributes.addFlashAttribute("exceptionMessage", e.getMessage());
            return "redirect:/users/" + id;
        }
        return "redirect:/";
    }

    @PreAuthorize("isPrincipal(#userId) or hasRole('ROLE_ADMIN')")
    @GetMapping("/delete/post/get/{postId}/{userId}")
    public String deletePostWithGet(Model model, @PathVariable Integer postId, @PathVariable Integer userId) {
        postService.delete(postService.findById(postId));
        User user;
        User userPrincipal;
        try {
            user = userService.getUserById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", userId)));
            userPrincipal = userPrincipalService.getUserPrincipal();
            List<Post> posts = postService.createUserFeed(userPrincipal.getUsername(), userId);

            model.addAttribute("posts", posts);
        } catch (RuntimeException e) {
            return "error";
        }
        addAttributes(model, user, userPrincipal);
        return "userPage";
    }

    @PreAuthorize("isPrincipal(#userId) or hasRole('ROLE_ADMIN')")
    @PostMapping("/delete/post/{postId}/{userId}")
    public String deletePost(Model model, @PathVariable Integer postId, @PathVariable Integer userId) {
        postService.delete(postService.findById(postId));
        User user;
        User userPrincipal;
        try {
            user = userService.getUserById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", userId)));
            userPrincipal = userPrincipalService.getUserPrincipal();
            List<Post> posts = postService.createUserFeed(userPrincipal.getUsername(),userId);

            model.addAttribute("posts", posts);
        } catch (RuntimeException e) {
            return "error";
        }
        addAttributes(model, user, userPrincipal);
        return "userPage";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/like")
    public String LikeOrDislikePost(@RequestParam String user, @RequestParam int post){

        likeService.createLikeOnPost(user,post);
        return "redirect:/users/" + postService.findById(post).getUser().getId();
    }

    @PreAuthorize("isPrincipal(#userId)")
    @PostMapping("/users/{id}/update/image")
    public String uploadUserImage(@PathVariable("id") int userId, @RequestParam("file") MultipartFile file, Model model) {
        try {
            fileService.store(file, userId);
            model.addAttribute("message", "Successful upload!");
        } catch (IOException e) {
            model.addAttribute("exceptionMessage", e.getMessage());
        }
        User user = userService.getUserById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", userId)));
        User userPrincipal = userPrincipalService.getUserPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("loggedUser", userPrincipal);
        model.addAttribute("visibilityUpdate", new Visibility());
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("userVisibility", visibilityService.getVisibilityForUser(user).orElse(new Visibility()));
        return "userAdminPage";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/users/{id}/update/image/default")
    public String setUserImageToDefault(@PathVariable("id") int userId, Model model) {
        User user = userService.getUserById(userId).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", userId)));
        User userPrincipal = userPrincipalService.getUserPrincipal();
        try {
            userInfoService.setProfilePhotoToDefault(user);
        } catch (RuntimeException e) {
            return "error";
        }
        model.addAttribute("user", user);
        model.addAttribute("loggedUser", userPrincipal);
        model.addAttribute("visibilityUpdate", new Visibility());
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("userVisibility", visibilityService.getVisibilityForUser(user).orElse(new Visibility()));
        return "userAdminPage";
    }

    @PreAuthorize("isPrincipal(#id) or hasRole('ROLE_ADMIN')")
    @PostMapping("/users/{id}/update")
    public String updateUserInfo(@PathVariable int id, Model model, @ModelAttribute UserDto userDto) {
        User user = null;
        User userPrincipal = null;
        try {
            userService.updateUser(id, userDto);
        } catch (RuntimeException e) {
            model.addAttribute("exceptionMessage", e.getMessage());
        }
        user = userService.getUserById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", id)));
        userPrincipal = userPrincipalService.getUserPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("loggedUser", userPrincipal);
        model.addAttribute("visibilityUpdate", new Visibility());
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("userVisibility", visibilityService.getVisibilityForUser(user).orElse(new Visibility()));
        model.addAttribute("message", "Update successful!");
        return "userAdminPage";
    }


    @PreAuthorize("isPrincipal(#id) or hasRole('ROLE_ADMIN')")
    @PostMapping("/users/{id}/update/visibility")
    public String updateUserVisibility(@PathVariable int id, Model model, @ModelAttribute Visibility visibility) {
        try {
            visibilityService.update(visibility);
        } catch (HibernateException e) {
            model.addAttribute("exceptionMessage", e.getMessage());
        }
        User user = userService.getUserById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", id)));
        User userPrincipal = userPrincipalService.getUserPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("loggedUser", userPrincipal);
        model.addAttribute("visibilityUpdate", new Visibility());
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("userVisibility", visibilityService.getVisibilityForUser(user).orElse(new Visibility()));
        model.addAttribute("message", "Update successful!");
        return "userAdminPage";
    }

    @PreAuthorize("isPrincipal(#id)")
    @PostMapping("/users/{id}/requests/{senderId}/approve")
    public String updateConnection(@PathVariable int id, @PathVariable int senderId, Model model, @PageableDefault(size = 10) Pageable pageable) {

        User userPrincipal = userPrincipalService.getUserPrincipal();
        User user = userService.getUserById(senderId).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", id)));
        try {
            connectionService.updateStatusToFriends(user, userPrincipal);
            model.addAttribute("loggedUser", userPrincipal);
            model.addAttribute("isProfilePage", userInfoService.checkIfPageMatchesProfilePage(user, userPrincipal));
            model.addAttribute("message", "You are now friends with user " + user.getUsername());
            model.addAttribute("page", connectionService.getConnectionByReceiverUserAndConnectionStatus(user, ConnectionType.REQUESTED, pageable));
        } catch (RuntimeException e) {
            model.addAttribute("exceptionMessage", e.getMessage());
        }
        return "friendRequestsPage";
    }

    private void addAttributes(Model model, User user, User userPrincipal) {
        model.addAttribute("user", user);
        model.addAttribute("loggedUser", userPrincipal);
        model.addAttribute("list", userInfoService.getUserInfoByPrincipal(user, userPrincipal));
        model.addAttribute("isProfilePage", userInfoService.checkIfPageMatchesProfilePage(user, userPrincipal));
        model.addAttribute("showPhoto", userInfoService.checkIfShowProfilePhoto(user, userPrincipal));
        model.addAttribute("isConnected", connectionService.checkIfUsersAreConnected(user, userPrincipal));
        model.addAttribute("isRequestedByPrincipal", connectionService.getConnectionByUserCouple(user, userPrincipal).getSenderUser().getId() == userPrincipal.getId());
        model.addAttribute("isRequestedFromUser", connectionService.getConnectionByUserCouple(user, userPrincipal).getSenderUser().getId() == user.getId());
        model.addAttribute("isFriends", connectionService.checkIfUsersAreFriends(user, userPrincipal));
        int requestsCount = connectionService.getAllUsersThatSentRequestToUser(user).size();
        model.addAttribute("requestsCountInt", requestsCount);
        model.addAttribute("requestsCountStr", String.valueOf(requestsCount));
        model.addAttribute("post", new Post());
        model.addAttribute("newComment" ,new Comment());
        model.addAttribute("like", new Like());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleMissingEntity() {
        return "notFound";
    }

    @ExceptionHandler(InvalidDbStateException.class)
    public String handleInvalidDbState() {
        return "error";
    }
}
