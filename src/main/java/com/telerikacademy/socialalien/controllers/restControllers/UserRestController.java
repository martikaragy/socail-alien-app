package com.telerikacademy.socialalien.controllers.restControllers;

import com.telerikacademy.socialalien.models.Connection;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.dtos.UserDto;
import com.telerikacademy.socialalien.services.contracts.ConnectionService;
import com.telerikacademy.socialalien.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserRestController {
    private UserService userService;
    private ConnectionService connectionService;

    @Autowired
    public UserRestController(UserService userService, ConnectionService connectionService) {
        this.userService = userService;
        this.connectionService = connectionService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        if (id == 1) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User with id 1 is accessible.");
        }
        Optional<User> userOptional = userService.getUserById(id);
        return userOptional.isPresent() ? ResponseEntity.ok(userOptional.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{id}/connections")
    public List<Connection> getUserFriendsRequests(@PathVariable int id, @RequestParam String connectionType) {
        return connectionService.getAllConnectionsOfUserByConnectionStatus(id, connectionType);
    }

    @PostMapping("/users")
    public void createUser(@Valid @RequestBody UserDto userDto) {
        userService.createUser(userDto);
    }

    @PutMapping("/users/{id}")
    public void updateUser(@PathVariable int id, @RequestBody UserDto userDto) {
        userService.updateUser(id, userDto);

    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id){
        userService.deleteUser(id);
    }

}
