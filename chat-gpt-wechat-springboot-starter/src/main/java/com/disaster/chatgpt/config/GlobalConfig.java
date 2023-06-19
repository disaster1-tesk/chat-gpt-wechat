package com.disaster.chatgpt.config;

import com.disaster.chatgpt.configcenter.nacos.autoconfig.NacosAutoConfig;
import com.disaster.chatgpt.infrastructure.client.function.KeyRandomStrategy;
import com.disaster.chatgpt.infrastructure.client.interceptor.DefaultOpenAiAuthInterceptor;
import com.disaster.chatgpt.infrastructure.client.interceptor.DynamicKeyOpenAiAuthInterceptor;
import com.disaster.chatgpt.infrastructure.client.interceptor.OpenAILogger;
import com.disaster.chatgpt.infrastructure.client.interceptor.OpenAiResponseInterceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(ChatGptProperties.class)
@AutoConfigureBefore(NacosAutoConfig.class)
public class GlobalConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public KeyRandomStrategy keyRandomStrategy(){
        return new KeyRandomStrategy();
    }

    @Bean
    public DefaultOpenAiAuthInterceptor defaultOpenAiAuthInterceptor(ChatGptProperties chatGptProperties){
        DefaultOpenAiAuthInterceptor defaultOpenAiAuthInterceptor = new DefaultOpenAiAuthInterceptor();
        defaultOpenAiAuthInterceptor.setApiKey(chatGptProperties.getApikey());
        defaultOpenAiAuthInterceptor.setKeyStrategy(applicationContext.getBean(chatGptProperties.getStrategy()));
        return defaultOpenAiAuthInterceptor;
    }

    @Bean
    public DynamicKeyOpenAiAuthInterceptor dynamicKeyOpenAiAuthInterceptor(ChatGptProperties chatGptProperties){
        DynamicKeyOpenAiAuthInterceptor dynamicKeyOpenAiAuthInterceptor = new DynamicKeyOpenAiAuthInterceptor();
        dynamicKeyOpenAiAuthInterceptor.setApiKey(chatGptProperties.getApikey());
        dynamicKeyOpenAiAuthInterceptor.setKeyStrategy(applicationContext.getBean(chatGptProperties.getStrategy()));
        return dynamicKeyOpenAiAuthInterceptor;
    }

    @Bean
    public HttpLoggingInterceptor httpLoggingInterceptor(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        //！！！！千万别再生产或者测试环境打开BODY级别日志！！！！
        //！！！生产或者测试环境建议设置为这三种级别：NONE,BASIC,HEADERS,！！！
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return httpLoggingInterceptor;
    }

    @Bean
    public OpenAiResponseInterceptor openAiResponseInterceptor(){
        OpenAiResponseInterceptor openAiResponseInterceptor = new OpenAiResponseInterceptor();
        return openAiResponseInterceptor;
    }


}
