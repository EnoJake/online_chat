package com.fengfengzi.chatserver.pojo;

import com.fengfengzi.chatserver.pojo.vo.MessagePageListResponseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 王丰
 * @version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagePageResponse {
    private int current;
    private List<Message> list;
    private int pages;
    private int size;
    private int total;
}
