package com.disaster.chatgpt.configcenter.nacos.autoconfig;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.disaster.chatgpt.config.ChatGptProperties;
import com.disaster.chatgpt.configcenter.nacos.refresh.NacosPropertiesRefresher;
import com.disaster.chatgpt.infrastructure.client.OpenAiClient;
import com.disaster.chatgpt.infrastructure.client.OpenAiStreamClient;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Configuration
public class NacosAutoConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;


    @Bean
    public OkHttpClient okHttpClient(ChatGptProperties chatGptProperties) {
        OkHttpClient.Builder builder = new OkHttpClient
                .Builder();
        ChatGptProperties.Okhttp okhttp = chatGptProperties.getOkhttp();
        builder =builder
                .proxy(Objects.nonNull(okhttp) && Objects.nonNull(okhttp.getProxy()) ?
                        new Proxy(Proxy.Type.HTTP, new InetSocketAddress(okhttp.getProxy().getHost(), okhttp.getProxy().getPort())) : null)
                .connectTimeout(okhttp.getProxy().getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(okhttp.getProxy().getWriteTimeout(), TimeUnit.SECONDS)
                .callTimeout(okhttp.getProxy().getCallTimeout(), TimeUnit.SECONDS)
                .readTimeout(okhttp.getProxy().getReadTimeout(), TimeUnit.SECONDS);
        if (Objects.nonNull(chatGptProperties.getOkhttp()) && !CollectionUtils.isEmpty(chatGptProperties.getOkhttp().getInterceptors())) {
            for (Class interceptor : chatGptProperties.getOkhttp().getInterceptors()) {
                Interceptor bean = (Interceptor) applicationContext.getBean(interceptor);
                builder.addInterceptor(bean);
            }
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnBean(value = OkHttpClient.class, name = "okHttpClient")
    public OpenAiClient openAiClient(ChatGptProperties chatGptProperties) {
        return OpenAiClient.builder()
                .apiKey(chatGptProperties.getApikey())
                .apiHost(StringUtils.isEmpty(chatGptProperties.getProxy()) ? null : chatGptProperties.getProxy())
                .authInterceptor(Objects.nonNull(chatGptProperties.getInterceptor()) ? applicationContext.getBean(chatGptProperties.getInterceptor()) : null)
                .keyStrategy(Objects.nonNull(chatGptProperties.getStrategy()) ? applicationContext.getBean(chatGptProperties.getStrategy()) : null)
                .okHttpClient(okHttpClient(chatGptProperties))
                .build();
    }


    @Bean
    @ConditionalOnBean(value = OkHttpClient.class, name = "okHttpClient")
    public OpenAiStreamClient openAiStreamClient(ChatGptProperties chatGptProperties) {
        return OpenAiStreamClient.builder()
                .apiKey(chatGptProperties.getApikey())
                .apiHost(StringUtils.isEmpty(chatGptProperties.getProxy()) ? null : chatGptProperties.getProxy())
                .authInterceptor(Objects.nonNull(chatGptProperties.getInterceptor()) ? applicationContext.getBean(chatGptProperties.getInterceptor()) : null)
                .keyStrategy(Objects.nonNull(chatGptProperties.getStrategy()) ? applicationContext.getBean(chatGptProperties.getStrategy()) : null)
                .okHttpClient(okHttpClient(chatGptProperties))
                .build();
    }

    @Bean
    @ConditionalOnBean(value = OpenAiStreamClient.class, name = "openAiStreamClient")
    public NacosPropertiesRefresher nacosPropertiesRefresher() {
        return new NacosPropertiesRefresher();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
