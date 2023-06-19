package com.disaster.chatgpt.resource;

import cn.zhouyafeng.itchat4j.Wechat;
import com.disaster.chatgpt.adapter.QQAdapter;
import com.disaster.chatgpt.adapter.WechatAdapter;
import com.disaster.chatgpt.configcenter.Directory;
import com.disaster.chatgpt.infrastructure.share.HttpResult;
import com.disaster.chatgpt.infrastructure.utils.LRUCache;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.cssxsh.mirai.device.MiraiDeviceGenerator;
import xyz.cssxsh.mirai.tool.FixProtocolVersion;

@RestController
@RequestMapping("/resource/chat/gpt/account")
@Slf4j
public class AccountResource {

    @Autowired
    private QQAdapter qqAdapter;
    @Autowired
    private WechatAdapter wechatAdapter;

    /**
     * qq账号密码登录
     *
     * @param userNum
     * @param password
     * @return
     */
    @GetMapping("/qq/v1/login")
    public HttpResult login(@RequestParam("userNum") Long userNum, @RequestParam("password") String password) {
        Bot bot = null;
        try {
            if (Directory.BOT_CACHE.containsKey(userNum + ":" + password)) {
                return HttpResult.success(String.format("成功登录账号为 %s 的qq", userNum));
            }
            bot = BotFactory.INSTANCE.newBot(userNum, password.trim(), new BotConfiguration() {{
                setProtocol(MiraiProtocol.ANDROID_PHONE);
                setHeartbeatStrategy(HeartbeatStrategy.STAT_HB);
                setLoginCacheEnabled(true);
                setDeviceInfo(bot1 -> new MiraiDeviceGenerator().generate());//引用插件生成随机信息
//                setLoginSolver(); 覆盖登录解决器,在登录时可能遇到图形验证码或滑动验证码所使用
            }});
            //使用临时修复插件
            FixProtocolVersion.update();
            bot.login();
            log.info("成功登录账号为 {} 的qq", userNum);
            //订阅监听事件
            bot.getEventChannel().registerListenerHost(qqAdapter);
        } catch (Exception e) {
            return HttpResult.fail(String.format("登录失败，失败原因 %s", e.getMessage()));
        }
        Directory.BOT_CACHE.put(userNum + ":" + password, bot);
        return HttpResult.success(String.format("成功登录账号为 %s 的qq", userNum));
    }

    /**
     * aa扫码登录
     *
     * @param userNum
     * @return
     */
    @GetMapping("/qq/v2/login")
    public HttpResult login(@RequestParam("userNum") Long userNum) {
        Bot bot = null;
        try {
            if (Directory.BOT_CACHE.containsKey(userNum.toString())) {
                return HttpResult.success(String.format("成功登录账号为 %s 的qq", userNum));
            }
            bot = BotFactory.INSTANCE.newBot(userNum, BotAuthorization.byQRCode(), configuration -> configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH));
            //使用临时修复插件
            FixProtocolVersion.update();
            bot.login();
            log.info("成功登录账号为 {} 的qq", userNum);
            //订阅监听事件
            bot.getEventChannel().registerListenerHost(qqAdapter);
        } catch (Exception e) {
            return HttpResult.fail(String.format("登录失败，失败原因 %s", e.getMessage()));
        }
        Directory.BOT_CACHE.put(userNum.toString(), bot);
        return HttpResult.success(String.format("成功登录账号为 %s 的qq", userNum));
    }

    /**
     * 微信扫码登录
     *
     * @param path
     * @return
     */
    @GetMapping("/wechat/login")
    public HttpResult login(@RequestParam("path") @Nullable String path) {
        Wechat wechatBot = new Wechat(wechatAdapter, StringUtils.isEmpty(path) ? "./" : path);
        wechatBot.start();
        return HttpResult.success("登录成功！");
    }

}
