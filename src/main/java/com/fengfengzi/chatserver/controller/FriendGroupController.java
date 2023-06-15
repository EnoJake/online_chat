package com.fengfengzi.chatserver.controller;

import com.fengfengzi.chatserver.common.R;
import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.dao.UserDao;
import com.fengfengzi.chatserver.pojo.GroupResponse;
import com.fengfengzi.chatserver.pojo.User;
import com.fengfengzi.chatserver.pojo.response.ResponseArray;
import com.fengfengzi.chatserver.pojo.vo.FriendGroupCreateRequestVo;
import com.fengfengzi.chatserver.pojo.vo.FriendGroupCreateResponseVo;
import com.fengfengzi.chatserver.pojo.vo.FriendGroupDeleteRequestVo;
import com.fengfengzi.chatserver.pojo.vo.SelfInfoResponseVo;
import com.fengfengzi.chatserver.service.FriendGroupService;
import com.fengfengzi.chatserver.utils.JwtUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 王丰
 * @version 1.0
 */

@RestController
@RequestMapping("/slipper/im/friend/group")
public class FriendGroupController {

    @Resource
    UserDao userDao;

    @Resource
    FriendGroupService friendGroupService;

    @GetMapping("/list")
    public R<List<GroupResponse>> listApi(@RequestHeader("im-token") String token) {
        System.out.println("======获取好友分组列表======\n");
        String username = JwtUtils.parseJwt(token).getSubject();

        R<List<GroupResponse>> r = new R<>();

        Map<String, Object> resMap = friendGroupService.list(username);
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            List<GroupResponse> data = (List<GroupResponse>) resMap.get("data");
            r.data(data);
            System.out.println("获取好友分组列表成功\n==========");
        } else {
            r.error();
            System.out.println("获取好友分组列表失败\n==========");
        }

        return r;
    }

    @PostMapping ("/create")
    public R<FriendGroupCreateResponseVo> addApi(
            @RequestHeader("im-token") String token,
            @RequestBody FriendGroupCreateRequestVo cVo) {
        System.out.println("======创建好友分组列表申请======\n");

        String username = JwtUtils.parseJwt(token).getSubject();

        R<FriendGroupCreateResponseVo> r = new R<>();

        Map<String, Object> resMap = friendGroupService.add(username, cVo.getName());
        Integer code = (Integer) resMap.get("code");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            FriendGroupCreateResponseVo data = (FriendGroupCreateResponseVo) resMap.get("data");
            r.data(data);
            System.out.println("创建好友列表成功\n==========");
        } else {
            r.error();
            System.out.println("创建好友列表失败\n==========");
        }

        return r;
    }

    @PostMapping("/delete")
    public R<Object> deleteApi(@RequestBody FriendGroupDeleteRequestVo dVo) {
        System.out.println("======删除好友分组列表申请申请======\n");

        R<Object> r = new R<>();

        Map<String, Object> resMap = friendGroupService.delete(dVo.getId());
        Integer code = (Integer) resMap.get("code");
        String msg = (String) resMap.get("msg");
        if (code.equals(ResultEnum.SUCCESS.getCode())) {
            r.ok();
            System.out.println("删除好友分组成功\n==========");
        } else {
            r.error(code, msg);
            System.out.println("删除好友分组失败\n==========");
        }


        return r;
    }

//    @GetMapping("/friend/group/list")
//    public ResponseArray getFriendGroup(@RequestHeader("im-token") String token) {
//
//        ResponseArray r = ResponseArray.ok();
//        Object[] data = new Object[1];
//
//        Map<String, Object> data0 = new HashMap<>();
//        data0.put("can_deleted", 1);
//        data0.put("created_at", "2023-05-21 07:51:49");
//
//        Object[] data0_friends = new Object[4];
//        Map<String, Object> data0_friends_0 = new HashMap<>();
//        Map<String, Object> data0_friends_1 = new HashMap<>();
//        Map<String, Object> data0_friends_2 = new HashMap<>();
//        Map<String, Object> data0_friends_3 = new HashMap<>();
//
//        //0
//        data0_friends_0.put("avatar", "http://resource.gumingchen.icu/images/slipper.jpeg");
//        data0_friends_0.put("group_id", 2764);
//        data0_friends_0.put("id", 1688);
//        data0_friends_0.put("nickname", "lilithavavsd");
//        data0_friends_0.put("remark", null);
//        data0_friends_0.put("sex", 2);
//        data0_friends_0.put("status", 1);
//        data0_friends_0.put("user_id", 2635);
//        data0_friends_0.put("username", "llilithavavsd");
//
//        //1
//        data0_friends_1.put("avatar", "http://resource.gumingchen.icu/images/slipper.jpeg");
//        data0_friends_1.put("group_id", 2764);
//        data0_friends_1.put("id", 1682);
//        data0_friends_1.put("nickname", "lilithb");
//        data0_friends_1.put("remark", null);
//        data0_friends_1.put("sex", 2);
//        data0_friends_1.put("status", 1);
//        data0_friends_1.put("user_id", 2634);
//        data0_friends_1.put("username", "lilithb");
//
//        //2
//        data0_friends_2.put("avatar", "http://resource.gumingchen.icu/images/slipper.jpeg");
//        data0_friends_2.put("group_id", 2764);
//        data0_friends_2.put("id", 1680);
//        data0_friends_2.put("nickname", "lilithd");
//        data0_friends_2.put("remark", null);
//        data0_friends_2.put("sex", 2);
//        data0_friends_2.put("status", 1);
//        data0_friends_2.put("user_id", 2633);
//        data0_friends_2.put("username", "lilithd");
//
//        //3
//        data0_friends_3.put("avatar", "http://resource.gumingchen.icu/images/slipper.jpeg");
//        data0_friends_3.put("group_id", 2764);
//        data0_friends_3.put("id", 1674);
//        data0_friends_3.put("nickname", "lilithc");
//        data0_friends_3.put("remark", null);
//        data0_friends_3.put("sex", 2);
//        data0_friends_3.put("status", 1);
//        data0_friends_3.put("user_id", 2630);
//        data0_friends_3.put("username", "lilithc");
//
//        data0_friends[0] = data0_friends_0;
//        data0_friends[1] = data0_friends_1;
//        data0_friends[2] = data0_friends_2;
//        data0_friends[3] = data0_friends_3;
//
//        data0.put("friends", data0_friends);
//        data0.put("id", 2764);
//        data0.put("name", "我的好友");
//        data0.put("userId", 2631);
//
//        data[0] = data0;
//        r.data(data);
//        return  r;
//
//
//
//        // ========================
////        String username = JwtUtils.parseJwt(token).getSubject();
////        User user = userDao.findUserByUsername(username);
////
////        System.out.println("catch -> /friend/group/list");
////        System.out.println("username -> " + username);
////
////        // 先对接前端，再对接数据库
////        RA r = RA.ok().resultEnum(ResultEnum.SUCCESS);
////
////        return R.ok().resultEnum(ResultEnum.SUCCESS).data("friendGroup", "friendGroup");
//    }
//
//    @GetMapping("/friend/apply/list")
//    public ResponseArray getFriendApply(@RequestHeader("im-token") String token) {
//
//        ResponseArray r = ResponseArray.ok();
//        r.data(new Object[0]);
//        return r;
//
//        // =============================
////        String username = JwtUtils.parseJwt(token).getSubject();
////        User user = userDao.findUserByUsername(username);
////
////        System.out.println("catch -> /friend/apply/list");
////        System.out.println("username -> " + username);
////
////        return R.ok().resultEnum(ResultEnum.SUCCESS).data("friendApply", "friendApply");
//    }

}
