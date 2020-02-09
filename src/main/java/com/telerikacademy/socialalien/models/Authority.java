package com.telerikacademy.socialalien.models;

import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Where(clause = "deletedStatus <> 'DELETED'")
@Table(name="authorities")
public class Authority extends BaseClass{

    @Column(name="username")
    private String username;

    @Column(name="authority")
    private String authority;

    @ManyToOne
    @JoinColumn(name="userId", referencedColumnName = "id")
    private User user;

    public Authority(){}

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @PrePersist
    public void setDeletedStatusToActive(){
        if (getDeletedStatus() == null) {
            setDeletedStatus(DeletedStatusType.ACTIVE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Authority authority1 = (Authority) o;
        return username.equals(authority1.username) &&
                authority.equals(authority1.authority) &&
                user.equals(authority1.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authority, user);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
