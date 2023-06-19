package com.disaster.chatgpt.service.imp;

import com.disaster.chatgpt.infrastructure.client.OpenAiClient;
import com.disaster.chatgpt.infrastructure.client.entity.chat.ChatCompletion;
import com.disaster.chatgpt.infrastructure.client.entity.chat.ChatCompletionResponse;
import com.disaster.chatgpt.infrastructure.client.entity.chat.Message;
import com.disaster.chatgpt.infrastructure.client.entity.images.ImageResponse;
import com.disaster.chatgpt.infrastructure.client.entity.images.ImageVariations;
import com.disaster.chatgpt.infrastructure.client.entity.whisper.Transcriptions;
import com.disaster.chatgpt.infrastructure.client.entity.whisper.WhisperResponse;
import com.disaster.chatgpt.service.ChatGptMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@Service
public class MessageHandler implements ChatGptMessageHandler {
    @Autowired
    private OpenAiClient client;

    @Override
    public String handlerText(String prompt) {
        //聊天模型：gpt-3.5
        Message message = Message.builder().role(Message.Role.USER).content(prompt).build();
        ChatCompletion chatCompletion = ChatCompletion
                .builder()
                .messages(Arrays.asList(message))
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .build();
        ChatCompletionResponse chatCompletionResponse = client.chatCompletion(chatCompletion);
        StringBuilder sb = new StringBuilder();
        chatCompletionResponse.getChoices().forEach(e -> {
            sb.append(e.getMessage().getContent());
        });
        return sb.toString();
    }

    @Override
    public ImageResponse handlerPicture(File file, ImageVariations imageVariations) {
        return client.variationsImages(file, ImageVariations.builder().build());
    }

    @Override
    public WhisperResponse handlerVoice(File file, Transcriptions transcriptions) {
        if (Objects.isNull(transcriptions)) {
            return client.speechToTextTranslations(file);
        }
        return client.speechToTextTranscriptions(file, transcriptions);
    }
}
