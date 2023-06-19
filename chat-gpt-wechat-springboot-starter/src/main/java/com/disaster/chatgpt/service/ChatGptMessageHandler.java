package com.disaster.chatgpt.service;

import com.disaster.chatgpt.infrastructure.client.entity.images.ImageResponse;
import com.disaster.chatgpt.infrastructure.client.entity.images.ImageVariations;
import com.disaster.chatgpt.infrastructure.client.entity.whisper.Transcriptions;
import com.disaster.chatgpt.infrastructure.client.entity.whisper.WhisperResponse;

import java.io.File;

public interface ChatGptMessageHandler {
    String handlerText(String message);

    ImageResponse handlerPicture(File file, ImageVariations imageVariations);

    WhisperResponse handlerVoice(File file, Transcriptions transcriptions);

}
