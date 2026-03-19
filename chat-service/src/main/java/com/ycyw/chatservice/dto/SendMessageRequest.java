package com.ycyw.chatservice.dto;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
        Integer senderId,
        String senderUsername,
        @NotBlank String content,
        String languageCode
) {}
