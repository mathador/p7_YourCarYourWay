package com.ycyw.chatservice.service;

import com.ycyw.chatservice.dto.ChannelDto;
import com.ycyw.chatservice.dto.MessageDto;
import com.ycyw.chatservice.model.ChatChannel;
import com.ycyw.chatservice.model.ChatChannelMember;
import com.ycyw.chatservice.model.ChatMessage;
import com.ycyw.chatservice.repository.ChatChannelMemberRepository;
import com.ycyw.chatservice.repository.ChatChannelRepository;
import com.ycyw.chatservice.repository.ChatMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatChannelRepository channelRepository;
    private final ChatChannelMemberRepository memberRepository;
    private final ChatMessageRepository messageRepository;

    public ChatService(
            ChatChannelRepository channelRepository,
            ChatChannelMemberRepository memberRepository,
            ChatMessageRepository messageRepository
    ) {
        this.channelRepository = channelRepository;
        this.memberRepository = memberRepository;
        this.messageRepository = messageRepository;
    }

    public List<ChannelDto> listChannels() {
        return channelRepository.findAll().stream()
                .map(ch -> toDto(ch.getId()))
                .toList();
    }

    public ChannelDto ensureChannel(String channelId) {
        if (!channelRepository.existsById(channelId)) {
            channelRepository.save(new ChatChannel(channelId));
        }
        return toDto(channelId);
    }

    public ChannelDto joinChannel(String channelId, String username) {
        ensureChannel(channelId);
        if (!memberRepository.existsByChannelIdAndUsernameIgnoreCase(channelId, username)) {
            memberRepository.save(new ChatChannelMember(channelId, username));
        }
        return toDto(channelId);
    }

    public ChannelDto leaveChannel(String channelId, String username) {
        ensureChannel(channelId);
        memberRepository.deleteByChannelIdAndUsernameIgnoreCase(channelId, username);
        return toDto(channelId);
    }

    public MessageDto sendMessage(String channelId, String from, String content) {
        ensureChannel(channelId);
        var now = Instant.now();
        var msg = new ChatMessage(UUID.randomUUID().toString(), channelId, from, content, now);
        messageRepository.save(msg);
        return new MessageDto(msg.getId(), msg.getChannelId(), msg.getFrom(), msg.getContent(), msg.getTimestamp());
    }

    public List<MessageDto> getHistory(String channelId, int limit) {
        ensureChannel(channelId);
        var page = PageRequest.of(0, limit);
        var desc = messageRepository.findByChannelIdOrderByTimestampDesc(channelId, page);
        // API attend l'ordre chronologique croissant
        var asc = desc.reversed();
        return asc.stream()
                .map(m -> new MessageDto(m.getId(), m.getChannelId(), m.getFrom(), m.getContent(), m.getTimestamp()))
                .toList();
    }

    private ChannelDto toDto(String channelId) {
        List<ChatChannelMember> members = memberRepository.findByChannelId(channelId);
        Set<String> usernames = members.stream()
                .map(ChatChannelMember::getUsername)
                .collect(Collectors.toSet());
        return new ChannelDto(channelId, usernames);
    }
}

