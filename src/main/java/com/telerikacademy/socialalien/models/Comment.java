package com.telerikacademy.socialalien.models;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;


@Entity
@Where(clause = "deletedStatus <> 'DELETED'")
@Table (name= "comments")
public class Comment extends BaseClass {

    @NotEmpty(message = "Comment cannot be empty.")
    @Column(name="value")
    private String value;

    @NotNull
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    public Comment() {
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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Comment comment = (Comment) o;
        return value.equals(comment.value) &&
                user.equals(comment.user) &&
                post.equals(comment.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, user, post);
    }
}
