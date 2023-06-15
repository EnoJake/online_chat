package com.fengfengzi.chatserver.controller;

import com.fengfengzi.chatserver.common.R;
import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.pojo.ConversationResponse;
import com.fengfengzi.chatserver.pojo.Groups;
import com.fengfengzi.chatserver.service.GroupService;
import com.fengfengzi.chatserver.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author 王丰
 * @version 1.0
 */

@RestController
@RequestMapping("/slipper/im/group")
public class GroupController {

    @Resource
    GroupService groupService;

    @GetMapping("/list")
    public R<List<Groups>> listApi(@RequestHeader("im-token") String token) {
        String username = JwtUtils.parseJwt(token).getSubject();
        System.out.println("======获取群聊列表======\n");
        System.out.println("--> username = " + username + "\n");

        R<List<Groups>> r = new R<>();

        Map<String, Object> resMap = groupService.list(username);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            List<Groups> data = (List<Groups>) resMap.get("data");
            r.data(data);
            System.out.println("群聊信息查询成功\n==========\n");
        } else {
            r.error();
            System.out.println("群聊信息查询失败\n==========\n");
        }
        return r;

    }

    @PostMapping("/create")
    public R<Groups> createApi(
            @RequestHeader("im-token") String token,
            @RequestBody String name) {
        String username = JwtUtils.parseJwt(token).getSubject();
        System.out.println("======创建群聊======\n");

        R<Groups> r = new R<>();

        Map<String, Object> resMap = groupService.create(username, name);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            Groups data = (Groups) resMap.get("data");
            r.data(data);
            System.out.println("群聊创建成功\n==========\n");
        } else {
            r.error(code, (String) resMap.get("msg"));
            System.out.println("群聊创建失败\n==========\n");
        }

        return r;

    }

    @PostMapping("/add")
    public R<Groups> addApi(
            @RequestHeader("im-token") String token,
            @RequestBody String name) {
        String username = JwtUtils.parseJwt(token).getSubject();
        System.out.println("======添加群聊======\n");

        R<Groups> r = new R<>();

        Map<String, Object> resMap = groupService.add(username, name);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            Groups data = (Groups) resMap.get("data");
            r.data(data);
            System.out.println("群聊添加成功\n==========\n");
        } else {
            r.error(code, (String) resMap.get("msg"));
            System.out.println("群聊添加失败\n==========\n");
        }
        return r;

    }

    @PostMapping("/delete")
    public R<Object> deleteApi(
            @RequestHeader("im-token") String token,
            @RequestBody String name) {
        String username = JwtUtils.parseJwt(token).getSubject();
        System.out.println("======退出群聊======\n");

        R<Object> r = new R<>();

        Map<String, Object> resMap = groupService.delete(username, name);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            Groups data = (Groups) resMap.get("data");
            System.out.println("群聊退出成功\n==========\n");
        } else {
            r.error(code, (String) resMap.get("msg"));
            System.out.println("群聊退出失败\n==========\n");
        }

        return r;
    }
}
