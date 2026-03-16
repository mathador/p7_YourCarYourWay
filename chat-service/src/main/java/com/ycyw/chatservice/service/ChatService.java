package com.ycyw.chatservice.service;

import com.ycyw.chatservice.dto.MessageDto;
import com.ycyw.chatservice.dto.SessionDto;
import com.ycyw.chatservice.model.ChatMessage;
import com.ycyw.chatservice.model.ChatSession;
import com.ycyw.chatservice.repository.ChatMessageRepository;
import com.ycyw.chatservice.repository.ChatSessionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    public ChatService(
            ChatSessionRepository sessionRepository,
            ChatMessageRepository messageRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    public List<SessionDto> listSessions() {
        return sessionRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public SessionDto createSession(Integer userId, String guestName, String countryCode) {
        var session = new ChatSession(UUID.randomUUID(), userId, guestName, countryCode);
        return toDto(sessionRepository.save(session));
    }

    public SessionDto getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
    }

    public MessageDto sendMessage(UUID sessionId, Integer senderId, String senderUsername, String content, String languageCode) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        var msg = new ChatMessage(sessionId, senderId, senderUsername, content, languageCode);
        messageRepository.save(msg);
        return toMessageDto(msg);
    }

    public List<MessageDto> getHistory(UUID sessionId, int limit) {
        var page = PageRequest.of(0, limit);
        var desc = messageRepository.findBySessionIdOrderByTimestampUtcDesc(sessionId, page);
        var asc = desc.reversed();
        return asc.stream()
                .map(this::toMessageDto)
                .toList();
    }

    private SessionDto toDto(ChatSession session) {
        return new SessionDto(
                session.getSessionId(),
                session.getUserId(),
                session.getGuestName(),
                session.getCreatedAt(),
                session.getCountryCode(),
                session.getStatus()
        );
    }

    private MessageDto toMessageDto(ChatMessage m) {
        return new MessageDto(
                m.getId(),
                m.getSessionId(),
                m.getSenderId(),
                m.getSenderUsername(),
                m.getContent(),
                m.getTimestampUtc(),
                m.getLanguageCode()
        );
    }
}
