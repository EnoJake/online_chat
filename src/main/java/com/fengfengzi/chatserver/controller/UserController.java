package com.fengfengzi.chatserver.controller;

import com.fengfengzi.chatserver.common.R;
import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.dao.GroupsDao;
import com.fengfengzi.chatserver.dao.MessageDao;
import com.fengfengzi.chatserver.dao.UserDao;
import com.fengfengzi.chatserver.dao.UsergroupDao;
import com.fengfengzi.chatserver.pojo.Groups;
import com.fengfengzi.chatserver.pojo.Info;
import com.fengfengzi.chatserver.pojo.User;
import com.fengfengzi.chatserver.pojo.Usergroup;
import com.fengfengzi.chatserver.pojo.response.LoginInResponse;
import com.fengfengzi.chatserver.pojo.vo.*;
import com.fengfengzi.chatserver.service.ConversationService;
import com.fengfengzi.chatserver.service.UserService;
import com.fengfengzi.chatserver.utils.JwtUtils;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author 王丰
 * @version 1.0
 */

@RestController
@RequestMapping("/slipper/im/user")
public class UserController {

    @Resource
    UserService userService;

    @Resource
    UserDao userDao;

    @Resource
    MessageDao messageDao;

    @Resource
    ConversationService conversationService;

    @Resource
    UsergroupDao usergroupDao;

    @Resource
    GroupsDao groupsDao;



    @GetMapping("/self/info")
    public R<Info> selfInfoApi(@RequestHeader("im-token") String token) {
        System.out.println("======获取个人信息======\n");
        String username = JwtUtils.parseJwt(token).getSubject();

        R<Info> r = new R<>();

        Map<String, Object> resMap = userService.selfInfo(username);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            Info data = (Info) resMap.get("data");
            r.data(data);
            System.out.println("获取个人信息成功\n==========");
        } else {
            r.error();
            System.out.println("获取个人信息失败\n==========");
        }
        // 使命完成
//
//        System.out.println("在获取个人信息的部分来测试群聊代码\n");
//        int[] groupsid = new int[1];
//        groupsid[0] = 2280;
//
//        Usergroup usergroup = new Usergroup(
//                new ObjectId(),
//                3280,
//                2500,
//                groupsid
//        );
//        usergroupDao.save(usergroup);
//
//
//        String[] members = new String[1];
//        members[0] = "fengfengzi0";
//        Groups groups = new Groups(
//                new ObjectId(),
//                2280,
//                "史上第一个群聊",
//                "http://localhost:8080/groups.jpg",
//                members
//        );
//        groupsDao.save(groups);

        return r;
    }

    @PostMapping("/update/basic")
    public R<Object> editBasicApi(
            @RequestHeader("im-token") String token,
            @RequestBody EditBasicVo eVo
    ) {
        System.out.println("======编辑个人信息======\n");
        String username = JwtUtils.parseJwt(token).getSubject();
        R<Object> r = new R<>();

        Map<String, Object> resMap = userService.editBasic(username, eVo);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            System.out.println("修改个人信息成功\n==========");
        } else {
            String msg = (String) resMap.get("msg");
            r.error();
            System.out.println("修改个人信息失败失败\n==========");
        }
        //System.out.println("就不让你改\n");

        return r;
    }

    @PostMapping("/update/password")
    public R<Object> editPasswordApi(
            @RequestHeader("im-token") String token,
            @RequestBody EditPasswordVo eVo
            ) {
        System.out.println("======修改密码======\n");
        String username = JwtUtils.parseJwt(token).getSubject();
        R<Object> r = new R<>();

        Map<String, Object> resMap = userService.editPassword(username, eVo);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            System.out.println("修改密码成功\n==========");
        } else {
            String msg = (String) resMap.get("msg");
            r.error(code, msg);
            System.out.println("修改密码失败\n==========");
        }
        //System.out.println("就不让你改\n");

        return r;
    }
//
//    @PostMapping("/file/upload")
//    public Object uploadApi(@RequestBody Object object) {
//
//
//        return null;
//    }


    //JwtUtils jwtUtils;
    /**
     * 用户注册
     */
//    @PostMapping("/register")
//    public R<Object> register(@RequestBody RegisterRequestVo rVo) {
//        Map<String, Object> resMap = userService.register(rVo);
//        Integer code = (Integer) resMap.get("code");
//
//        if (code.equals(ResultEnum.REGISTER_SUCCESS.getCode())) {
//
//            String username = (String)resMap.get("username");
//            String token = JwtUtils.createJwt(username);
//
//            R<Object> r =new R<>();
//            r.ok();
//            System.out.println("register return success");
//            return r;
//
//        }
//        else {
//            R<Object> r = new R<>();
//            r.error();
//            return r;
//        }
//    }

    /**
     * 用户登录
     * */
//    @PostMapping("/login")
//    public R<LoginResponseVo> login(@RequestBody LoginRequestVo lVo) {
//
//        System.out.println("catch request");
//
//
//
//        Map<String, Object> resMap = userService.login(lVo);
//
//        Integer code = (Integer) resMap.get("code");
//        if (code.equals(ResultEnum.LOGIN_SUCCESS.getCode())) {
//
//            String username = (String)resMap.get("username");
//            String token = JwtUtils.createJwt(username);
//
//            //LocalDateTime currentDateTime = LocalDateTime.now();
//            //LocalDateTime expiredDateTime = currentDateTime.plusHours(10);
//            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            //String updated_at = currentDateTime.format(formatter);
//            //String expired_at = expiredDateTime.format(formatter);
//
//            R<LoginResponseVo> r = new R<>();
//            r.ok();
//            LoginResponseVo response = new LoginResponseVo();
//            response.setToken(token);
//            r.data(response);
//
//            System.out.println("going to return return token " + token);
//            return r;
//        }
//        else {
//            R<LoginResponseVo> r = new R<>();
//            r.error();
//            return r;
//        }
//    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }


//    @GetMapping("/self/info")
//    public R<SelfInfoResponseVo> getUserInfo(@RequestHeader("im-token") String token) {
//        // 空指针异常
//        // 它奶奶的，逻辑写反了
//        // 然后是classcast异常 。。。
//
//
//
//
//        String username = JwtUtils.parseJwt(token).getSubject();
//        System.out.println("username -> " + username + " get request for self info");
//        Map<String, Object> resMap = userService.getSelfInfo(username);
//
//        System.out.println("============\n\n在get self info 里面测试conversation");
//        conversationService.getConversationList(username);
//
//        Integer code = (Integer) resMap.get("code");
//        R<SelfInfoResponseVo> r = new R<>();
//        if (code.equals(ResultEnum.SUCCESS.getCode())) {
//            r.ok();
//            SelfInfoResponseVo response = new SelfInfoResponseVo();
//            response.setId(Integer.parseInt((String)resMap.get("id")));
//            response.setUsername(username);
//            response.setAvatar((String)resMap.get("avatar"));
//            r.data(response);
//
//        }
//        else {
//            r.error();
//        }
//        return r;
//
//    }


}
