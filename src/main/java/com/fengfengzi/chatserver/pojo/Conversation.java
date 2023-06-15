package com.fengfengzi.chatserver.pojo;

import com.fengfengzi.chatserver.pojo.vo.ConversationMessageVo;
import com.fengfengzi.chatserver.pojo.vo.MessageVo;
import com.fengfengzi.chatserver.pojo.vo.OppositeVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author 王丰
 * @version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversation")
public class Conversation {

    @Id
    private ObjectId _id;
    private int id;
    private int from; // 拥有者
    private int to; // 对方
    private String created_at; // "2023-10-10 12:34:56"
    private int type;
    private int unread_count;
    private int message; // message的id
    private long timestamp; // 用于排序 更新时间

}
