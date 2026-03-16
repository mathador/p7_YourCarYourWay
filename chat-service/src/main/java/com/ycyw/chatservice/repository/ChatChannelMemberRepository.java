package com.ycyw.chatservice.repository;

import com.ycyw.chatservice.model.ChatChannelMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatChannelMemberRepository extends JpaRepository<ChatChannelMember, Long> {
    List<ChatChannelMember> findByChannelId(String channelId);
    boolean existsByChannelIdAndUsernameIgnoreCase(String channelId, String username);
    long deleteByChannelIdAndUsernameIgnoreCase(String channelId, String username);
}

