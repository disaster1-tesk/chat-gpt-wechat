package com.disaster.chatgpt.resource;

import com.disaster.chatgpt.infrastructure.client.OpenAiStreamClient;
import com.disaster.chatgpt.infrastructure.client.entity.billing.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource/chat/gpt/")
@Slf4j
public class ChatGptResource {
    @Autowired
    private OpenAiStreamClient client;


    @GetMapping("subscription")
    public String subscription() {
        Subscription subscription = client.subscription();
        log.info("用户名：{}", subscription.getAccountName());
        log.info("用户总余额（美元）：{}", subscription.getHardLimitUsd());
        log.info("更多信息看Subscription类");
        return subscription.getHardLimitUsd()+"";
    }


}
