package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.exceptions.InvalidInputException;
import com.telerikacademy.socialalien.models.Authority;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.Visibility;
import com.telerikacademy.socialalien.repositories.contracts.UserRepository;
import com.telerikacademy.socialalien.services.contracts.AuthorityService;
import com.telerikacademy.socialalien.services.contracts.ConnectionService;
import com.telerikacademy.socialalien.services.contracts.UserInfoService;
import com.telerikacademy.socialalien.services.contracts.VisibilityService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    private UserRepository userRepository;
    private ConnectionService connectionService;
    private VisibilityService visibilityService;
    private AuthorityService authorityService;

    public UserInfoServiceImpl(
            UserRepository userRepository,
            ConnectionService connectionService,
            VisibilityService visibilityService,
            AuthorityService authorityService) {
        this.userRepository = userRepository;
        this.connectionService = connectionService;
        this.visibilityService = visibilityService;
        this.authorityService = authorityService;
    }

    @Override
    public User getAnonymousUser() {
        return userRepository.findById(1).orElseThrow(() -> new EntityNotFoundException("Default user with id 1 does not exist."));
    }

    @Override
    public List<String> getUserInfoByPrincipal(User user, User userPrincipal) {
        Visibility userVisibility = visibilityService.getVisibilityForUser(user).orElseThrow(
                () -> new EntityNotFoundException(String.format("No visibility settings for user with id %d.", user.getId())));
        if (isUserAnonymous(userPrincipal)) {
            return getUserInfo(user, userVisibility);
        }
        if (isUserAdmin(userPrincipal)) {
            return getUserInfo(user, visibilityService.getAllPublicVisibility());
        }
        if (connectionService.checkIfUsersAreFriends(user, userPrincipal)) {
            return getUserInfo(user, visibilityService.getAllPublicVisibility());
        } else {
            return getUserInfo(user, userVisibility);
        }
    }

    @Override
    public void setProfilePhotoToDefault(User user) {
        user.setProfilePhoto("profile-default.jpg");
    }

    @Override
    public boolean checkIfShowProfilePhoto(User user, User userPrincipal) {
        if (user.getProfilePhoto() == null) {
            return false;
        }
        boolean isPublic = user.getVisibility().isProfilePhoto();
        if (isPublic) {
            return true;
        }
        return connectionService.checkIfUsersAreFriends(user, userPrincipal);
    }

    @Override
    public boolean checkIfPageMatchesProfilePage(User user, User userPrincipal) {
        if (user.getId() == userPrincipal.getId()) {
            return true;
        }
        return false;
    }


    @Override
    public boolean isUserAnonymous(User user) {
        if (user == null) {
            throw new InvalidInputException("Provided user entity cannot be empty.");
        }
        List<Authority> authorities = authorityService.getByUser(user);
        if (authorities.get(0).getAuthority().equals("ROLE_ANONYMOUS")) {
            return true;
        }
        return false;

    }

    @Override
    public boolean isUserAdmin(User user) {
        if (user == null) {
            throw new InvalidInputException("Provided user entity cannot be empty.");
        }
        List<Authority> authorities = authorityService.getByUser(user);
        if (authorities.get(0).getAuthority().equals("ROLE_ADMIN")) {
            return true;
        }
        return false;
    }

    private List<String> getUserInfo(User user, Visibility visibility) {
        List<String> attributes = new ArrayList<>();

        if (user.getFirstName() != null && visibility.isFirstName()) {
            attributes.add("First name: " + user.getFirstName());
        }
        if (user.getLastName() != null && visibility.isLastName()) {
            attributes.add("Last name: " + user.getLastName());
        }
        if (visibility.isUsername()) {
            attributes.add("Username: " + user.getUsername());
        }

        if (user.getDate() != null && visibility.isDate()) {
            attributes.add("Birth date: " + user.getDate().toString());
        }
        if (visibility.isEmail()) {
            attributes.add("Email: " + user.getEmail());
        }
        if (user.getEducationLevel() != null && visibility.isEducationLevel()) {
            attributes.add("Education level: " + user.getEducationLevel());
        }
        if (user.getJobTitle() != null && visibility.isJobTitle()) {
            attributes.add("Job title: " + user.getJobTitle());
        }

        if (user.getCityOfResidence() != null && visibility.isCityOfResidence()) {
            attributes.add("City of Residence: " + user.getCityOfResidence().getName());
        }
        if (user.getCityOfBirth() != null && visibility.isCityOfBirth()) {
            attributes.add("City of Birth: " + user.getCityOfBirth().getName());
        }
        if (user.getAddress() != null && visibility.isAddress()) {
            attributes.add("Address: " + user.getAddress());
        }
        return attributes;
    }


}
