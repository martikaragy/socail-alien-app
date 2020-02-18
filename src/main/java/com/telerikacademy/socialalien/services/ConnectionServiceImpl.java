package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.exceptions.*;
import com.telerikacademy.socialalien.models.Connection;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.enums.ConnectionType;
import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import com.telerikacademy.socialalien.repositories.contracts.ConnectionRepository;
import com.telerikacademy.socialalien.services.contracts.AuthorityService;
import com.telerikacademy.socialalien.services.contracts.ConnectionService;
import com.telerikacademy.socialalien.services.contracts.UserInfoService;
import com.telerikacademy.socialalien.services.contracts.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    private ConnectionRepository connectionRepository;
    private UserService userService;
    private UserInfoService userInfoService;
    private AuthorityService authorityService;

    public ConnectionServiceImpl(
            ConnectionRepository connectionRepository,
            @Lazy UserService userService,
            @Lazy UserInfoService userInfoService,
            AuthorityService authorityService) {
        this.connectionRepository = connectionRepository;
        this.userService = userService;
        this.userInfoService = userInfoService;
        this.authorityService = authorityService;
    }

    @Override
    public List<Connection> getAll(){
        return connectionRepository.findAll();

    }

    @Override
    public Connection findById(int id) {
        return connectionRepository.findById(id).get();
    }

    @Override
    public List<Connection> getAllbyUserId(int id) {
        User user = userService.getUserById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", id)));
        List<Connection> listBySender= connectionRepository.getConnectionBySenderUser(user);
        List<Connection> listByReciver =connectionRepository.getConnectionByReceiverUser(user);
        listBySender.addAll(listByReciver);
        List<Connection> allConnections= listBySender;
        return allConnections;
    }

    @Override
    public List<Connection> getAllConnectionsOfUserByConnectionStatus(int userId, String connectionTypeAsStr) {
        ConnectionType connectionType = ConnectionType.valueOf(connectionTypeAsStr.toUpperCase());
        if (!(connectionType.equals(ConnectionType.REQUESTED)) && !(connectionType.equals(ConnectionType.APPROVED))) {
            throw new InvalidInputException("Invalid connection status: Valid statuses are: REQUESTED, APPROVED.");
        }
        if (getAllbyUserId(userId) == null) {
            return null;
        }
        List<Connection> connections = getAllbyUserId(userId);
        return connections.stream().filter(connection -> connection.getConnectionStatus().equals(connectionType)).collect(Collectors.toList());
    }

//    @Override
//    public List<Connection> getAllConnectionsOfUserByConnectionStatus(int userId, ConnectionType connectionType) {
//        if (getAllbyUserId(userId) == null) {
//            return null;
//        }
//        List<Connection> connections = getAllbyUserId(userId);
//        return connections.stream().filter(connection -> connection.getConnectionStatus().equals(connectionType)).collect(Collectors.toList());
//    }


    @Override
    public Connection getConnectionByUserCouple(User userOne, User userTwo) {
        List<Connection> connections = new ArrayList<>();
        Connection connectionOneDirection = connectionRepository.findBySenderUserAndReceiverUser(userOne, userTwo).orElseGet(() -> new Connection(ConnectionType.NOTCONNECTED));
        connections.add(connectionOneDirection);
        Connection connectionOtherDirection = connectionRepository.findBySenderUserAndReceiverUser(userTwo, userOne).orElseGet(() -> new Connection(ConnectionType.NOTCONNECTED));
        connections.add(connectionOtherDirection);
        connections = connections.stream().filter(connection -> {
            return connection.getConnectionStatus().equals(ConnectionType.APPROVED) || connection.getConnectionStatus().equals((ConnectionType.REQUESTED));
        }).collect(Collectors.toList());
        if (connections.size() > 1) {
            throw new InvalidDbStateException("Users have two different connection statuses.");
        }
        if (connections.size() == 0) {
            Connection connection = new Connection(ConnectionType.NOTCONNECTED);
            connection.setSenderUser(userOne);
            connection.setReceiverUser(userTwo);
            return connection;
        }
        return connections.get(0);
    }

    @Override
    public List<Connection> getAllReceivedRequests(User user){
        return connectionRepository.getConnectionByReceiverUser(user).stream().filter(connection -> {return connection.getConnectionStatus() == ConnectionType.REQUESTED;}).collect(Collectors.toList());
    }

    @Override
    public List<User> getAllUsersThatSentRequestToUser(User user){
        List<Connection> connections = connectionRepository.getConnectionByReceiverUser(user);
        if (connections.size() == 0) {
            return new ArrayList<>();
        }
        return connections.stream()
                          .filter(connection -> {
                              return connection.getConnectionStatus() == ConnectionType.REQUESTED;
                          })
                          .map(connection -> connection.getSenderUser())
                          .collect(Collectors.toList());
    }

    @Override
    public Page<Connection> getConnectionByReceiverUserAndConnectionStatus(User senderUser, ConnectionType connectionStatus, Pageable pageable) {
        return connectionRepository.findAllByReceiverUserAndConnectionStatus(senderUser, connectionStatus, pageable);
    }

    @Override
    public Connection create(Connection connection) {
        return connectionRepository.save(connection);
    }

    @Override
    public Connection create(int senderId, int receiverId, String connectionStatus) {
        Connection connection = new Connection();
        connection.setSenderUser(userService.getUserById(senderId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id %d does not exist.", senderId))));
        connection.setReceiverUser(userService.getUserById(receiverId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id %d does not exist.", receiverId))));
        connection.setConnectionStatus(ConnectionType.valueOf(connectionStatus.toUpperCase()));
        connection.setDeletedStatus(DeletedStatusType.ACTIVE);
        return connectionRepository.save(connection);
    }

    @Override
    public Connection create(User user, User userPrincipal) {
        Connection connection = getConnectionByUserCouple(user, userPrincipal);
        if (connection.getConnectionStatus() == ConnectionType.REQUESTED || connection.getConnectionStatus() == ConnectionType.APPROVED) {
            throw new DuplicateEntityException(String.format("User with id %d and user with id %d are already connected.", user.getId(), userPrincipal.getId()));
        }
        connection.setConnectionStatus(ConnectionType.REQUESTED);
        connection.setSenderUser(userPrincipal);
        connection.setReceiverUser(user);
        connection.setDeletedStatus(DeletedStatusType.ACTIVE);
        return connectionRepository.save(connection);
    }

    @Override
    public Connection update(Connection connection) {
        Connection updatedConnection = connectionRepository.findById(connection.getId()).orElseThrow(() -> new EntityNotFoundException(String.format("Connection with id %d does not exist.", connection.getId())));
        if (connection.getReceiverUser() != null) {
            updatedConnection.setReceiverUser(connection.getReceiverUser());
        }
        if (connection.getSenderUser() != null) {
            updatedConnection.setSenderUser(connection.getSenderUser());
        }
        if (connection.getConnectionStatus() != null) {
            updatedConnection.setConnectionStatus(connection.getConnectionStatus());
        }
        if (connection.getDeletedStatus() != null) {
            updatedConnection.setDeletedStatus(connection.getDeletedStatus());
        }
        if (connection.getDateDeleted() != null) {
            updatedConnection.setDateDeleted(connection.getDateDeleted());
        }
        if (connection.getComment() != null) {
            connection.setComment(connection.getComment());
        }
        return connectionRepository.save(updatedConnection);

    }

    @Override
    public Connection update(int connectionId, String connectionStatus) {
        Connection connection = connectionRepository.findById(connectionId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Connection with id %d does not exist.", connectionId)));
        connection.setConnectionStatus(ConnectionType.valueOf(connectionStatus.toUpperCase()));
        return connectionRepository.save(connection);
    }

    @Override
    public Connection updateStatusToFriends(User user, User userPrincipal) {
        Connection connection = connectionRepository.findBySenderUserAndReceiverUser(user, userPrincipal).orElseThrow(
                () -> new EntityNotFoundException(String.format("No friend request  from user %s to be approved by user %s.", user.getUsername(), userPrincipal.getUsername())));
        if(connection.getConnectionStatus().equals(ConnectionType.REQUESTED)){
            connection.setConnectionStatus(ConnectionType.APPROVED);
            return connectionRepository.save(connection);
        }
        return connection;
    }

    @Override
    public void delete(Connection connection) {
        Connection connectionDao = connectionRepository.findById(connection.getId()).orElseThrow(() -> new EntityNotFoundException(String.format("Connection with id %d does not exist.", connection.getId())));
        connectionDao.setDeletedStatus(DeletedStatusType.DELETED);
        connectionDao.setDateDeleted(new Timestamp(System.currentTimeMillis()));
        connectionRepository.save(connectionDao);
    }

    @Override
    public void delete(int id) {
        Connection connection = connectionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Connection with id %d does not exist.", id)));
        delete(connection);
    }

    @Override
    public void deleteFriendship(User user, User userPrincipal) {
        Connection connection = getConnectionByUserCouple(user, userPrincipal);
        if (!connection.getConnectionStatus().equals(ConnectionType.APPROVED)) {
            throw new OperationNotAllowedException(String.format(
                    "Connection with status Friends between user with id %d and user with id %d does not exist.", user.getId(), userPrincipal.getId()));
        }
        connection.setDeletedStatus(DeletedStatusType.DELETED);
        connection.setDateDeleted(Timestamp.from(Instant.now()));
        update(connection);
    }

    @Override
    public boolean checkIfUsersAreFriends(User user, User userPrincipal) {
        if (userInfoService.isUserAnonymous(userPrincipal)) {
            return false;
        }
        return getConnectionByUserCouple(user, userPrincipal).getConnectionStatus().equals(ConnectionType.APPROVED);
    }



    @Override
    public boolean checkIfUsersAreConnected(User user, User userPrincipal) {
        if (userInfoService.isUserAnonymous(userPrincipal)) {
            return false;
        }
        return getConnectionByUserCouple(user, userPrincipal).getConnectionStatus().equals(ConnectionType.APPROVED)
                || getConnectionByUserCouple(user, userPrincipal).getConnectionStatus().equals(ConnectionType.REQUESTED);
    }

}
