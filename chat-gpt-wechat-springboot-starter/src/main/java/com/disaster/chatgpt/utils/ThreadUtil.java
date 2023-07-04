package com.disaster.chatgpt.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ThreadUtil {
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static ConcurrentHashMap<String,Runnable> runnableConcurrentHashMap = new ConcurrentHashMap<String,Runnable>();


    public static void run(Runnable runnable){
        runnableConcurrentHashMap.putIfAbsent(Thread.currentThread().getId()+"",runnable);
        executor.execute(runnable);
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("executor begin destroy");
            executor.shutdown();
        }));
    }
}
