package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.exceptions.DuplicateEntityException;
import com.telerikacademy.socialalien.exceptions.InvalidInputException;
import com.telerikacademy.socialalien.models.Authority;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.repositories.contracts.AuthorityRepository;
import com.telerikacademy.socialalien.services.contracts.AuthorityService;
import com.telerikacademy.socialalien.services.contracts.UserService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;


public class AuthorityServiceImplTest {

    private AuthorityRepository mockAuthorityRepository = Mockito.mock(AuthorityRepository.class);

    private UserService mockUserService = Mockito.mock(UserService.class);


    @Test
    public void createAuthority_Should_ReturnAuthorityWithCorrectAttributes_When_ValidInput() {
        //Arrange
        AuthorityService authorityService = new AuthorityServiceImpl(mockAuthorityRepository, mockUserService);
        Authority testAuthority = new Authority();
        testAuthority.setUsername("pinko");
        testAuthority.setAuthority("ROLE_USER");
        Mockito.when(mockUserService.getUserByUsername(Mockito.anyString())).thenReturn(Optional.of(new User()));
        Mockito.when(mockAuthorityRepository.findByUsernameAndAuthority(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(mockAuthorityRepository.save(Mockito.any(Authority.class))).thenReturn(testAuthority);
        //Act
        authorityService.createAuthority(testAuthority);
    }

    @Test(expected = InvalidInputException.class)
    public void createAuthority_Should_ThrowException_When_InvalidAuthorityFieldInput() {
        //Arrange
        AuthorityService authorityService = new AuthorityServiceImpl(mockAuthorityRepository, mockUserService);
        Authority testAuthority = new Authority();
        testAuthority.setUsername("pinko");
        testAuthority.setAuthority("ROLE");
        Mockito.when(mockUserService.getUserByUsername(Mockito.anyString())).thenReturn(Optional.of(new User()));
        Mockito.when(mockAuthorityRepository.findByUsernameAndAuthority(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(mockAuthorityRepository.save(Mockito.any(Authority.class))).thenReturn(testAuthority);
        //Act
        authorityService.createAuthority(testAuthority);
    }

    @Test(expected = DuplicateEntityException.class)
    public void createAuthority_Should_ThrowException_When_AuthorityEntityAlreadyExists() {
        //Arrange
        AuthorityService authorityService = new AuthorityServiceImpl(mockAuthorityRepository, mockUserService);
        Authority testAuthority = new Authority();
        testAuthority.setUsername("pinko");
        testAuthority.setAuthority("ROLE_USER");
        Mockito.when(mockUserService.getUserByUsername(Mockito.anyString())).thenReturn(Optional.of(new User()));
        Mockito.when(mockAuthorityRepository.findByUsernameAndAuthority(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.of(new Authority()));
        Mockito.when(mockAuthorityRepository.save(Mockito.any(Authority.class))).thenReturn(testAuthority);
        //Act
        authorityService.createAuthority(testAuthority);
    }

    @Test(expected = InvalidInputException.class)
    public void createAuthority_Should_ThrowException_When_NullAuthorityObject() {
        //Arrange
        AuthorityService authorityService = new AuthorityServiceImpl(mockAuthorityRepository, mockUserService);
        //Act
        authorityService.createAuthority(null);
    }

    @Test(expected = InvalidInputException.class)
    public void createAuthority_Should_ThrowException_When_InvalidUsername() {
        //Arrange
        AuthorityService authorityService = new AuthorityServiceImpl(mockAuthorityRepository, mockUserService);
        Authority testAuthority = new Authority();
        testAuthority.setAuthority("ROLE_USER");
        //Act
        authorityService.createAuthority(testAuthority);
    }

    @Test(expected = InvalidInputException.class)
    public void createAuthority_Should_ThrowException_When_InvalidAuthority() {
        //Arrange
        AuthorityService authorityService = new AuthorityServiceImpl(mockAuthorityRepository, mockUserService);
        Authority testAuthority = new Authority();
        testAuthority.setUsername("pinko");
        //Act
        authorityService.createAuthority(testAuthority);
    }

    @Test
    public void updateAuthority_Should_UpdateAuthority_When_ValidInput() {
        //Arrange
        AuthorityService authorityService = new AuthorityServiceImpl(mockAuthorityRepository, mockUserService);
        Authority mockAuthorityDto = new Authority();
        mockAuthorityDto.setId(2);
        mockAuthorityDto.setUsername("pinko");
        mockAuthorityDto.setAuthority("ROLE_USER");
        Mockito.when(mockAuthorityRepository.findById(2)).thenReturn(Optional.of(mockAuthorityDto));
        Mockito.when(mockAuthorityRepository.save(Mockito.any(Authority.class))).thenReturn(new Authority());
        //Act
        authorityService.updateAuthority(2, mockAuthorityDto);
        //Assert
        Assertions.assertThat(mockAuthorityDto.getAuthority()).isEqualTo("ROLE_ADMIN");
    }


}
