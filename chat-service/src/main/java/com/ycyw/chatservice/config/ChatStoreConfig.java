package com.ycyw.chatservice.config;

import com.ycyw.chatservice.store.InMemoryChatStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatStoreConfig {

    @Bean
    public InMemoryChatStore inMemoryChatStore() {
        return new InMemoryChatStore();
    }
}

