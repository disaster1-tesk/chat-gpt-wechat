package com.disaster.chatgpt.Utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static ConcurrentHashMap<String,Runnable> runnableConcurrentHashMap = new ConcurrentHashMap<String,Runnable>();


    public static void run(Runnable runnable){
        runnableConcurrentHashMap.putIfAbsent(Thread.currentThread().getId()+"",runnable);
        executor.execute(runnable);
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
        }));
    }
}
