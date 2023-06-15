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
public class GroupFriendResponseVo {

    private String avatar;
    private int group_id;
    private int id;
    private String nickname;
    private Object remark;
    private int sex;
    private int status;
    private int user_id;
    private String username;
}
