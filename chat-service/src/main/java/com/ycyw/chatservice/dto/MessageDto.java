package com.ycyw.chatservice.dto;

import java.time.Instant;

public record MessageDto(
        String id,
        String channelId,
        String from,
        String content,
        Instant timestamp
) {}

