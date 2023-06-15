package com.fengfengzi.chatserver.pojo.vo;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Document(collection = "messages0")
public class MessageVo {
    @Id
    private ObjectId _id;
    private String content;
    private String createAt;
    private String from;
    private String to;
}
