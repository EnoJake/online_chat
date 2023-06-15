package com.fengfengzi.chatserver.controller;

import com.fengfengzi.chatserver.common.R;
import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.pojo.ApplyResponse;
import com.fengfengzi.chatserver.pojo.MessagePageResponse;
import com.fengfengzi.chatserver.pojo.vo.*;
import com.fengfengzi.chatserver.service.FriendService;
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
@RequestMapping("/slipper/im")
public class FriendController {

    @Resource
    FriendService friendService;

    @GetMapping("/friend/list")
    public Object listApi(@RequestHeader("im-token") String token) {

        System.out.println("这个api又被调用过吗");

        return null;
    }

    @PostMapping("/friend/create")
    public R<Object> addApi(
            @RequestHeader("im-token") String token,
            @RequestBody FriendCreateRequestVo cVo) {
        System.out.println("======添加好友申请======\n");

        String send = JwtUtils.parseJwt(token).getSubject();
        System.out.println("申请人：" + send + " --> 被申请人：" + cVo.getUsername() + "\n");
        System.out.println("期望分组号：" + cVo.getGroup_id() + "\n");

        R<Object> r = new R<>();
        Map<String, Object> resMap = friendService.add(send, cVo.getUsername(), cVo.getGroup_id());


        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            System.out.println("申请消息发送成功\n==========");
        } else {
            String msg = (String) resMap.get("msg");
            r.error(code, msg);
            System.out.println("申请消息发送失败\n==========");
        }

        return r;
    }

    @PostMapping("/friend/accept")
    public R<Object> acceptApi(
            @RequestHeader("im-token") String token,
            @RequestBody FriendAcceptRequestVo aVo) {
        // 我 token  申请记录的 aVo.id 分组 aVo.group_id | 通知对方 type 5
        // 接受好友申请的，传进来的是申请记录的 id 和 我同意将这个人分到那个分组 group_id
        System.out.println("======接受好友申请======\n");
        String username = JwtUtils.parseJwt(token).getSubject();

        R<Object> r = new R<>();

        Map<String, Object> resMap = friendService.accept(username, aVo.getId(), aVo.getGroup_id());
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            System.out.println("接受好友成功==========\n");
        } else {
            r.error();
            System.out.println("接受好友失败==========\n");
        }
        return r;


    }

    @PostMapping("/friend/refuse")
    public R<Object> refuseApi(@RequestBody FriendRefuseRequestVo rVo) {
        System.out.println("======拒绝好友申请======\n");
        R<Object> r = new R<>();


        Map<String, Object> resMap = friendService.refuse(rVo.getId());
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            System.out.println("拒绝好友成功==========\n");
        } else {
            r.error();
            System.out.println("拒绝好友失败==========\n");
        }
        return r;

    }

    @PostMapping("/friend/again")
    public Object againApi(@RequestBody FriendAgainRequestVo aVo) {
        System.out.println("======申请重新添加======\n");

        R<Object> r = new R<>();

        Map<String, Object> resMap = friendService.again(aVo.getId());
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            System.out.println("申请重新添加成功\n==========");
        } else {
            r.error();
            System.out.println("申请重新添加失败\n==========");
        }

        return r;
    }

    @PostMapping("/friend/move")
    public R<Object> moveApi(@RequestBody FriendMoveRequestVo mVo) {
        System.out.println("======移动好友");
        R<Object> r = new R<>();

        Map<String, Object> resMap = friendService.move(mVo);
        Integer code = (Integer) resMap.get("code");
        String msg = (String) resMap.get("msg");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            System.out.println("移动好友成功\n==========");
        } else {
            r.error(code, msg);
            System.out.println("移动好友失败\n==========");
        }
        return r;

    }

    @PostMapping("/friend/delete")
    public R<Object> deleteApi(
            @RequestHeader("im-token") String token,
            @RequestBody int id) {
        System.out.println("======删除友友的======");
        String username = JwtUtils.parseJwt(token).getSubject();

        R<Object> r = new R<>();

        Map<String, Object> resMap = friendService.delete(username, id);
        Integer code = (Integer) resMap.get("code");
        String msg = (String) resMap.get("msg");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            System.out.println("删除好友成功\n==========");
        } else {
            r.error(code, msg);
            System.out.println("删除好友失败\n==========");
        }
        return r;
    }

    @GetMapping("/friend/apply/list")
    public R<List<ApplyResponse>> applyListApi(@RequestHeader("im-token") String token) {
        System.out.println("======个人申请列表查询======\n");
        String username = JwtUtils.parseJwt(token).getSubject();

        R<List<ApplyResponse>> r = new R<>();

        Map<String, Object> resMap = friendService.applyList(username);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            List<ApplyResponse> data = (List<ApplyResponse>) resMap.get("data");
            r.data(data);
            System.out.println("个人申请列表查询成功\n==========\n");
        } else {
            r.error();
            System.out.println("个人申请列表查询失败\n==========\n");
        }

        return r;
    }

    @GetMapping("/private/message/page")
    public R<MessagePageResponse> messagePageApi(
            @RequestHeader("im-token") String token,
            @RequestParam(name = "friend_id") String friend_id) {
        String username = JwtUtils.parseJwt(token).getSubject();

        System.out.println("======得到私有聊天消息======\n");
        R<MessagePageResponse> r = new R<>();

        Map<String, Object> resMap = friendService.messagePage(username, Integer.parseInt(friend_id));
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            MessagePageResponse data = (MessagePageResponse) resMap.get("data");
            r.data(data);
            System.out.println("成功得到私有聊天消息======\n");
        } else {
            r.error();
            System.out.println("获取私有聊天消息失败======\n");
        }

        return r;
    }

    @PostMapping("/private/message/status")
    public R<Object> messageStatusApi(
            @RequestHeader("im-token") String token,
            @RequestBody int[] ids) {
        String username = JwtUtils.parseJwt(token).getSubject();
        System.out.println("======设置消息已读======\n");
        R<Object> r = new R<>();

        Map<String, Object> resMap = friendService.messageStatus(username, ids);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            System.out.println("消息和会话置空成功\n==========");
        } else {
            r.error();
            System.out.println("消息和会话置空失败\n==========");
        }
        return r;
    }








}
