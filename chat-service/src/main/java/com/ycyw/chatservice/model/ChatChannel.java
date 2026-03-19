package com.ycyw.chatservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_channels")
public class ChatChannel {

    @Id
    @Column(name = "id", nullable = false, length = 200)
    private String id;

    public ChatChannel() {}

    public ChatChannel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

