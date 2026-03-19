package com.ycyw.chatservice.repository;

import com.ycyw.chatservice.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findBySessionIdOrderByTimestampUtcDesc(UUID sessionId, Pageable pageable);
}
