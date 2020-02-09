package com.telerikacademy.socialalien.models.dtos;

import javax.validation.constraints.NotNull;

public class ConnectionDto {

    @NotNull
    private int senderId;
    @NotNull
    private int receiverId;
    @NotNull
    private String connectionStatus;

    public ConnectionDto() {
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
}
