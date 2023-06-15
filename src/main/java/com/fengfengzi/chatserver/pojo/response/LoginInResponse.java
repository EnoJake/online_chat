package com.fengfengzi.chatserver.pojo.response;

import com.fengfengzi.chatserver.common.R;
import com.fengfengzi.chatserver.common.ResultEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 王丰
 * @version 1.0
 */

@Data
public class LoginInResponse {

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回状态")
    private String status;

    @ApiModelProperty(value = "返回数据")
    private Map<String, Object> data = new HashMap<>();

    public LoginInResponse() {
    }

    //成功静态方法
    public static LoginInResponse ok() {
        LoginInResponse r = new LoginInResponse();
        r.setCode(0);
        r.setMessage("成功！");
        r.setStatus("success");
        return r;
    }

    public LoginInResponse data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
