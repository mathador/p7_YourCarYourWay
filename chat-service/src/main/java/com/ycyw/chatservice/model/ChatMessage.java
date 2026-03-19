package com.ycyw.chatservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(
        name = "chat_messages",
        indexes = {
                @Index(name = "idx_messages_channel_ts", columnList = "channel_id,timestamp")
        }
)
public class ChatMessage {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "channel_id", nullable = false, length = 200)
    private String channelId;

    @Column(name = "from_username", nullable = false, length = 200)
    private String from;

    @Column(name = "content", nullable = false, length = 4000)
    private String content;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    public ChatMessage() {}

    public ChatMessage(String id, String channelId, String from, String content, Instant timestamp) {
        this.id = id;
        this.channelId = channelId;
        this.from = from;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}

