package com.ycyw.userservice.dto;

public record UserResponse(
        Integer id,
        String username,
        String email,
        String firstName,
        String lastName,
        String role,
        boolean active,
        boolean isPshProfile
) {}
