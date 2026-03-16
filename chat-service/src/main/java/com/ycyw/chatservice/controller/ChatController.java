package com.ycyw.chatservice.controller;

import com.ycyw.chatservice.dto.CreateSessionRequest;
import com.ycyw.chatservice.dto.MessageDto;
import com.ycyw.chatservice.dto.SendMessageRequest;
import com.ycyw.chatservice.dto.SessionDto;
import com.ycyw.chatservice.service.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@Validated
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public Map<String, String> health() {
        return Map.of("status", "ok", "service", "chat-service");
    }

    @GetMapping("/sessions")
    public List<SessionDto> listSessions() {
        return chatService.listSessions();
    }

    @PostMapping("/sessions")
    public SessionDto createSession(@RequestBody @Valid CreateSessionRequest request) {
        return chatService.createSession(request.userId(), request.guestName(), request.countryCode());
    }

    @GetMapping("/sessions/{sessionId}")
    public SessionDto getSession(@PathVariable @NotNull UUID sessionId) {
        return chatService.getSession(sessionId);
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public MessageDto sendMessage(
            @PathVariable @NotNull UUID sessionId,
            @RequestBody @Valid SendMessageRequest request
    ) {
        return chatService.sendMessage(sessionId, request.senderId(), request.senderUsername(), request.content(), request.languageCode());
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public List<MessageDto> getHistory(
            @PathVariable @NotNull UUID sessionId,
            @RequestParam(defaultValue = "50") @Min(1) @Max(200) int limit
    ) {
        return chatService.getHistory(sessionId, limit);
    }
}
