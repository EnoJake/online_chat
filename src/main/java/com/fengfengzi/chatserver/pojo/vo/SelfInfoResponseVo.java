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
public class SelfInfoResponseVo {
    private int id;
    private String username;
    private String nickname;
    private String avatar;
    private int sex;
    private Object mobile;
}
