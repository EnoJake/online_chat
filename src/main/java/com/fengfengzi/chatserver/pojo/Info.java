package com.fengfengzi.chatserver.pojo;

import com.fengfengzi.chatserver.pojo.vo.OppositeVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author 王丰
 * @version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "info")
public class Info {

    @Id
    private ObjectId _id;
    private int id; // 唯一，用于检索
    @Indexed(unique = true)
    private String username;
    private String nickname;
    private String password;
    private int sex;
    private String avatar = "http://124.223.50.19:8080/avatar.jpg"; // 124.223.50.19localhost
    private String mobile = "";

}
