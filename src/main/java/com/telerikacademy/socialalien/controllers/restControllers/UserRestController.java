package com.telerikacademy.socialalien.controllers.restControllers;

import com.telerikacademy.socialalien.models.Connection;
import com.telerikacademy.socialalien.models.Post;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.dtos.UserDto;
import com.telerikacademy.socialalien.services.contracts.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserRestController {
    private UserService userService;
    private UserInfoService userInfoService;

    private PostService postService;
    private VisibilityService visibilityService;

    @Autowired
    public UserRestController(UserService userService,
                              UserInfoService userInfoService,
                              PostService postService,
                              VisibilityService visibilityService) {
        this.userService = userService;
        this.userInfoService = userInfoService;
        this.postService = postService;
        this.visibilityService = visibilityService;

    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

//    @GetMapping("/users/{id}")
//    public ResponseEntity<User> getUserById(@PathVariable int id) {
//        if (id == 1) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User with id 1 is not accessible.");
//        }
//        Optional<User> userOptional = userService.getUserById(id);
//        return userOptional.isPresent() ? ResponseEntity.ok(userOptional.get()) : ResponseEntity.notFound().build();
//    }

    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> userOptional = userService.getUserByUsername(username);
        return userOptional.isPresent() ? ResponseEntity.ok(userOptional.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/users/search/{searchTerm}")
    public Set<User> getUserBySearchTerm(@PathVariable String searchTerm) {
        return this.userService.getUsersBySubstring(searchTerm);
    }

    @GetMapping("/users/{username}/show/info")
    public List<String> getUserInfo(@PathVariable String username) {
        User user =  userService.getUserByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
       return this.userInfoService.getUserInfo(user, visibilityService.getAllPublicVisibility());
    }



    @GetMapping("/users/{id}/posts")
    public List<Post> getUserPosts(@PathVariable int id) {
        User user = userService.getUserById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no user with id %d", id)));
        return postService.getAllByUser(user);
    }

    @GetMapping("/users/{username}/connections/requests")
    public List<User> getUserFriendsRequests(@PathVariable String username) {
        User user = userService.getUserByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no user with username %s", username)));;
        return userService.getPeopleRequestingFriendship(user.getId());
    }

    @GetMapping("/users/{username}/connections/friends")
    public List<User> getUserFriends(@PathVariable String username) {
        User user = userService.getUserByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no user with username %s", username)));;
        return userService.getUserFriends(user.getId());
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PutMapping("/users/{id}")
    public void updateUser(@PathVariable int id, @RequestBody UserDto userDto) {
        userService.updateUser(id, userDto);
    }

    @PutMapping("/users/{username}/update")
    public void updateUserWithUsername(@PathVariable String username, @RequestBody UserDto userDto) {
        User user = userService.getUserByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no user with username %s", username)));
        userService.updateUser(user.getId(), userDto);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id){
        userService.deleteUser(id);
    }

}
