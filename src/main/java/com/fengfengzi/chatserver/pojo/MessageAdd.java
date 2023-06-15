package com.fengfengzi.chatserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

/**
 * @author 王丰
 * @version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageAdd {

    private ObjectId _id;
    private String ack;
    private String content;
    private long created_at;
    private int from;
    private int id;
    private int status;
    private int to;
    private int type;
    private String url;
    private String name;
    private String avatar;
}
