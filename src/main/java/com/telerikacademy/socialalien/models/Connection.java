package com.telerikacademy.socialalien.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telerikacademy.socialalien.models.enums.ConnectionType;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;


@Entity
@Where(clause = "deletedStatus <> 'DELETED'")
@Table (name= "connections")
public class Connection extends BaseClass{

    @NotNull
    @JsonIgnore
    @OneToOne
    @JoinColumn(name="senderUser_id")
    private User senderUser;

    @NotNull
    @OneToOne
    @JsonIgnore
    @JoinColumn(name="receiverUser_id")
    private User receiverUser;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="connectionStatus")
    private ConnectionType connectionStatus;

    public Connection() {
    }

    public Connection(ConnectionType connectionStatus) {
       this.connectionStatus = connectionStatus;
    }

    public User getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(User senderUser) {
        this.senderUser = senderUser;
    }

    public User getReceiverUser() {
        return receiverUser;
    }

    public void setReceiverUser(User receiverUser) {
        this.receiverUser = receiverUser;
    }

    public ConnectionType getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionType connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Connection that = (Connection) o;
        return senderUser.equals(that.senderUser) &&
                receiverUser.equals(that.receiverUser) &&
                connectionStatus == that.connectionStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(senderUser, receiverUser, connectionStatus);
    }
}
