package com.disaster.chatgpt.scanner;


import cn.zhouyafeng.itchat4j.Wechat;
import com.disaster.chatgpt.utils.ThreadUtil;
import com.disaster.chatgpt.adapter.QQAdapter;
import com.disaster.chatgpt.adapter.WechatAdapter;
import com.disaster.chatgpt.configcenter.Directory;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import xyz.cssxsh.mirai.device.MiraiDeviceGenerator;
import xyz.cssxsh.mirai.tool.FixProtocolVersion;

import java.util.Scanner;

@Component
@Slf4j
public class SystemScanner implements ApplicationRunner, ApplicationContextAware {
    private static final String[] SERVLET_INDICATOR_CLASSES = new String[]{"javax.servlet.Servlet", "org.springframework.web.context.ConfigurableWebApplicationContext"};

    private static ApplicationContext applicationContext;
    @Autowired
    private QQAdapter qqAdapter;
    @Autowired
    private WechatAdapter wechatAdapter;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        WebApplicationType webApplicationType = deduceFromClasspath();
        if (webApplicationType.equals(WebApplicationType.NONE) || webApplicationType.equals(WebApplicationType.REACTIVE)) {
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("请选择登录方式：");
                System.out.println("1.QQ-账号密码登录");
                System.out.println("2.QQ-扫码登录");
                System.out.println("3.微信-扫码登录");
                int i = sc.nextInt();
                switch (i) {
                    case 1:
                        System.out.println("账号：");
                        String userNum = sc.next();
                        System.out.println("密码：");
                        String password = sc.next();
                        qqLoginHandler(1, Long.parseLong(userNum), password);
                        break;
                    case 2:
                        System.out.println("账号：");
                        String userNum1 = sc.next();
                        qqLoginHandler(2, Long.parseLong(userNum1), null);
                        break;
                    case 3:
                        wechatLoginHandler();
                        break;
                }
            }
        }
    }

    public void qqLoginHandler(int type, Long userNum, String password) {
       ThreadUtil.run(() -> {
            Bot bot = null;
            if (type == 1) {
                try {
                    if (Directory.BOT_CACHE.containsKey(userNum + ":" + password)) {
                        return;
                    }
                    bot = BotFactory.INSTANCE.newBot(userNum, password.trim(), new BotConfiguration() {{
                        fileBasedDeviceInfo();
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
                    log.error(String.format("登录失败，失败原因 %s", e.getMessage()));
                }
                Directory.BOT_CACHE.put(userNum + ":" + password, bot);
                log.info(String.format("成功登录账号为 %s 的qq", userNum));
            } else {
                try {
                    if (Directory.BOT_CACHE.containsKey(userNum.toString())) {
                        return;
                    }
                    bot = BotFactory.INSTANCE.newBot(userNum, BotAuthorization.byQRCode(), configuration -> configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_WATCH));
                    //使用临时修复插件
                    FixProtocolVersion.update();
                    bot.login();
                    log.info("成功登录账号为 {} 的qq", userNum);
                    //订阅监听事件
                    bot.getEventChannel().registerListenerHost(qqAdapter);
                } catch (Exception e) {
                    log.error(String.format("登录失败，失败原因 %s", e.getMessage()));
                }
                Directory.BOT_CACHE.put(userNum.toString(), bot);
                log.info(String.format("成功登录账号为 %s 的qq", userNum));
            }
        });
    }


    public void wechatLoginHandler() {
        ThreadUtil.run(() -> {
            Wechat wechatBot = new Wechat(wechatAdapter, "./");
            wechatBot.start();
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SystemScanner.applicationContext = applicationContext;
    }


    public static WebApplicationType deduceFromClasspath() {
        if (org.springframework.util.ClassUtils.isPresent("org.springframework.web.reactive.DispatcherHandler", (ClassLoader) null) && !org.springframework.util.ClassUtils.isPresent("org.springframework.web.servlet.DispatcherServlet", (ClassLoader) null) && !org.springframework.util.ClassUtils.isPresent("org.glassfish.jersey.servlet.ServletContainer", (ClassLoader) null)) {
            return WebApplicationType.REACTIVE;
        } else {
            String[] var0 = SERVLET_INDICATOR_CLASSES;
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2) {
                String className = var0[var2];
                if (!org.springframework.util.ClassUtils.isPresent(className, (ClassLoader) null) || CollectionUtils.isEmpty(applicationContext.getBeansOfType(ConfigurableWebApplicationContext.class))) {
                    return WebApplicationType.NONE;
                }
            }

            return WebApplicationType.SERVLET;
        }
    }
}
