package com.ycyw.chatservice.store;

import com.ycyw.chatservice.dto.ChannelDto;
import com.ycyw.chatservice.dto.MessageDto;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryChatStore {

    private static final int DEFAULT_MAX_MESSAGES_PER_CHANNEL = 500;

    private final int maxMessagesPerChannel;
    private final ConcurrentMap<String, Deque<MessageDto>> messagesByChannel = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<String>> membersByChannel = new ConcurrentHashMap<>();

    public InMemoryChatStore() {
        this(DEFAULT_MAX_MESSAGES_PER_CHANNEL);
    }

    public InMemoryChatStore(int maxMessagesPerChannel) {
        this.maxMessagesPerChannel = Math.max(1, maxMessagesPerChannel);
    }

    public List<ChannelDto> listChannels() {
        var ids = new ArrayList<>(membersByChannel.keySet());
        Collections.sort(ids);
        return ids.stream()
                .map(this::getChannel)
                .toList();
    }

    public ChannelDto getChannel(String channelId) {
        var members = membersByChannel.getOrDefault(channelId, Collections.emptySet());
        return new ChannelDto(channelId, Set.copyOf(members));
    }

    public ChannelDto ensureChannel(String channelId) {
        membersByChannel.computeIfAbsent(channelId, ignored -> ConcurrentHashMap.newKeySet());
        messagesByChannel.computeIfAbsent(channelId, ignored -> new ArrayDeque<>());
        return getChannel(channelId);
    }

    public ChannelDto join(String channelId, String username) {
        ensureChannel(channelId);
        membersByChannel.get(channelId).add(username);
        return getChannel(channelId);
    }

    public ChannelDto leave(String channelId, String username) {
        ensureChannel(channelId);
        membersByChannel.get(channelId).remove(username);
        return getChannel(channelId);
    }

    public MessageDto appendMessage(String channelId, String from, String content) {
        ensureChannel(channelId);
        var msg = new MessageDto(UUID.randomUUID().toString(), channelId, from, content, Instant.now());
        var deque = messagesByChannel.get(channelId);
        synchronized (deque) {
            deque.addLast(msg);
            while (deque.size() > maxMessagesPerChannel) {
                deque.removeFirst();
            }
        }
        return msg;
    }

    public List<MessageDto> getHistory(String channelId, int limit) {
        ensureChannel(channelId);
        var deque = messagesByChannel.get(channelId);
        var safeLimit = Math.max(1, Math.min(limit, maxMessagesPerChannel));
        synchronized (deque) {
            var result = new ArrayList<MessageDto>(Math.min(safeLimit, deque.size()));
            var it = deque.descendingIterator();
            while (it.hasNext() && result.size() < safeLimit) {
                result.add(it.next());
            }
            Collections.reverse(result);
            return result;
        }
    }
}

