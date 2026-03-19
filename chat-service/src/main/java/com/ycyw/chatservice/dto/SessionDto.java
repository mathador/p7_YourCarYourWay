package com.ycyw.chatservice.dto;

import com.ycyw.chatservice.model.ChatSessionStatus;
import java.time.Instant;
import java.util.UUID;

public record SessionDto(
        UUID sessionId,
        Integer userId,
        String guestName,
        Instant createdAt,
        String countryCode,
        ChatSessionStatus status
) {}
