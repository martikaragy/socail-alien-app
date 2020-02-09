package com.telerikacademy.socialalien.services.contracts;


import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.dtos.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(int id);
    Optional<User> getUserByUsername(String username);

    Set<User> getUsersBySubstring(String substring);
    User createUser(UserDto userDto);
    User updateUser(int userId, UserDto userDto);
    void updateUser(User user);
    void deleteUser(int id);
}
