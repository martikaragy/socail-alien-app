package com.telerikacademy.socialalien.services.contracts;

import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.Visibility;

import java.util.Optional;

public interface VisibilityService {

    Visibility getAllPublicVisibility();

    Optional<Visibility> getVisibilityForUser(User user);

    Visibility create(Visibility visibility);

    Visibility update(Visibility visibility);

    void delete(int id);
}
