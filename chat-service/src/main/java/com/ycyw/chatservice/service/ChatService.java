package com.ycyw.chatservice.service;

import com.ycyw.chatservice.dto.ChannelDto;
import com.ycyw.chatservice.dto.MessageDto;
import com.ycyw.chatservice.store.InMemoryChatStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final InMemoryChatStore store;

    public ChatService(InMemoryChatStore store) {
        this.store = store;
    }

    public List<ChannelDto> listChannels() {
        return store.listChannels();
    }

    public ChannelDto ensureChannel(String channelId) {
        return store.ensureChannel(channelId);
    }

    public ChannelDto joinChannel(String channelId, String username) {
        return store.join(channelId, username);
    }

    public ChannelDto leaveChannel(String channelId, String username) {
        return store.leave(channelId, username);
    }

    public MessageDto sendMessage(String channelId, String from, String content) {
        return store.appendMessage(channelId, from, content);
    }

    public List<MessageDto> getHistory(String channelId, int limit) {
        return store.getHistory(channelId, limit);
    }
}

