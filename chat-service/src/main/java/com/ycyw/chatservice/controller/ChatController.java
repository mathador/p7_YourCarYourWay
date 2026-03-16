package com.ycyw.chatservice.controller;

import com.ycyw.chatservice.dto.ChannelDto;
import com.ycyw.chatservice.dto.JoinChannelRequest;
import com.ycyw.chatservice.dto.MessageDto;
import com.ycyw.chatservice.dto.SendMessageRequest;
import com.ycyw.chatservice.service.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/channels")
    public List<ChannelDto> listChannels() {
        return chatService.listChannels();
    }

    @PutMapping("/channels/{channelId}")
    public ChannelDto ensureChannel(@PathVariable @NotBlank String channelId) {
        return chatService.ensureChannel(channelId);
    }

    @PostMapping("/channels/{channelId}/members")
    public ChannelDto joinChannel(
            @PathVariable @NotBlank String channelId,
            @RequestBody @Valid JoinChannelRequest request
    ) {
        return chatService.joinChannel(channelId, request.username());
    }

    @DeleteMapping("/channels/{channelId}/members/{username}")
    public ChannelDto leaveChannel(
            @PathVariable @NotBlank String channelId,
            @PathVariable @NotBlank String username
    ) {
        return chatService.leaveChannel(channelId, username);
    }

    @PostMapping("/channels/{channelId}/messages")
    public MessageDto sendMessage(
            @PathVariable @NotBlank String channelId,
            @RequestBody @Valid SendMessageRequest request
    ) {
        return chatService.sendMessage(channelId, request.from(), request.content());
    }

    @GetMapping("/channels/{channelId}/messages")
    public List<MessageDto> getHistory(
            @PathVariable @NotBlank String channelId,
            @RequestParam(defaultValue = "50") @Min(1) @Max(200) int limit
    ) {
        return chatService.getHistory(channelId, limit);
    }
}