package com.disaster.chatgpt.configcenter.nacos.refresh;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.disaster.chatgpt.config.ChatGptProperties;
import com.disaster.chatgpt.configcenter.Directory;
import com.disaster.chatgpt.infrastructure.client.OpenAiClient;
import com.disaster.chatgpt.infrastructure.client.OpenAiStreamClient;
import com.disaster.chatgpt.infrastructure.client.interceptor.OpenAiAuthInterceptor;
import com.disaster.chatgpt.infrastructure.client.interceptor.OpenAiResponseInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.Yaml;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Slf4j
public class NacosPropertiesRefresher extends AbstractListener implements ApplicationRunner, DisposableBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private ConfigService configService;
    @Value("${spring.application.name:chat-gpt-wechat}")
    private String applicationName;
    @Value("${spring.cloud.nacos.config.file-extension:yml}")
    private String fileExtension;
    /**
     * nacos config manager
     */
    private NacosConfigManager nacosConfigManager;
    /**
     * nacos config properties
     */
    private NacosConfigProperties nacosConfigProperties;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        configService.shutDown();
    }


    @Override
    public void receiveConfigInfo(String configInfo) {
        Map<String, Object> load = new Yaml().load(configInfo);
        ChatGptProperties chatGptProperties = Directory.refresh(load);
        log.info("ChatGptProperties = {}", chatGptProperties);
        OpenAiStreamClient openAiStreamClient = reBuildStreamClient(chatGptProperties);
        OpenAiClient openAiClient = reBuildClient(chatGptProperties);
        log.info("openAiStreamClient = {}", openAiStreamClient);
        log.info("openAiClient = {}", openAiClient);
    }

    private OpenAiClient reBuildClient(ChatGptProperties chatGptProperties) {
        OpenAiClient openAiClient = applicationContext.getBean(OpenAiClient.class);
        openAiClient.setApiKey(chatGptProperties.getApikey());
        openAiClient.setKeyStrategy(Objects.nonNull(chatGptProperties.getStrategy()) ? applicationContext.getBean(chatGptProperties.getStrategy()) : null);
        openAiClient.setAuthInterceptor(Objects.nonNull(chatGptProperties.getInterceptor()) ? applicationContext.getBean(chatGptProperties.getInterceptor()) : null);
        openAiClient.setApiHost(chatGptProperties.getProxy());
        OkHttpClient.Builder builder = getBuilder(chatGptProperties);
        Map<String, OpenAiAuthInterceptor> beansOfType = applicationContext.getBeansOfType(OpenAiAuthInterceptor.class);
        if (!CollectionUtils.isEmpty(beansOfType)) {
            beansOfType.values().forEach(bean -> {
                bean.setApiKey(chatGptProperties.getApikey());
            });
        }
        if (Objects.nonNull(chatGptProperties.getOkhttp()) && !CollectionUtils.isEmpty(chatGptProperties.getOkhttp().getInterceptors())) {
            for (Class interceptor : chatGptProperties.getOkhttp().getInterceptors()) {
                Interceptor bean = (Interceptor) applicationContext.getBean(interceptor);
                builder.addInterceptor(bean);
            }
        }
        openAiClient.setOkHttpClient(builder.build());
        openAiClient.buildOpenAiApi();
        return openAiClient;
    }

    @NotNull
    private static OkHttpClient.Builder getBuilder(ChatGptProperties chatGptProperties) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        ChatGptProperties.Okhttp okhttp = chatGptProperties.getOkhttp();
        builder = builder.proxy((Objects.nonNull(okhttp) && Objects.nonNull(okhttp.getProxy()) ?
                        new Proxy(Proxy.Type.HTTP, new InetSocketAddress(okhttp.getProxy().getHost(), okhttp.getProxy().getPort())) : null))
                .addInterceptor(new OpenAiResponseInterceptor())
                .connectTimeout(okhttp.getProxy().getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(okhttp.getProxy().getWriteTimeout(), TimeUnit.SECONDS)
                .callTimeout(okhttp.getProxy().getCallTimeout(), TimeUnit.SECONDS)
                .readTimeout(okhttp.getProxy().getReadTimeout(), TimeUnit.SECONDS);
        return builder;
    }

    @NotNull
    private OpenAiStreamClient reBuildStreamClient(ChatGptProperties chatGptProperties) {
        OpenAiStreamClient openAiStreamClient = applicationContext.getBean(OpenAiStreamClient.class);
        openAiStreamClient.setApiKey(chatGptProperties.getApikey());
        openAiStreamClient.setKeyStrategy(Objects.nonNull(chatGptProperties.getStrategy()) ? applicationContext.getBean(chatGptProperties.getStrategy()) : null);
        openAiStreamClient.setAuthInterceptor(Objects.nonNull(chatGptProperties.getInterceptor()) ? applicationContext.getBean(chatGptProperties.getInterceptor()) : null);
        openAiStreamClient.setApiHost(chatGptProperties.getProxy());
        OkHttpClient.Builder builder = getBuilder(chatGptProperties);
        Map<String, OpenAiAuthInterceptor> beansOfType = applicationContext.getBeansOfType(OpenAiAuthInterceptor.class);
        if (!CollectionUtils.isEmpty(beansOfType)) {
            beansOfType.values().forEach(bean -> {
                bean.setApiKey(chatGptProperties.getApikey());
            });
        }
        if (Objects.nonNull(chatGptProperties.getOkhttp()) && !CollectionUtils.isEmpty(chatGptProperties.getOkhttp().getInterceptors())) {
            for (Class interceptor : chatGptProperties.getOkhttp().getInterceptors()) {
                Interceptor bean = (Interceptor) applicationContext.getBean(interceptor);
                builder.addInterceptor(bean);
            }
        }
        openAiStreamClient.setOkHttpClient(builder.build());
        openAiStreamClient.buildOpenAiApi();
        return openAiStreamClient;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            nacosConfigManager = applicationContext.getBean(NacosConfigManager.class);
            nacosConfigProperties = applicationContext.getBean(NacosConfigProperties.class);
            String activeProfile = "dev";
            if (nacosConfigProperties.getEnvironment().getActiveProfiles().length>0){
                  activeProfile = nacosConfigProperties.getEnvironment().getActiveProfiles()[0];
            }
            final String dataId = applicationName + "-" + activeProfile + "." + fileExtension;
            final String group = nacosConfigProperties.getGroup();
            configService = nacosConfigManager.getConfigService();
            configService.addListener(dataId, group, this);
        } catch (BeansException e) {
            log.info("NacosConfigManager or NacosConfigProperties is not exist");
        }
    }
}
