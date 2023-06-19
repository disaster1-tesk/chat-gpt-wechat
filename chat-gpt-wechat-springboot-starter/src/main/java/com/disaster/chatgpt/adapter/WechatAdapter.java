package com.disaster.chatgpt.adapter;

import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;
import cn.zhouyafeng.itchat4j.utils.tools.DownloadTools;
import com.disaster.chatgpt.infrastructure.client.OpenAiStreamClient;
import com.disaster.chatgpt.infrastructure.client.entity.images.ImageResponse;
import com.disaster.chatgpt.infrastructure.client.entity.images.ImageVariations;
import com.disaster.chatgpt.infrastructure.client.entity.images.Item;
import com.disaster.chatgpt.infrastructure.client.entity.whisper.WhisperResponse;
import com.disaster.chatgpt.service.ChatGptMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Component
@Slf4j
public class WechatAdapter implements IMsgHandlerFace {
    @Autowired
    private MessageHandlerDispatcher messageHandlerDispatcher;

    public static final String picPrefix = "/src/main/resources/pic";
    public static final String viedoPrefix = "/src/main/resources/viedo";
    public static final String voicePrefix = "/src/main/resources/voice";

    @Override
    public String textMsgHandle(BaseMsg baseMsg) {
        ChatGptMessageHandler dispatcher = messageHandlerDispatcher.dispatcher();
        if (baseMsg.isGroupMsg()) {
            //存在@机器人的消息就向ChatGPT提问
            if (baseMsg.getText().contains("@" + Core.getInstance().getNickName())) {
                //去除@再提问
                String prompt = baseMsg.getText().replace("@" + Core.getInstance().getNickName() + " ", "").trim();
                return dispatcher.handlerText(prompt);
            }
            return null;
        } else {
            return dispatcher.handlerText(baseMsg.getText());
        }
    }

    @Override
    public String picMsgHandle(BaseMsg baseMsg) {
        if (baseMsg.isGroupMsg()) {
            //存在@机器人的消息就向ChatGPT提问
            if (baseMsg.getText().contains("@" + Core.getInstance().getNickName())) {
                //去除@再提问
                log.info("群聊图片开始处理");
                String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());// 这里使用收到图片的时间作为文件名
                String picPath = System.getProperty("user.dir") + picPrefix + File.separator + fileName + ".png"; // 调用此方法来保存图片
                DownloadTools.getDownloadFn(baseMsg, MsgTypeEnum.PIC.getType(), picPath); // 保存图片的路径
                ChatGptMessageHandler dispatcher = messageHandlerDispatcher.dispatcher();
                ImageResponse imageResponse = dispatcher.handlerPicture(new File(picPath), ImageVariations.builder().build());
                List<Item> data = imageResponse.getData();
                StringBuilder sb = new StringBuilder();
                data.forEach(item -> sb.append("优化之后的图片链接：" + item.getUrl() + "\n"));
                return sb.toString();
            }
            return null;
        } else {
            log.info("私聊图片开始处理");
            String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());// 这里使用收到图片的时间作为文件名
            String picPath = System.getProperty("user.dir") + picPrefix + File.separator + fileName + ".png"; // 调用此方法来保存图片
            DownloadTools.getDownloadFn(baseMsg, MsgTypeEnum.PIC.getType(), picPath); // 保存图片的路径
            ChatGptMessageHandler dispatcher = messageHandlerDispatcher.dispatcher();
            ImageResponse imageResponse = dispatcher.handlerPicture(new File(picPath), ImageVariations.builder().build());
            List<Item> data = imageResponse.getData();
            StringBuilder sb = new StringBuilder();
            data.forEach(item -> sb.append("优化之后的图片链接：" + item.getUrl() + "\n"));
            return sb.toString();
        }
    }

    @Override
    public String voiceMsgHandle(BaseMsg baseMsg) {
        if (baseMsg.isGroupMsg()) {
            //存在@机器人的消息就向ChatGPT提问
            if (baseMsg.getText().contains("@" + Core.getInstance().getNickName())) {
                //去除@再提问
                log.info("群聊语音开始处理");
                String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
                String voicePath = System.getProperty("user.dir") + voicePrefix + File.separator + fileName + ".mp3";
                DownloadTools.getDownloadFn(baseMsg, MsgTypeEnum.VOICE.getType(), voicePath);
                ChatGptMessageHandler dispatcher = messageHandlerDispatcher.dispatcher();
                WhisperResponse whisperResponse = dispatcher.handlerVoice(new File(voicePath), null);
                String text = whisperResponse.getText();
                return dispatcher.handlerText(text);
            }
            return null;
        } else {
            log.info("私聊语音开始处理");
            String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());// 这里使用收到图片的时间作为文件名
            String picPath = picPrefix + File.separator + fileName + ".png"; // 调用此方法来保存图片
            DownloadTools.getDownloadFn(baseMsg, MsgTypeEnum.PIC.getType(), picPath); // 保存图片的路径
            ChatGptMessageHandler dispatcher = messageHandlerDispatcher.dispatcher();
            ImageResponse imageResponse = dispatcher.handlerPicture(new File(picPath), ImageVariations.builder().build());
            List<Item> data = imageResponse.getData();
            StringBuilder sb = new StringBuilder();
            data.forEach(item -> sb.append("优化之后的图片链接：" + item.getUrl() + "\n"));
            return sb.toString();
        }
    }

    @Override
    public String viedoMsgHandle(BaseMsg baseMsg) {
        return null;
    }

    @Override
    public String nameCardMsgHandle(BaseMsg baseMsg) {
        return null;
    }

    @Override
    public void sysMsgHandle(BaseMsg baseMsg) {

    }

    @Override
    public String verifyAddFriendMsgHandle(BaseMsg baseMsg) {
        return null;
    }

    @Override
    public String mediaMsgHandle(BaseMsg baseMsg) {
        return null;
    }
}
