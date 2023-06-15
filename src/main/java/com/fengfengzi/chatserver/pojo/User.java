package com.fengfengzi.chatserver.pojo;

import com.fengfengzi.chatserver.pojo.vo.OppositeVo;
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
@Document(collection = "users0")
// 妈的插错表了，去死吧草泥马
// 废弃的类
public class User {

    @Id
    private ObjectId _id;
    @Indexed(unique = true)
    private String username;
    private String password;
    private Group[] groups;
    private String user_id;
    private String code; // 唯一，用于检索
    private String avatar = "http://localhost:8080/avatar.jpg";
    private OppositeVo[] talked;
}
