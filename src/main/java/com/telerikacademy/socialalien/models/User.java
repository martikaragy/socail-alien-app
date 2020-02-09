package com.telerikacademy.socialalien.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telerikacademy.socialalien.exceptions.InvalidInputException;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Where(clause = "deletedStatus <> 'DELETED'")
@Table(name="users")
public class User extends BaseClass{

    @NotEmpty(message="Please provide username.")
    @Size(min = 2, max = 50, message = "Username should be between 2 and 50 characters.")
    @Column(name="username")
    private String username;

    @NotEmpty(message="Please provide password.")
    @Column(name = "password")
    @JsonIgnore
    private String password;

    @Size(max = 50, message = "User's first name should be maximum 50 characters.")
    @Column(name="firstName")
    private String firstName;

    @Size(max = 50, message = "User's last name should be maximum 50 characters.")
    @Column(name="lastName")
    private String lastName;

    @Email(message = "Invalid email address.")
    @NotEmpty(message="Please provide email address.")
    @Column(name="email")
    private String email;

    @Size(max = 50, message = "Address should be maximum 50 characters.")
    @Column(name="address")
    private String address;

    @Column(name="birthDate")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name="cityOfBirth")
    private City cityOfBirth;

    @ManyToOne
    @JoinColumn(name="cityOfResidence")
    private City cityOfResidence;

    @Size(max = 50, message = "Job title should be maximum  50 characters.")
    @Column(name="job")
    private String jobTitle;

    @Size(max = 50, message = "Education info should be maximum 50 characters.")
    @Column(name="education")
    private String educationLevel;

    @Column(name="profilePhoto")
    @JsonIgnore
    private String profilePhoto;

    @Column(name="coverPhoto")
    @JsonIgnore
    private String coverPhoto;

    @OneToMany
    @JoinColumn(name="userId")
    @JsonIgnore
    private List<Authority> authorities = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Visibility visibility;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    List<Post> posts = new ArrayList<>();

    public User(){}

    public User(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if(firstName != null && firstName.length() != 0) {
            if (firstName.length() > 50) {
                throw new InvalidInputException("First name should be maximum 50 characters.");
            }
            this.firstName = firstName;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if(lastName != null && lastName.length() != 0) {
            if (lastName.length() > 50) {
                throw new InvalidInputException("Last name should be maximum 50 characters.");
            }
            this.lastName = lastName;
        }

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if(address != null && address.length() != 0) {
            if (address.length() > 50) {
                throw new InvalidInputException("Address should fit in 50 characters at most.");
            }
            this.address = address;
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public City getCityOfBirth() {
        return cityOfBirth;
    }

    public void setCityOfBirth(City cityOfBirth) {
        this.cityOfBirth = cityOfBirth;
    }

    public City getCityOfResidence() {
        return cityOfResidence;
    }

    public void setCityOfResidence(City cityOfResidence) {
        this.cityOfResidence = cityOfResidence;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {

        if(jobTitle!=null && jobTitle.length() != 0) {
            if (jobTitle.length() > 50) {
                throw new InvalidInputException("Job title should fit in 50 characters at most.");
            }
            this.jobTitle = jobTitle;
        }
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        if(educationLevel!=null && educationLevel.length() != 0) {
            if (educationLevel.length() > 50) {
                throw new InvalidInputException("Education description should fit in 50 characters at most.");
            }
            this.educationLevel = educationLevel;
        }
        this.educationLevel = educationLevel;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.length() < 2) {
            throw new InvalidInputException("Username for user cannot be empty.");
        }
        this.username = username;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
