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
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "groups")
public class Groups {

    @Id
    private ObjectId _id;
    private int id;
    private String name;
    private String avatar = "http://124.223.50.19:8080/groups.jpg"; //到时候localhost得改成ip
    private String[] members;
    private int[] memberss;
    private String[] avatars;
}
