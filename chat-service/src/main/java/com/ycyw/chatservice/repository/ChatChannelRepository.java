package com.ycyw.chatservice.repository;

import com.ycyw.chatservice.model.ChatChannel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatChannelRepository extends JpaRepository<ChatChannel, String> {
}

