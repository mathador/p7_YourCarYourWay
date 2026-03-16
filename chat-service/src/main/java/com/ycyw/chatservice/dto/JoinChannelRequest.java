package com.ycyw.chatservice.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinChannelRequest(
        @NotBlank String username
) {}

