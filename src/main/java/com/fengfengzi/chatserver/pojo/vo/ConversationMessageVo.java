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
public class ConversationMessageVo {
    private String content;
    private String createdAt;
    private String created_at;
    private String from;
    private String messageid;
    private String status;
    private String to;
    private String type;
    private String url;
}
