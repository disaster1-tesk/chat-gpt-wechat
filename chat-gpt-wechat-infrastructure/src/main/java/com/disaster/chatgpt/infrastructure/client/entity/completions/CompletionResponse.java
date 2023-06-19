package com.disaster.chatgpt.infrastructure.client.entity.completions;

import com.disaster.chatgpt.infrastructure.client.entity.common.OpenAiResponse;
import com.disaster.chatgpt.infrastructure.client.entity.common.Usage;
import com.disaster.chatgpt.infrastructure.client.entity.common.Choice;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述： 答案类
 *
 * @author https:www.unfbx.com
 *  2023-02-11
 */
@Data
public class CompletionResponse extends OpenAiResponse implements Serializable {
    private String id;
    private String object;
    private long created;
    private String model;
    private Choice[] choices;
    private Usage usage;
}
