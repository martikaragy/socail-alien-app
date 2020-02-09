package com.telerikacademy.socialalien.services.contracts;

import com.telerikacademy.socialalien.models.Connection;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.enums.ConnectionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConnectionService {

    List<Connection> getAll();

    Connection findById(int id);

    List<Connection> getAllbyUserId(int id);

    Connection getConnectionByUserCouple(User userOne, User userTwo);

    List<Connection> getAllConnectionsOfUserByConnectionStatus(int userId, ConnectionType connectionType);

    List<Connection> getAllConnectionsOfUserByConnectionStatus(int userId, String connectionTypeAsStr);

    Page<Connection> getConnectionByReceiverUserAndConnectionStatus(User senderUser, ConnectionType connectionStatus, Pageable pageable);

    List<Connection> getAllReceivedRequests(User user);

    List<User> getAllUsersThatSentRequestToUser(User user);

    boolean checkIfUsersAreConnected(User user, User userPrincipal);

    Connection create(Connection connection);

    Connection create(int senderId, int receiverId, String connectionStatus);

    Connection create(User user, User userPrincipal);

    Connection update(Connection connection);

    Connection update(int connectionId, String connectionStatus);

    Connection updateStatusToFriends(User user, User userPrincipal);

    void delete(Connection connection);

    void delete(int id);

    void deleteFriendship(User user, User userPrincipal);

    boolean checkIfUsersAreFriends(User user, User userPrincipal);


}
