package com.telerikacademy.socialalien.configurations;

import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.repositories.UserRepositoryJdbcImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private UserRepositoryJdbcImpl userRepository;

    @Autowired
    public CustomMethodSecurityExpressionRoot(Authentication authentication, Environment env) {

        super(authentication);
        this.userRepository = new UserRepositoryJdbcImpl(env);
    }

    public boolean isPrincipal(int id) {
        User user = userRepository.getById(id);
        return user.getUsername().equals(authentication.getName());
    }

    @Override
    public void setFilterObject(Object filterObject) {
        throw new NotImplementedException();
    }

    @Override
    public Object getFilterObject() {
        return null;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        throw new NotImplementedException();
    }

    @Override
    public Object getReturnObject() {
        return null;
    }

    @Override
    public Object getThis() {
        return null;
    }
}
