package com.disaster.chatgpt.adapter;


import com.disaster.chatgpt.service.ChatGptMessageHandler;
import net.mamoe.mirai.contact.MessageTooLargeException;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class QQAdapter implements ListenerHost, InitializingBean {

    @Autowired
    private MessageHandlerDispatcher messageHandlerDispatcher;

    private ChatGptMessageHandler chatGptMessageHandler;

    /**
     * 机器人收到的好友消息的事件
     *
     * @param event
     */
    @EventHandler
    public void onFriendMessageEvent(FriendMessageEvent event) {
        String prompt = event.getMessage().contentToString().trim();
        try {
            event.getSubject().sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(event.getMessage()))
                    .append(chatGptMessageHandler.handlerText(prompt))
                    .build());
        } catch (MessageTooLargeException e) {
            event.getSubject().sendMessage(prompt);
        }
    }

    /**
     * 机器人收到的群消息的事件
     *
     * @param event
     */
    @EventHandler
    public void onGroupMessageEvent(GroupMessageEvent event) {
        if (event.getMessage().contains(new At(event.getBot().getId()))) {
            String prompt = event.getMessage().contentToString().replace("@" + event.getBot().getId(), "").trim();
            try {
                event.getSubject().sendMessage(new MessageChainBuilder()
                        .append(new QuoteReply(event.getMessage()))
                        .append(new At(event.getSender().getId()))
                        .append(chatGptMessageHandler.handlerText(prompt))
                        .build());
            } catch (MessageTooLargeException e) {
                event.getSubject().sendMessage(prompt);
            }
        }
    }

    /**
     * 戳一戳事件
     *
     * @param event
     */
    @EventHandler
    public void onNudgeEvent(NudgeEvent event){
        event.getBot().nudge().sendTo(event.getSubject());
    }


    /**
     * 机器人收到的陌生人消息的事件
     *
     * @param event
     */
    @EventHandler
    public void onStrangerMessageEvent(StrangerMessageEvent event){
        String prompt = event.getMessage().contentToString().trim();
        try {
            event.getSubject().sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(event.getMessage()))
                    .append(chatGptMessageHandler.handlerText(prompt))
                    .build());
        } catch (MessageTooLargeException e) {
            event.getSubject().sendMessage(prompt);
        }
    }

    /**
     * 机器人收到的群临时会话消息的事件
     *
     * @param event
     */
    public void onTempMessageEvent(TempMessageEvent event){
        if (event.getMessage().contains(new At(event.getBot().getId()))) {
            String prompt = event.getMessage().contentToString().replace("@" + event.getBot().getId(), "").trim();
            try {
                event.getSubject().sendMessage(new MessageChainBuilder()
                        .append(new QuoteReply(event.getMessage()))
                        .append(new At(event.getSender().getId()))
                        .append(chatGptMessageHandler.handlerText(prompt))
                        .build());
            } catch (MessageTooLargeException e) {
                event.getSubject().sendMessage(prompt);
            }
        }
    }

    /**
     * 群临时会话消息
     *
     * @param event
     */
    public void onGroupTempMessageEvent(GroupTempMessageEvent event){
        if (event.getMessage().contains(new At(event.getBot().getId()))) {
            String prompt = event.getMessage().contentToString().replace("@" + event.getBot().getId(), "").trim();
            try {
                event.getSubject().sendMessage(new MessageChainBuilder()
                        .append(new QuoteReply(event.getMessage()))
                        .append(new At(event.getSender().getId()))
                        .append(chatGptMessageHandler.handlerText(prompt))
                        .build());
            } catch (MessageTooLargeException e) {
                event.getSubject().sendMessage(prompt);
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.chatGptMessageHandler = messageHandlerDispatcher.dispatcher();
    }
}
