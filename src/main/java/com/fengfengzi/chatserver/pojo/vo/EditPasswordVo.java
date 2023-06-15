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
public class EditPasswordVo {
    private String old_password;
    private String new_password;
    private String confirm_password;
}
