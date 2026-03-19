package com.ycyw.chatservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "chat_messages",
        indexes = {
                @Index(name = "idx_messages_session_ts", columnList = "session_id,timestamp_utc")
        }
)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "sender_id")
    private Integer senderId;

    @Column(name = "sender_username")
    private String senderUsername;

    @Column(name = "content", nullable = false, length = 4000)
    private String content;

    @Column(name = "timestamp_utc", nullable = false)
    private Instant timestampUtc;

    @Column(name = "language_code")
    private String languageCode;

    public ChatMessage() {}

    public ChatMessage(UUID sessionId, Integer senderId, String senderUsername, String content, String languageCode) {
        this.sessionId = sessionId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.content = content;
        this.timestampUtc = Instant.now();
        this.languageCode = languageCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimestampUtc() {
        return timestampUtc;
    }

    public void setTimestampUtc(Instant timestampUtc) {
        this.timestampUtc = timestampUtc;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}
