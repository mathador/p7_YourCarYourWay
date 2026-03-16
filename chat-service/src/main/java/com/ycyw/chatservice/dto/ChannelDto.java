package com.ycyw.chatservice.dto;

import java.util.Set;

public record ChannelDto(
        String id,
        Set<String> members
) {}

