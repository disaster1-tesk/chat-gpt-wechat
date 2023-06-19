package com.disaster.chatgpt.configcenter;

import cn.hutool.core.bean.BeanUtil;
import com.disaster.chatgpt.config.ChatGptProperties;
import com.disaster.chatgpt.infrastructure.client.function.KeyStrategyFunction;
import com.disaster.chatgpt.infrastructure.client.interceptor.OpenAiAuthInterceptor;
import com.disaster.chatgpt.infrastructure.utils.LRUCache;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import okhttp3.Interceptor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class Directory implements DisposableBean {
    private static ConcurrentHashMap<String, Object> propertiesCache = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ChatGptProperties> propCache = new ConcurrentHashMap<>();
    public static final LRUCache<String, Bot> BOT_CACHE = new LRUCache<>(10000);

    public static ChatGptProperties refresh(Map<String, Object> map) {
        if (CollectionUtils.isEmpty(propertiesCache)) {
            propertiesCache.putAll(map);
        } else {
            propertiesCache.replaceAll((s, o) -> o = propertiesCache.get(s));
        }
        ChatGptProperties chatGptProperties = generaterChatGptProperties(map);
        propCache.put("", chatGptProperties);
        return chatGptProperties;
    }

    public static ChatGptProperties getPropertiesFromCache() {
        return propCache.get("");
    }

    public static void putPropertiesToCache(ChatGptProperties chatGptProperties) {
        propCache.put("", chatGptProperties);
    }

    @SneakyThrows
    private static ChatGptProperties generaterChatGptProperties(Map<String, Object> map) {
        Map<String, Object> chat = (Map<String, Object>) map.get("chat");
        Map<String, Object> gpt = (Map<String, Object>) chat.get("gpt");
        Map<String, Object> config = (Map<String, Object>) gpt.get("config");
        Map<String, Object> okhttp = (Map<String, Object>) config.get("okhttp");
        ChatGptProperties chatGptProperties = BeanUtil.mapToBean(config, ChatGptProperties.class, true);
        String strategy = (String) config.get("strategy");
        String interceptor = (String) config.get("interceptor");
        if (!StringUtils.isEmpty(strategy)) {
            chatGptProperties.setStrategy((Class<? extends KeyStrategyFunction>) Class.forName(strategy));
        }
        if (!StringUtils.isEmpty(interceptor)) {
            chatGptProperties.setInterceptor((Class<? extends OpenAiAuthInterceptor>) Class.forName(interceptor));
        }
        if (!CollectionUtils.isEmpty(okhttp) && !StringUtils.isEmpty(okhttp.get("interceptors"))) {
            List<String> interceptors = (List<String>) okhttp.get("interceptors");
            List<Class<? extends Interceptor>> classes = Lists.newArrayList();
            for (String string : interceptors) {
                classes.add((Class<? extends Interceptor>) Class.forName(string));
            }
            chatGptProperties.getOkhttp().setInterceptors(classes);
        }
        return chatGptProperties;
    }


    @Override
    public void destroy() throws Exception {
        BOT_CACHE.values().forEach(bot -> {
            bot.close(new RuntimeException());
        });
    }
}
