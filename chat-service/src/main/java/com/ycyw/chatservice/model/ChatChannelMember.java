package com.ycyw.chatservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "chat_channel_members",
        uniqueConstraints = @UniqueConstraint(name = "uk_channel_member", columnNames = {"channel_id", "username"}),
        indexes = @Index(name = "idx_members_channel", columnList = "channel_id")
)
public class ChatChannelMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel_id", nullable = false, length = 200)
    private String channelId;

    @Column(name = "username", nullable = false, length = 200)
    private String username;

    public ChatChannelMember() {}

    public ChatChannelMember(String channelId, String username) {
        this.channelId = channelId;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

