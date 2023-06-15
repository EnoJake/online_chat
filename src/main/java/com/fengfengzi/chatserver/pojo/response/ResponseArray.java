package com.fengfengzi.chatserver.pojo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 王丰
 * @version 1.0
 */

@Data
public class ResponseArray {

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回状态")
    private String status;

    @ApiModelProperty(value = "返回数据")
    private Object[] data;

    private ResponseArray() {
    }

    //成功静态方法
    public static ResponseArray ok() {
        ResponseArray r = new ResponseArray();
        r.setCode(0);
        r.setMessage("成功！");
        r.setStatus("success");
        return r;
    }

    public ResponseArray data(Object[] data) {
        this.data = data;
        return this;
    }
}
