package com.telerikacademy.socialalien.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telerikacademy.socialalien.models.enums.PrivacyType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Entity
@Where(clause = "deletedStatus <> 'DELETED'")
@Table (name= "posts")
public class Post extends BaseClass {

    @NotEmpty(message = "Text of post cannot be empty.")
    @Column(name="value")
    private String value;

    @NotNull
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @JsonIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @JsonIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "post")
    private List<Like> likes;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "privacy")
    private PrivacyType privacyType;



    public Post() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    public PrivacyType getPrivacyType() {
        return privacyType;
    }

    public void setPrivacyType(PrivacyType privacyType) {
        this.privacyType = privacyType;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return value.equals(post.value) &&
                user.equals(post.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, user);
    }
}
