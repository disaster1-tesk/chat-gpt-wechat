package com.disaster.chatgpt.adapter;

import com.disaster.chatgpt.config.ChatGptProperties;
import com.disaster.chatgpt.configcenter.Directory;
import com.disaster.chatgpt.service.ChatGptMessageHandler;
import com.disaster.chatgpt.service.imp.MessageHandler;
import com.disaster.chatgpt.service.imp.StreamMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MessageHandlerDispatcher {
    @Autowired
    private MessageHandler messageHandler;
    @Autowired
    private StreamMessageHandler streamMessageHandler;


    public ChatGptMessageHandler dispatcher() {
        ChatGptProperties propertiesFromCache = Directory.getPropertiesFromCache();
        if (Objects.isNull(propertiesFromCache) || Objects.isNull(propertiesFromCache.getType()) || propertiesFromCache.getType().equals(ChatGptProperties.Type.DEFAULT)) {
            return messageHandler;
        }
        return streamMessageHandler;
    }

}
