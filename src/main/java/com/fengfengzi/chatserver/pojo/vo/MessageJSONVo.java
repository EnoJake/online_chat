package com.fengfengzi.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 王丰
 * @version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageJSONVo {
    private String ack;
    private String content;
    private long createdAt;
    private int from;
    private int id;
    private int to;
    private int type;
    private String url;
}
