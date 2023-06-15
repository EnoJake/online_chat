package com.fengfengzi.chatserver.controller;

import com.fengfengzi.chatserver.common.R;
import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.pojo.vo.LoginRequestVo;
import com.fengfengzi.chatserver.pojo.vo.LoginResponseVo;
import com.fengfengzi.chatserver.pojo.vo.RegisterRequestVo;
import com.fengfengzi.chatserver.service.LoginService;
import com.fengfengzi.chatserver.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 王丰
 * @version 1.0
 */

@RestController
@RequestMapping("/slipper/im")
public class LoginController {

    @Resource
    LoginService loginService;


    /**
     * 用户登录
     * */
    @PostMapping("/login")
    public R<LoginResponseVo> loginApi(@RequestBody LoginRequestVo lVo) {
        System.out.println("======登录======\n");
        System.out.println("账号：" + lVo.getUsername() + "\n" + "密码：" + lVo.getPassword() + "\n");

        R<LoginResponseVo> r = new R<>();

        Map<String, Object> resMap = loginService.login(lVo);
        Integer code = (Integer) resMap.get("code");
        String msg = (String) resMap.get("msg");
        if (code.equals(ResultEnum.LOGIN_SUCCESS.getCode())) {
            String token = JwtUtils.createJwt(lVo.getUsername());

            r.ok(code, msg);
            LoginResponseVo response = new LoginResponseVo(token);
            r.data(response);

            System.out.println("-->登录成功，返回token--" + token + "\n==========\n");
        } else {
            r.error(code, msg);
            System.out.println("登录失败\n==========");
        }
        return r;
    }

    @PostMapping("/register")
    public R<Object> registerApi(@RequestBody RegisterRequestVo rVo) {
        System.out.println("======注册======\n");
        System.out.println("账号：" + rVo.getUsername() + "\n" + "密码：" + rVo.getPassword() + "\n");

        R<Object> r = new R<>();

        Map<String, Object> resMap = loginService.register(rVo);
        Integer code = (Integer) resMap.get("code");
        String msg = (String) resMap.get("msg");
        if (code.equals(ResultEnum.REGISTER_SUCCESS.getCode())) {
            r.ok(code, msg);
            r.data(null);
            System.out.println("  注册成功\n==============\n");
        } else {
            r.error(code, msg);
            System.out.println("  注册失败\n==============\n");
        }
        return r;
    }

    @PostMapping("/logout")
    public R<Object> logoutApi(@RequestHeader("im-token") String token) {
        // 退出post带过来了什么 什么都没有带过来，你自己写点东西充充数好了
        System.out.println("======退出登录操作======\n");
        // 由于我没有设置用户在线状态，这里可以直接返回成功，限制用户只能同时登录一个之后再说
        String username = JwtUtils.parseJwt(token).getSubject();
        System.out.println("这个友友：" + username + " 想退出登录了\n");
        System.out.println("让他退！！！\n");
        R<Object> r = new R<>();
        r.ok();

        return r;
    }
}
