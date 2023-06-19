# chat-gpt-wechat

> a simple projection

chatgpt intelligent robot integrating qq and wechat

## Installing / Getting started

Starting this project is very simple, you just need to follow the steps

Run in jar:
```shell
git clone https://github.com/disaster1-tesk/chat-gpt-wechat.git
mvn clean install
cd chat-gpt-wechat-springboot-starter
java -jar chat-gpt-wechat-springboot-starter-1.1.0.jar
```
Run in ide:
```shell
git clone https://github.com/disaster1-tesk/chat-gpt-wechat.git
run in ide
```

### Prepare
The startup of the project depends on nacos, so you may need to install nacos,You also need to modify the configuration a little

### Configuration

```yaml
chat:
  gpt:
    config:
      okhttp:
        proxy: #something about okhttp configuration
          host: 127.0.0.1
          port: 7890
        interceptors:
          - com.disaster.chatgpt.infrastructure.client.interceptor.OpenAiResponseInterceptor
          - com.disaster.chatgpt.infrastructure.client.interceptor.DefaultOpenAiAuthInterceptor
      type: STREAM # This configuration determines whether to stream chatgpt Api data back
      apikey: # chat-gpt personel key
        - xxxx-sdafsafs-xxx
      strategy: com.disaster.chatgpt.infrastructure.client.function.KeyRandomStrategy 
      interceptor: com.disaster.chatgpt.infrastructure.client.interceptor.DynamicKeyOpenAiAuthInterceptor
      proxy: yourProxyUrl # if not settting okhttp proxy, it perhaps help you interview chatgpt api

spring:
  application:
    name: chat-gpt-wechat
```

### Run Type

#### Web Type

```java
package com.disaster.chatgpt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class ChatGptWechatApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ChatGptWechatApplication.class);
        builder.headless(false).web(WebApplicationType.SERVLET).run(args);
    }

}

```

#### None Type

```java
package com.disaster.chatgpt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class ChatGptWechatApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ChatGptWechatApplication.class);
        builder.headless(false).web(WebApplicationType.NONE).run(args);
    }

}

```

## Features

* Improve the processing of wechat \qq information
* Supports parsing different types of configuration files
* Supports different configuration centers
* Configure center and start uncoupling

## Links

The implementation of this project depends on the following projects：

itchat4j: https://github.com/yaphone/itchat4j

mirai: https://github.com/mamoe/mirai

Chat-gpt-java: https://github.com/Grt1228/chatgpt-java


## Licensing

"The code in this project is licensed under Apache License."