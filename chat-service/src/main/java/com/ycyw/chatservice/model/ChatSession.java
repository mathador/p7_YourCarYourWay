package com.ycyw.chatservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_sessions")
public class ChatSession {

    @Id
    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "country_code")
    private String countryCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChatSessionStatus status;

    public ChatSession() {}

    public ChatSession(UUID sessionId, Integer userId, String guestName, String countryCode) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.guestName = guestName;
        this.countryCode = countryCode;
        this.createdAt = Instant.now();
        this.status = ChatSessionStatus.OPEN;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public ChatSessionStatus getStatus() {
        return status;
    }

    public void setStatus(ChatSessionStatus status) {
        this.status = status;
    }
}
