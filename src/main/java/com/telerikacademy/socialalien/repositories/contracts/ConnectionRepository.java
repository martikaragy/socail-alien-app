package com.telerikacademy.socialalien.repositories.contracts;

import com.telerikacademy.socialalien.models.Connection;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.enums.ConnectionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection,Integer> {

    List<Connection> getConnectionBySenderUser(User senderUser);

    Page<Connection> findAllByReceiverUserAndConnectionStatus(User senderUser, ConnectionType connectionStatus, Pageable pageable);

    List<Connection> getConnectionByReceiverUser(User receiverUser);

    Optional<Connection> findBySenderUserAndReceiverUser(User senderUser, User receiverUser);
    List<Connection> findAllByConnectionStatus(ConnectionType connectionStatus);
}
