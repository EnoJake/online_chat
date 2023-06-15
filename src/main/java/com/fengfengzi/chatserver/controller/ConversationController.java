package com.fengfengzi.chatserver.controller;

import com.fengfengzi.chatserver.common.R;
import com.fengfengzi.chatserver.common.RA;
import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.dao.UserDao;
import com.fengfengzi.chatserver.pojo.*;
import com.fengfengzi.chatserver.pojo.response.ResponseArray;
import com.fengfengzi.chatserver.pojo.vo.From;
import com.fengfengzi.chatserver.pojo.vo.MessageTo;
import com.fengfengzi.chatserver.pojo.vo.SelfInfoResponseVo;
import com.fengfengzi.chatserver.service.ConversationService;
import com.fengfengzi.chatserver.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 王丰
 * @version 1.0
 */

@RestController
@RequestMapping("/slipper/im/conversation")
public class ConversationController {

    @Autowired
    MongoTemplate mongoTemplate;

    @Resource
    ConversationService conversationService;

    @GetMapping("/list")
    public R<List<ConversationResponse>> listApi(@RequestHeader("im-token") String token) {
        System.out.println("======个人会话查询======\n");
        String username = JwtUtils.parseJwt(token).getSubject();

        R<List<ConversationResponse>> r = new R<>();

        Map<String, Object> resMap = conversationService.list(username);;
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            List<ConversationResponse> data = (List<ConversationResponse>) resMap.get("data");
            r.data(data);
            System.out.println("个人会话查询成功\n==========\n");
        } else {
            r.error();
            System.out.println("个人会话查询失败\n==========\n");
        }
        return r;
    }

    @PostMapping("/create")
    public R<ConversationResponse> createApi(
            @RequestHeader("im-token") String token,
            @RequestBody int id) {
        // 创建一个新的会话
        // token 得到自己的信息，id对方信息作为参数传进来
        System.out.println("======创建新的会话======");
        String username = JwtUtils.parseJwt(token).getSubject();

        R<ConversationResponse> r = new R<>();

        Map<String, Object> resMap = conversationService.create(username, id);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            ConversationResponse data = (ConversationResponse) resMap.get("data");
            System.out.println("个人会话添加成功\n==============");
            r.data(data);
        } else {
            r.error();
            System.out.println("个人会话添加失败\n==============");
        }
        return r;
    }

    @PostMapping("/delete")
    public R<Object> deleteApi(
            @RequestHeader("im-token") String token,
            @RequestBody int id) {
        System.out.println("======删除会话======");
        String username = JwtUtils.parseJwt(token).getSubject();

        R<Object> r = new R<>();

        Map<String, Object> resMap = conversationService.delete(username, id);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            System.out.println("个人会话删除成功\n==============");
        } else {
            r.error();
            System.out.println("个人会话删除失败\n==============");
        }

        return r;

    }

//    @GetMapping("/conversation/list")
//    public R<List<Conversation>> getConversationList(@RequestHeader("im-token") String token) {
//
//        System.out.println("come in conversation list");
//
//        String username = JwtUtils.parseJwt(token).getSubject();
//        User user = userDao.findUserByUsername(username);
//
//        Map<String, Object> resMap = conversationService.getConversationList(username);
//        Integer code = (Integer) resMap.get("code");
//        R<List<Conversation>> r = new R<>();
//        if (code.equals(ResultEnum.SUCCESS.getCode())) {
//            r.ok();
//            List<Conversation> data = (List<Conversation>) resMap.get("data");
//
//            r.data(data);
//
//        }
//        else {
//            r.error();
//        }
//
//        return r;
//
//    }
}
