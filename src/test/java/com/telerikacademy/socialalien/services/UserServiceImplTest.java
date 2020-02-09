package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.exceptions.InvalidInputException;
import com.telerikacademy.socialalien.models.*;
import com.telerikacademy.socialalien.models.dtos.UserDto;
import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import com.telerikacademy.socialalien.repositories.contracts.UserRepository;
import com.telerikacademy.socialalien.services.contracts.*;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;


public class UserServiceImplTest {

    private UserService userService;
    private AuthorityService mockAuthorityService;
    private PasswordEncoder passwordEncoder;
    private UserRepository mockUserRepository;
    private CityService mockCityService;
    private PostService mockPostService;
    private CommentService mockCommentService;
    private ConnectionService mockConnectionService;
    private VisibilityService mockVisibilityService;
    private LikeService mockLikeService;

    @Before
    public void before() {
        passwordEncoder = new BCryptPasswordEncoder();
        mockUserRepository = Mockito.mock(UserRepository.class);
        mockAuthorityService = Mockito.mock(AuthorityService.class);
        mockCityService = Mockito.mock(CityService.class);
        mockPostService = Mockito.mock(PostService.class);
        mockCommentService = Mockito.mock(CommentService.class);
        mockConnectionService = Mockito.mock(ConnectionService.class);
        mockVisibilityService = Mockito.mock(VisibilityService.class);
        mockLikeService = Mockito.mock(LikeService.class);
        userService = new UserServiceImpl(mockUserRepository,
                                          mockAuthorityService,
                                          mockConnectionService,
                                          mockCommentService,
                                          passwordEncoder,
                                          mockCityService,
                                          mockPostService,
                                          mockVisibilityService,
                                          mockLikeService);
    }

    @Test
    public void createUser_Should_ReturnUserWithCorrectAttributes_When_ValidInput() {
        //Arrange
        City city1 = new City("Sofia", new Planet("Earth"));
        City city2 = new City("Nima", new Planet("Mars"));
        Mockito.when(mockCityService.getOrCreateCityWithNameAndPlanet(Mockito.anyString(), Mockito.anyString())).thenReturn(city1).thenReturn(city2);
        Mockito.when(mockUserRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserDto dto = new UserDto("fifo",
                                  "mecho",
                                  "petrov",
                                  "mecho@m.com",
                                  "alabalanica",
                                  "alabalanica",
                                  "Rakovska 1",
                                  "2001-10-03",
                                  "Nifo",
                                  "Saturn",
                                  "Ale",
                                  "Moon",
                                  "student",
                                  "highschool");
        //Act
        User user = userService.createUser(dto);

        //Assert
        Assertions.assertThat(user.getCityOfBirth()).isEqualTo(city1);
        Assertions.assertThat(user.getCityOfResidence()).isEqualTo(city2);
        Assertions.assertThat(user.getPassword()).isNotEqualTo(dto.getPassword());
        Assertions.assertThat(user.getDate()).isEqualTo(LocalDate.of(2001, 10, 3));
    }

    @Test(expected = InvalidInputException.class)
    public void createUser_Should_ThrowException_When_UsernameEmpty() {
        //Arrange
        City city1 = new City("Sofia", new Planet("Earth"));
        City city2 = new City("Nima", new Planet("Mars"));
        Mockito.when(mockCityService.getOrCreateCityWithNameAndPlanet(Mockito.anyString(), Mockito.anyString())).thenReturn(city1).thenReturn(city2);
        UserDto dto = new UserDto("",
                                  "mecho",
                                  "petrov",
                                  "mecho@m.com",
                                  "alabalanica",
                                  "alabalanica",
                                  "Rakovska 1",
                                  "2001-10-03",
                                  "Nifo",
                                  "Saturn",
                                  "Ale",
                                  "Moon",
                                  "student",
                                  "highschool");
        //Act
        User user = userService.createUser(dto);
    }

    //
    @Test(expected = InvalidInputException.class)
    public void createUser_Should_ThrowException_When_EmailEmpty() {
        //Arrange
        City city1 = new City("Sofia", new Planet("Earth"));
        City city2 = new City("Nima", new Planet("Mars"));
        Mockito.when(mockCityService.getOrCreateCityWithNameAndPlanet(Mockito.anyString(), Mockito.anyString())).thenReturn(city1).thenReturn(city2);
        UserDto dto = new UserDto("fifo",
                                  "mecho",
                                  "petrov",
                                  "",
                                  "alabalanica",
                                  "alabalanica",
                                  "Rakovska 1",
                                  "2001-10-03",
                                  "Nifo",
                                  "Saturn",
                                  "Ale",
                                  "Moon",
                                  "student",
                                  "highschool");
        //Act
        User user = userService.createUser(dto);
    }

    @Test(expected = InvalidInputException.class)
    public void createUser_Should_ThrowException_When_PasswordEmpty() {
        //Arrange
        City city1 = new City("Sofia", new Planet("Earth"));
        City city2 = new City("Nima", new Planet("Mars"));
        Mockito.when(mockCityService.getOrCreateCityWithNameAndPlanet(Mockito.anyString(), Mockito.anyString())).thenReturn(city1).thenReturn(city2);
        UserDto dto = new UserDto("fifo",
                                  "mecho",
                                  "petrov",
                                  "mecho@m.com",
                                  "",
                                  "alabalanica",
                                  "Rakovska 1",
                                  "2001-10-03",
                                  "Nifo",
                                  "Saturn",
                                  "Ale",
                                  "Moon",
                                  "student",
                                  "highschool");
        //Act
        User user = userService.createUser(dto);
    }

    @Test(expected = InvalidInputException.class)
    public void createUser_Should_ThrowException_When_PasswordContainsWhitespace() {
        //Arrange
        City city1 = new City("Sofia", new Planet("Earth"));
        City city2 = new City("Nima", new Planet("Mars"));
        Mockito.when(mockCityService.getOrCreateCityWithNameAndPlanet(Mockito.anyString(), Mockito.anyString())).thenReturn(city1).thenReturn(city2);
        UserDto dto = new UserDto("fifo",
                                  "mecho",
                                  "petrov",
                                  "mecho@m.com",
                                  "asa dasa1234",
                                  "asa dasa1234",
                                  "Rakovska 1",
                                  "2001-10-03",
                                  "Nifo",
                                  "Saturn",
                                  "Ale",
                                  "Moon",
                                  "student",
                                  "highschool");
        //Act
        User user = userService.createUser(dto);
    }

    @Test(expected = InvalidInputException.class)
    public void createUser_Should_ThrowException_When_NoMatchPasswords() {
        //Arrange
        City city1 = new City("Sofia", new Planet("Earth"));
        City city2 = new City("Nima", new Planet("Mars"));
        Mockito.when(mockCityService.getOrCreateCityWithNameAndPlanet(Mockito.anyString(), Mockito.anyString())).thenReturn(city1).thenReturn(city2);
        UserDto dto = new UserDto("fifo",
                                  "mecho",
                                  "petrov",
                                  "mecho@m.com",
                                  "alabalanica",
                                  "alabalanicaesuper",
                                  "Rakovska 1",
                                  "2001-10-03",
                                  "Nifo",
                                  "Saturn",
                                  "Ale",
                                  "Moon",
                                  "student",
                                  "highschool");
        //Act
        User user = userService.createUser(dto);
    }

    @Test(expected = InvalidInputException.class)
    public void createUser_Should_ThrowException_When_InvalidBirthDate() {
        //Arrange
        City city1 = new City("Sofia", new Planet("Earth"));
        City city2 = new City("Nima", new Planet("Mars"));
        Mockito.when(mockCityService.getOrCreateCityWithNameAndPlanet(Mockito.anyString(), Mockito.anyString())).thenReturn(city1).thenReturn(city2);
        UserDto dto = new UserDto("fifo",
                                  "mecho",
                                  "petrov",
                                  "mecho@m.com",
                                  "alabalanica",
                                  "alabalanica",
                                  "Rakovska 1",
                                  "20011003",
                                  "Nifo",
                                  "Saturn",
                                  "Ale",
                                  "Moon",
                                  "student",
                                  "highschool");
        //Act
        User user = userService.createUser(dto);
    }

    @Test
    public void updateUser_Should_UpdateUserProperty_When_ValidInput() {
        //Arrange
        Mockito.when(mockUserRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(new User()));
        UserDto userDto = new UserDto("pepo",
                                      "pepi",
                                      "sharkov",
                                      "pepi@gmail.com",
                                      "",
                                      "",
                                      "Sesami Street",
                                      "1967-10-12",
                                      "Bernini",
                                      "Atos",
                                      "Sofia",
                                      "Earth",
                                      "student",
                                      "university");
        City mockCityOfBirth = new City("Bernini", new Planet("Atos"));
        Mockito.when(mockCityService.getOrCreateCityWithNameAndPlanet("Bernini", "Atos"))
               .thenReturn(mockCityOfBirth);
        City mockCityOfResidence = new City("Sofia", new Planet("Earth"));
        Mockito.when(mockCityService.getOrCreateCityWithNameAndPlanet("Sofia", "Earth"))
               .thenReturn(mockCityOfResidence);
        Mockito.when(mockUserRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        //Act
        User user = userService.updateUser(1, userDto);
        //Assert
        Assertions.assertThat(user.getFirstName()).isEqualTo("pepi");
        Assertions.assertThat(user.getLastName()).isEqualTo("sharkov");
        Assertions.assertThat(user.getEmail()).isEqualTo("pepi@gmail.com");
        Assertions.assertThat(user.getAddress()).isEqualTo("Sesami Street");
        Assertions.assertThat(user.getDate()).isEqualTo("1967-10-12");
        Assertions.assertThat(user.getCityOfBirth()).isEqualTo(mockCityOfBirth);
        Assertions.assertThat(user.getCityOfResidence()).isEqualTo(mockCityOfResidence);
        Assertions.assertThat(user.getJobTitle()).isEqualTo("student");
        Assertions.assertThat(user.getEducationLevel()).isEqualTo("university");
    }

    @Test(expected = InvalidInputException.class)
    public void updateUser_Should_ThrowException_When_InvalidEmail() {
        //Arrange
        Mockito.when(mockUserRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(new User()));
        UserDto userDto = new UserDto("pepo",
                                      "",
                                      "",
                                      "pepi@",
                                      "asadasa1234",
                                      "asadasa1234",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "");
        Mockito.when(mockUserRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        //Act
        User user = userService.updateUser(1, userDto);

    }

    @Test(expected = InvalidInputException.class)
    public void updateUser_Should_ThrowException_When_InvalidDate() {
        //Arrange
        Mockito.when(mockUserRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(new User()));
        UserDto userDto = new UserDto("pepo",
                                      "",
                                      "",
                                      "pepi@gmail.com",
                                      "asadasa1234",
                                      "asadasa1234",
                                      "",
                                      "89-01-16",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "");
        Mockito.when(mockUserRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        //Act
        User user = userService.updateUser(1, userDto);

    }

    @Test(expected = InvalidInputException.class)
    public void updateUser_Should_ThrowException_When_UpdateCityWithoutProvidedPlanet() {
        //Arrange
        Mockito.when(mockUserRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(new User()));
        UserDto userDto = new UserDto("pepo",
                                      "",
                                      "",
                                      "pepi@gmail.com",
                                      "asadasa1234",
                                      "asadasa1234",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "Sofia",
                                      "",
                                      "",
                                      "");
        Mockito.when(mockUserRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        //Act
        User user = userService.updateUser(1, userDto);
    }

    @Test
    public void deleteUser_Should_DeleteUser_When_UserExists() {
        //Arrange
        User mockUser = new User();
        mockUser.setId(2);
        Mockito.when(mockUserRepository.findById(2)).thenReturn(Optional.of(mockUser));
        Mockito.when(mockAuthorityService.getByUser(mockUser)).thenReturn(Collections.emptyList());
        Mockito.when(mockVisibilityService.getVisibilityForUser(mockUser)).thenReturn(Optional.of(new Visibility()));
        Mockito.doNothing().when(mockVisibilityService).delete(2);
        Mockito.when(mockConnectionService.getAllbyUserId(2)).thenReturn(Collections.emptyList());
        Mockito.when(mockCommentService.getAllByUser(mockUser)).thenReturn(Collections.emptyList());
        Mockito.when(mockLikeService.findAllByUser(mockUser)).thenReturn(Collections.emptyList());
        Mockito.doNothing().when(mockPostService).delete(Mockito.any(Post.class));
        //Act
        userService.deleteUser(2);
        //Assert
        Assertions.assertThat(mockUser.getDeletedStatus()).isEqualTo(DeletedStatusType.DELETED);
    }
}