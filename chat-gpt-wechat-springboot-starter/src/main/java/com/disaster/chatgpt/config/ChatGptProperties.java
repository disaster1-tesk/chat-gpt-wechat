package com.disaster.chatgpt.config;

import com.disaster.chatgpt.infrastructure.client.function.KeyStrategyFunction;
import com.disaster.chatgpt.infrastructure.client.interceptor.OpenAiAuthInterceptor;
import lombok.*;
import okhttp3.Interceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("chat.gpt.config")
@Data
public class ChatGptProperties {
    private Okhttp okhttp = new Okhttp();
    private String proxy;
    private Type type = Type.DEFAULT;
    private List<String> apikey;
    private Class<? extends OpenAiAuthInterceptor> interceptor;
    private Class<? extends KeyStrategyFunction> strategy;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Proxy {
        private String host;
        private int port;
        private int callTimeout = 30;
        private int connectTimeout = 30;
        private int readTimeout = 30;
        private int writeTimeout = 30;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Okhttp {
        private Proxy proxy;
        private List<Class<? extends Interceptor>> interceptors;
    }


    public static enum Type {
        STREAM("stream"), DEFAULT("default");
        private String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
