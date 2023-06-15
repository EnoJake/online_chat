package com.fengfengzi.chatserver.pojo;

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
public class MessagePageGroupResponse {
    private int current;
    private List<MessageAdd> list;
    private int pages;
    private int size;
    private int total;
}
