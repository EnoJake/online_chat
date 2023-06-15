package com.fengfengzi.chatserver.pojo;

import com.fengfengzi.chatserver.pojo.vo.SelfInfoResponseVo;
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
public class ApplyResponse {
    private Info applicant;
    private int group_id;
    private int id;
    private Object remark;
    private Info reviewer;
    private int status;
}
