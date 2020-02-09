package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.services.contracts.UserInfoService;
import com.telerikacademy.socialalien.services.contracts.UserPrincipalService;
import com.telerikacademy.socialalien.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserPrincipalServiceImpl implements UserPrincipalService {
    private UserService userService;
    private UserInfoService userInfoService;

    @Autowired
    public UserPrincipalServiceImpl(UserService userService, UserInfoService userInfoService) {
        this.userService = userService;
        this.userInfoService = userInfoService;
    }

    public User getUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return userInfoService.getAnonymousUser();
        }
        return userService.getUserByUsername(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with username %s doesn't exist.", authentication.getName())));

    }

}
