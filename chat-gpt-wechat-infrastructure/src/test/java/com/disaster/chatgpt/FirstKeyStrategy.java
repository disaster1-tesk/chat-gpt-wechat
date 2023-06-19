package com.disaster.chatgpt;


import com.disaster.chatgpt.infrastructure.client.function.KeyStrategyFunction;

import java.util.List;

/**
 * 描述：自定义的key使用策略
 * 总是使用第一个key
 *
 * @author https:www.unfbx.com
 * @date 2023-04-03
 */
public class FirstKeyStrategy implements KeyStrategyFunction<List<String>, String> {

    /**
     * 总是使用第一个
     * @param keys
     * @return
     */
    @Override
    public String apply(List<String> keys) {
        return keys.get(0);
    }
}
