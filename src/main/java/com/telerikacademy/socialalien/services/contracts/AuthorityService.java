package com.telerikacademy.socialalien.services.contracts;

import com.telerikacademy.socialalien.models.Authority;
import com.telerikacademy.socialalien.models.User;

import java.util.List;

public interface AuthorityService {
    List<Authority> getByUser(User user);
    Authority createAuthority(Authority authorityDto);
    Authority updateAuthority(int authorityId, Authority authorityDto);
    void deleteAuthority(int id);
}
