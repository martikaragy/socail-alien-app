package com.telerikacademy.socialalien.services.contracts;


import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.Visibility;

import java.util.List;

public interface UserInfoService {
    User getAnonymousUser();

    List<String> getUserInfoByPrincipal(User user, User userPrincipal);

    void setProfilePhotoToDefault(User user);

    boolean checkIfShowProfilePhoto(User user, User userPrincipal);

    boolean checkIfPageMatchesProfilePage(User user, User userPrincipal);

    boolean isUserAnonymous(User user);

    boolean isUserAdmin(User user);

    List<String> getUserInfo(User user, Visibility visibility);
}
