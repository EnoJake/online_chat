package com.fengfengzi.chatserver.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王丰
 * @version 1.0
 */

@Data
public class R<T> {

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "状态消息")
    private String status;

    @ApiModelProperty(value = "返回数据")
    private T data;

    //成功静态方法
    public void ok() {
        this.setCode(0);
        this.setStatus("success");
        this.setMessage(ResultEnum.SUCCESS.getMessage());
    }

    public void ok(int code, String message) {
        this.setCode(code);
        this.setStatus("success");
        this.setMessage(message);
    }

    //失败静态方法
    public void error() {
        this.setCode(600);
        this.setStatus("error");
        this.setMessage(ResultEnum.ERROR.getMessage());
    }

    public void error(int code, String message) {
        this.setCode(code);
        this.setStatus("error");
        this.setMessage(message);
    }


    public R data(T data) {
        this.setData(data);
        return this;
    }
}
