package com.fengfengzi.chatserver.pojo;

import com.fengfengzi.chatserver.pojo.vo.From;
import com.fengfengzi.chatserver.pojo.vo.MessageTo;
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
public class SendMessage {
    private String created_at;
    private From from;
    private int id;
    private MessageTo message;
    private From to;
    private int type;
    private final int unread_count = 0;
}
