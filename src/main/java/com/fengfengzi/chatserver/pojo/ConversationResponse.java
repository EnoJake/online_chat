package com.fengfengzi.chatserver.pojo;

import com.fengfengzi.chatserver.pojo.vo.ConversationMessageResponseVo;
import com.fengfengzi.chatserver.pojo.vo.SelfInfoResponseVo;
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
public class ConversationResponse {
    private String created_at;
    private Info from;
    private int id;
    private Message message;
    private Info to;
    private int type;
    private int unread_count;
}
