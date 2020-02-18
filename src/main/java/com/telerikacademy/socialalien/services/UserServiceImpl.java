package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.exceptions.DuplicateEntityException;
import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.exceptions.InvalidInputException;
import com.telerikacademy.socialalien.exceptions.OperationNotAllowedException;
import com.telerikacademy.socialalien.models.*;
import com.telerikacademy.socialalien.models.dtos.UserDto;
import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import com.telerikacademy.socialalien.repositories.contracts.UserRepository;
import com.telerikacademy.socialalien.services.contracts.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");

    private UserRepository userRepository;
    private AuthorityService authorityService;
    private PostService postService;
    private CommentService commentService;
    private ConnectionService connectionService;
    private PasswordEncoder passwordEncoder;
    private CityService cityService;
    private VisibilityService visibilityService;
    private LikeService likeService;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            AuthorityService authorityService,
            ConnectionService connectionService,
            CommentService commentService,
            PasswordEncoder passwordEncoder,
            CityService cityService,
            PostService postService,
            VisibilityService visibilityService,
            @Lazy LikeService likeService) {
        this.userRepository = userRepository;
        this.authorityService = authorityService;
        this.connectionService = connectionService;
        this.commentService = commentService;
        this.passwordEncoder = passwordEncoder;
        this.cityService = cityService;
        this.postService = postService;
        this.visibilityService = visibilityService;
        this.likeService = likeService;
    }

    @Override
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(int id){
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getUserFriends(int id){
        List<Connection> friendConnections = connectionService.getAllConnectionsOfUserByConnectionStatus(id, "APPROVED");
        List<User> groupOne = friendConnections.stream()
                .map(connection -> connection.getReceiverUser())
                .filter(user -> user.getId()!=id)
                .collect(Collectors.toList());
        List<User> groupTwo = friendConnections.stream()
                .map(connection -> connection.getSenderUser())
                .filter(user -> user.getId()!=id)
                .collect(Collectors.toList());
        groupOne.addAll(groupTwo);
        return groupOne.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<User> getPeopleRequestingFriendship(int id){
        List<Connection> friendConnections = connectionService.getAllConnectionsOfUserByConnectionStatus(id, "REQUESTED");
       return friendConnections.stream()
                .filter(connection -> connection.getReceiverUser().getId() == id)
               .map(connection -> connection.getSenderUser())
               .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public User createUser(UserDto userDto) {
        City cityOfBirth = null;
        City cityOfResidence = null;

        User user = new User();

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        String password = userDto.getPassword();
        if (password.contains(" ")) {
            throw new InvalidInputException("Password should not contain whitespaces.");
        }
        if (!password.equals(userDto.getPasswordConfirmation())) {
            throw new InvalidInputException("Passwords don't match.");
        }
        password = passwordEncoder.encode(password);
        user.setPassword(password);
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setAddress(userDto.getAddress());
        setBirthDate(userDto, user);

        cityOfBirth = setCityOfBirth(cityOfBirth, user, userDto.getCityOfBirth(), userDto.getPlanetOfCityOfBirth());
        cityOfResidence = setCityOfResidence(cityOfResidence, user, userDto.getCityOfResidence(), userDto.getPlanetOfCityOfResidence());

        user.setJobTitle(userDto.getJobTitle());
        user.setEducationLevel(userDto.getEducationLevel());
        user.setDeletedStatus(DeletedStatusType.ACTIVE);

        try {
            user = userRepository.save(user);
        }catch(DataIntegrityViolationException e){
            throw new DuplicateEntityException(String.format("User with username %s already exists.", userDto.getUsername()));
        }

        if(cityOfBirth != null){
            cityOfBirth.getNatives().add(user);
            cityService.updateCity(cityOfBirth);
        }
        if(cityOfResidence != null){
            cityOfResidence.getResidents().add(user);
            cityService.updateCity(cityOfResidence);
        }
        user = setProfileVisibility(user);
        user = addUserAuthority(user);
        return user;
    }

    @Transactional
    @Override
    public User updateUser(int userId, UserDto userDto){
        Optional<User> userOptional = userRepository.findById(userId);
        User userDao = userOptional.orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", userId)));
        City cityOfBirth = null;
        City cityOfResidence = null;

        setEmail(userDao, userDto.getEmail());
        userDao.setFirstName(userDto.getFirstName());
        userDao.setLastName(userDto.getLastName());
        userDao.setAddress(userDto.getAddress());
        setBirthDate(userDto, userDao);

        cityOfBirth = setCityOfBirth(cityOfBirth, userDao, userDto.getCityOfBirth(), userDto.getPlanetOfCityOfBirth());
        cityOfResidence = setCityOfResidence(cityOfResidence, userDao, userDto.getCityOfResidence(), userDto.getPlanetOfCityOfResidence());

        userDao.setJobTitle(userDto.getJobTitle());
        userDao.setEducationLevel(userDto.getEducationLevel());

        userDao = userRepository.save(userDao);

        if(cityOfBirth != null) {
            cityOfBirth.getNatives().add(userDao);
            cityService.updateCity(cityOfBirth);

        }
        if(cityOfResidence != null) {
            cityOfResidence.getResidents().add(userDao);
            cityService.updateCity(cityOfResidence);
        }
        return userDao;
    }

    @Override
    public void updateUser(User user) {
        if (userRepository.findById(user.getId()).orElseThrow(() -> new EntityNotFoundException("Not found")) != null) {
            userRepository.save(user);
        } else {
            throw new EntityNotFoundException(String.format("User %d does not exist", user.getId()));
        }

    }

    @Transactional
    @Override
    public void deleteUser(int id){
        if (id == 1) {
            throw new OperationNotAllowedException("User with id 1 cannot be deleted.");
        }
        Optional <User> userOptional = userRepository.findById(id);
        User userDao = userOptional.orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", id)));
        List<Authority> userAuthorities = authorityService.getByUser(userDao);
        for (Authority authority: userAuthorities) {
            authorityService.deleteAuthority(authority.getId());
        }

        Visibility visibility = visibilityService.getVisibilityForUser(userDao).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d has no visibility settings.", id)));
        visibilityService.delete(visibility.getId());

        List<Connection> userConnections = connectionService.getAllbyUserId(id);
        userConnections.forEach(
                connection -> connectionService.delete(connection));

        List<Comment> userComments = commentService.getAllByUser(userDao);
        userComments.forEach(comment -> commentService.delete(comment));

        List<Like> userLikes = likeService.findAllByUser(userDao);
        userLikes.forEach(like -> likeService.delete(like));

        List<Post> userPosts = userDao.getPosts();
        userPosts.forEach(post -> postService.delete(post));

        userDao.setDeletedStatus(DeletedStatusType.DELETED);
        userDao.setDateDeleted(Timestamp.from(Instant.now()));
        updateUser(userDao);
    }

    @Override
    public Set<User> getUsersBySubstring(String substring) {
        List<User> users= userRepository.findAllByFirstNameContains(substring);
        users.addAll(userRepository.findAllByLastNameContains(substring));
        users.addAll(userRepository.findAllByEmailContains(substring));
        users.addAll(userRepository.findAllByUsernameContains(substring));
        users = users.stream().distinct().collect(Collectors.toList());
        Set<User> updatedUsers = new HashSet<>();
        for (User user : users) {
            for (Authority authority : authorityService.getByUser(user)) {
                if (authority.getAuthority().equals("ROLE_USER")) {
                    updatedUsers.add(user);
                }
            }
        }
        return updatedUsers;
    }

    private void setBirthDate(UserDto userDto, User user) {
        String birthDate = userDto.getBirthDate();
        if (birthDate != null && birthDate.length() != 0) {
            try {
                LocalDate bDate = LocalDate.parse(birthDate, dateTimeFormatter);
                user.setDate(bDate);
            } catch (DateTimeParseException e) {
                throw new InvalidInputException("Invalid date.");
            }
        }
    }

    private City setCityOfResidence(City cityOfResidence, User user, String nameOfCityOfResidence, String planetNameOfCityOfResidence) {
        if (nameOfCityOfResidence != null && nameOfCityOfResidence.length() != 0) {
            if (planetNameOfCityOfResidence == null || planetNameOfCityOfResidence.length() == 0) {
                throw new InvalidInputException(String.format("Name of planet for city with name %s cannot be empty.", nameOfCityOfResidence));
            }
            cityOfResidence = cityService.getOrCreateCityWithNameAndPlanet(nameOfCityOfResidence, planetNameOfCityOfResidence);
            user.setCityOfResidence(cityOfResidence);
        }
        return cityOfResidence;
    }

    private City setCityOfBirth(City cityOfBirth, User user, String nameOfCityOfBirth, String planetNameOfCityOfBirth) {
        if (nameOfCityOfBirth != null && nameOfCityOfBirth.length() != 0) {
            if (planetNameOfCityOfBirth == null || planetNameOfCityOfBirth.length() == 0) {
                throw new InvalidInputException(String.format("Name of planet for city with name %s cannot be empty.", nameOfCityOfBirth));
            }
            cityOfBirth = cityService.getOrCreateCityWithNameAndPlanet(nameOfCityOfBirth, planetNameOfCityOfBirth);

            user.setCityOfBirth(cityOfBirth);
        }
        return cityOfBirth;
    }

    private void setEmail(User userDao, String email) {
        if (email != null && email.length() != 0) {
            if (!emailPattern.matcher(email).matches()) {
                throw new InvalidInputException("Invalid email.");
            }
            userDao.setEmail(email);
        }
    }

    private User addUserAuthority(User finalizedUser) {
        Authority authority = new Authority();
        authority.setAuthority("ROLE_USER");
        authority.setUsername(finalizedUser.getUsername());
        authority.setUser(finalizedUser);
        authorityService.createAuthority(authority);
        finalizedUser.getAuthorities().add(authority);
        return userRepository.save(finalizedUser);
    }

    private User setProfileVisibility(User user) {
        Visibility userProfileVisibility = new Visibility();
        userProfileVisibility.setUser(user);
        userProfileVisibility = visibilityService.create(userProfileVisibility);
        user.setVisibility(userProfileVisibility);
        user = userRepository.save(user);
        return user;
    }

}

