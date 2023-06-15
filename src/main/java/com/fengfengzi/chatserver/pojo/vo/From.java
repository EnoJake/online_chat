package com.fengfengzi.chatserver.pojo.vo;

import lombok.Data;

/**
 * @author 王丰
 * @version 1.0
 */

@Data
public class From {
    private final String avatar = "http://resource.gumingchen.icu/images/slipper.jpeg";
    private int id;
    private final Object mobile = null;
    private String nickname;
    private final int sex = 2;
    private String username;
}
