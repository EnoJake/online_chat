package com.fengfengzi.chatserver.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王丰
 * @version 1.0
 */

@Data
public class RA {

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "状态消息")
    private String status;

    @ApiModelProperty(value = "返回数据")
    private Object[] data;

    //把构造方法私有
    private RA() {
    }

    //成功静态方法
    public static RA ok() {
        RA r = new RA();
        r.setStatus("success");
        r.resultEnum(ResultEnum.SUCCESS);
        return r;
    }

    //失败静态方法
    public static RA error() {
        RA r = new RA();
        r.setStatus("error");
        r.resultEnum(ResultEnum.ERROR);
        return r;
    }

    public RA resultEnum(ResultEnum resultEnum) {
        this.code(resultEnum.getCode());
        this.message(resultEnum.getMessage());
        return this;
    }


    public RA message(String message) {
        this.setMessage(message);
        return this;
    }

    public RA code(Integer code) {
        this.setCode(code);
        return this;
    }

    public RA data(Object[] value) {
        this.data = value;
        return this;
    }

}
