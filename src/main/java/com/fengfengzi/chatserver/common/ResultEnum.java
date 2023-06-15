package com.fengfengzi.chatserver.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 王丰
 * @version 1.0
 */

@Getter
@AllArgsConstructor
public enum ResultEnum {

    SUCCESS(0, "成功！"),
    ERROR(2001, "失败！"),
    ACCOUNT_IS_FROZEN_OR_CANCELLED(1200, "账号被冻结或注销"),
    KAPTCHA_TIME_OUT_OR_ERROR(1007, "验证码失效或错误"),
    ACCOUNT_NOT_FOUND(600, "账号不存在"),
    USER_LOGIN_FAILED(600, "账号或密码错误"),
    USER_LOGOUT_SUCCESS(2000, "用户登出成功"),
    //LOGIN_SUCCESS(1000, "登录成功"),
    LOGIN_SUCCESS(0, "登录成功！"),
    USER_NEED_AUTHORITIES(2002, "用户未登录"),
    USER_HAS_EXIST(600, "用户已被注册，请换个用户名重试"),
    INCORRECT_PASSWORD_TWICE(1004, "两次密码不一致"),
    REGISTER_SUCCESS(0, "注册成功！"),
    REGISTER_FAILED(1004, "注册失败，请重新尝试"),
    TOKEN_VALIDATION_EXPIRED(1006, "用户登录过期"),
    ILLEGAL_LOGIN(1009, "非法登录"),
    USER_HAS_LOGGED(1010, "用户已经在别处登录了"),
    OLD_PASSWORD_ERROR(1011, "原始密码输入错误"),
    ERROR_SETTING_GENDER(1012, "设置性别错误，必须为纯数字"),
    ERROR_SETTING_AGE(1012, "设置年龄错误，必须为纯数字"),
    ERROR_SETTING_EMAIL(1012, "设置邮箱错误"),
    ILLEGAL_OPERATION(4001, "非法操作"),
    ;
    /* 枚举类自出生起便会有的继承来的方法 */

    private Integer code;
    private String message;

    public static ResultEnum parse(int code) {
        ResultEnum[] values = values();
        for (ResultEnum value : values) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new RuntimeException("Unknown code of ResultEnum");
    }
}
