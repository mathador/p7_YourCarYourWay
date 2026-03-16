package com.ycyw.chatservice.repository;

import com.ycyw.chatservice.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByChannelIdOrderByTimestampDesc(String channelId, Pageable pageable);
}

