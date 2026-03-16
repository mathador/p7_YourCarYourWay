package com.ycyw.chatservice.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateSessionRequest(
        Integer userId,
        String guestName,
        String countryCode
) {}
