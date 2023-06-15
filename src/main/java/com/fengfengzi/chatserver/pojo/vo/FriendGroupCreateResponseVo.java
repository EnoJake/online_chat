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
public class FriendGroupCreateResponseVo {
    private int can_deleted;
    private String created_at;
    private int id;
    private String name;
    private int user_id;
}
