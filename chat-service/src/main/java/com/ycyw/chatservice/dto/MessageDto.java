package com.ycyw.chatservice.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
        Integer id,
        UUID sessionId,
        Integer senderId,
        String senderUsername,
        String content,
        Instant timestampUtc,
        String languageCode
) {}
