package com.telerikacademy.socialalien.models;

import com.telerikacademy.socialalien.models.enums.DeletedStatusType;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "visibility")
public class Visibility extends BaseClass {

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "username", insertable = false)
    private boolean username;

    @Column(name = "firstName", insertable = false)
    private boolean firstName;

    @Column(name = "lastName", insertable = false)
    private boolean lastName;

    @Column(name = "email", insertable = false)
    private boolean email;

    @Column(name = "address", insertable = false)
    private boolean address;

    @Column(name = "birthDate", insertable = false)
    private boolean date;

    @Column(name = "cityOfBirth", insertable = false)
    private boolean cityOfBirth;

    @Column(name = "cityOfResidence", insertable = false)
    private boolean cityOfResidence;

    @Column(name = "job", insertable = false)
    private boolean jobTitle;

    @Column(name = "education", insertable = false)
    private boolean educationLevel;

    @Column(name = "profilePhoto", insertable = false)
    private boolean profilePhoto;

    @Column(name = "coverPhoto", insertable = false)
    private boolean coverPhoto;

    public Visibility() {
    }

    public Visibility(boolean username,
                      boolean firstName,
                      boolean lastName,
                      boolean email,
                      boolean address,
                      boolean date,
                      boolean cityOfBirth,
                      boolean cityOfResidence,
                      boolean jobTitle,
                      boolean educationLevel,
                      boolean profilePhoto) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.date = date;
        this.cityOfBirth = cityOfBirth;
        this.cityOfResidence = cityOfResidence;
        this.jobTitle = jobTitle;
        this.educationLevel = educationLevel;
        this.profilePhoto = profilePhoto;
    }

    @PrePersist
    public void setDeletedStatusToActive() {
        if (getDeletedStatus() == null) {
            setDeletedStatus(DeletedStatusType.ACTIVE);
        }
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isUsername() {
        return username;
    }

    public void setUsername(boolean username) {
        this.username = username;
    }

    public boolean isFirstName() {
        return firstName;
    }

    public void setFirstName(boolean firstName) {
        this.firstName = firstName;
    }

    public boolean isLastName() {
        return lastName;
    }

    public void setLastName(boolean lastName) {
        this.lastName = lastName;
    }

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public boolean isAddress() {
        return address;
    }

    public void setAddress(boolean address) {
        this.address = address;
    }

    public boolean isDate() {
        return date;
    }

    public void setDate(boolean date) {
        this.date = date;
    }

    public boolean isCityOfBirth() {
        return cityOfBirth;
    }

    public void setCityOfBirth(boolean cityOfBirth) {
        this.cityOfBirth = cityOfBirth;
    }

    public boolean isCityOfResidence() {
        return cityOfResidence;
    }

    public void setCityOfResidence(boolean cityOfResidence) {
        this.cityOfResidence = cityOfResidence;
    }

    public boolean isJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(boolean jobTitle) {
        this.jobTitle = jobTitle;
    }

    public boolean isEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(boolean educationLevel) {
        this.educationLevel = educationLevel;
    }

    public boolean isProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(boolean profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public boolean isCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(boolean coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Visibility that = (Visibility) o;
        return username == that.username &&
                firstName == that.firstName &&
                lastName == that.lastName &&
                email == that.email &&
                address == that.address &&
                date == that.date &&
                cityOfBirth == that.cityOfBirth &&
                cityOfResidence == that.cityOfResidence &&
                jobTitle == that.jobTitle &&
                educationLevel == that.educationLevel &&
                profilePhoto == that.profilePhoto &&
                user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, username, firstName, lastName, email, address, date, cityOfBirth, cityOfResidence, jobTitle, educationLevel, profilePhoto);
    }
}
