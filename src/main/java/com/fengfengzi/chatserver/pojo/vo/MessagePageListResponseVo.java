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
public class MessagePageListResponseVo {
    private Object ack;
    private String content;
    private String createdAt;
    private long created_at;
    private int from;
    private int id;
    private int status;
    private int to;
    private int type;
    private String url;
}
