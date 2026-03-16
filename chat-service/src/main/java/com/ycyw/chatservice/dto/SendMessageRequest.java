package com.ycyw.chatservice.dto;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
        @NotBlank String from,
        @NotBlank String content
) {}

