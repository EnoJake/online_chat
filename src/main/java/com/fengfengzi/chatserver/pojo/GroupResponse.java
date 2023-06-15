package com.fengfengzi.chatserver.pojo;

import com.fengfengzi.chatserver.pojo.vo.GroupFriendResponseVo;
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
public class GroupResponse {
    private int can_deleted;
    private String created_at;
    private List<Info> friends;
    private int id;
    private String name;
    private int userId;
}
