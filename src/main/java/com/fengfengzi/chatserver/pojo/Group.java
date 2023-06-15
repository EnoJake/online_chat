package com.fengfengzi.chatserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author 王丰
 * @version 1.0
 * 分组非群聊
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "group")
public class Group {

    @Id
    private ObjectId _id;
    private int id;
    private String name;
    private int can_deleted;
    private int userId;
    private int code;
    private int[] friends;

}
