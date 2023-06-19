package com.disaster.chatgpt.service.imp;

import com.disaster.chatgpt.infrastructure.client.OpenAiStreamClient;
import com.disaster.chatgpt.infrastructure.client.entity.images.ImageResponse;
import com.disaster.chatgpt.infrastructure.client.entity.images.ImageVariations;
import com.disaster.chatgpt.infrastructure.client.entity.whisper.Transcriptions;
import com.disaster.chatgpt.infrastructure.client.entity.whisper.WhisperResponse;
import com.disaster.chatgpt.service.ChatGptMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class StreamMessageHandler implements ChatGptMessageHandler {
    @Autowired
    private OpenAiStreamClient client;

    @Override
    public String handlerText(String message) {
        return null;
    }

    @Override
    public ImageResponse handlerPicture(File file, ImageVariations imageVariations) {
        return null;
    }

    @Override
    public WhisperResponse handlerVoice(File file, Transcriptions transcriptions) {
        return null;
    }
}
