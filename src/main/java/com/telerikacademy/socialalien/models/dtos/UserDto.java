package com.telerikacademy.socialalien.models.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class UserDto {

    @NotEmpty(message = "Please, provide username.")
    @Size(min = 2, max = 50, message = "Username should be between 2 and 50 characters.")
    private String username;

    @NotNull(message = "Please, provide password.")
    @Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters.")
    private String password;

    @NotNull(message = "Please, confirm password.")
    @Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters.")
    private String passwordConfirmation;

    @Size(max = 50, message = "User's first name should be maximum 50 characters.")
    private String firstName;

    @Size(max = 50, message = "User's last name should be maximum 50 characters.")
    private String lastName;

    @Email(message = "Invalid email address.")
    @NotEmpty(message="Please provide email address.")
    private String email;

    @Size(max = 50, message = "Address should be maximum 50 characters.")
    private String address;

    private String birthDate;

    @Size(max = 50, message = "City name should be maximum 50 characters.")
    private String cityOfBirth;

    @Size(max = 50, message = "Planet name should be maximum 50 characters.")
    private String planetOfCityOfBirth;

    @Size(max = 50, message = "City name should be maximum 50 characters.")
    private String cityOfResidence;

    @Size(max = 50, message = "Planet name should be maximum 50 characters.")
    private String planetOfCityOfResidence;

    @Size(max = 50, message = "Job title should be maximum  50 characters.")
    private String jobTitle;

    @Size(max = 50, message = "Education info should be maximum 50 characters.")
    private String educationLevel;

    public UserDto() {
    }

    public UserDto(String username, String firstName, String lastName, String email, String password, String passwordConfirmation,
                   String address, String date, String cityOfBirth, String planetOfCityOfBirth, String cityOfResidence, String planetOfCityOfResidence, String jobTitle, String educationLevel) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.address = address;
        this.birthDate = date;
        this.cityOfBirth = cityOfBirth;
        this.planetOfCityOfBirth = planetOfCityOfBirth;
        this.cityOfResidence = cityOfResidence;
        this.planetOfCityOfResidence = planetOfCityOfResidence;
        this.jobTitle = jobTitle;
        this.educationLevel = educationLevel;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String date) {
        this.birthDate = date;
    }

    public String getCityOfBirth() {
        return cityOfBirth;
    }

    public void setCityOfBirth(String cityOfBirth) {
        this.cityOfBirth = cityOfBirth;
    }

    public String getCityOfResidence() {
        return cityOfResidence;
    }

    public void setCityOfResidence(String cityOfResidence) {
        this.cityOfResidence = cityOfResidence;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlanetOfCityOfBirth() {
        return planetOfCityOfBirth;
    }

    public void setPlanetOfCityOfBirth(String planetOfCityOfBirth) {
        this.planetOfCityOfBirth = planetOfCityOfBirth;
    }

    public String getPlanetOfCityOfResidence() {
        return planetOfCityOfResidence;
    }

    public void setPlanetOfCityOfResidence(String planetOfCityOfResidence) {
        this.planetOfCityOfResidence = planetOfCityOfResidence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDto userDto = (UserDto) o;
        return username.equals(userDto.username) &&
                email.equals(userDto.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email);
    }
}