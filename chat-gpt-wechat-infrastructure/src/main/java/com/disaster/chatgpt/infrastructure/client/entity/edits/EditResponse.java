package com.disaster.chatgpt.infrastructure.client.entity.edits;


import com.disaster.chatgpt.infrastructure.client.entity.common.Choice;
import com.disaster.chatgpt.infrastructure.client.entity.common.Usage;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 *  2023-02-15
 */
@Data
public class EditResponse implements Serializable {
    private String id;
    private String object;
    private long created;
    private String model;
    private Choice[] choices;
    private Usage usage;
}
