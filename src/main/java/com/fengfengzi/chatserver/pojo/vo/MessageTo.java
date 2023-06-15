package com.fengfengzi.chatserver.pojo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author 王丰
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class MessageTo {

    private String content;
    private String createdAt;
    private long created_at;
    private int from;
    private int id;
    private int status;
    private int to;
    private int type;
    private final String url = "";
}