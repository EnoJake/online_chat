package com.fengfengzi.chatserver.service;

import com.alibaba.fastjson.JSONObject;
import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.dao.*;
import com.fengfengzi.chatserver.handler.WebSocketHandler;
import com.fengfengzi.chatserver.pojo.*;
import com.fengfengzi.chatserver.pojo.vo.FriendMoveRequestVo;
import com.fengfengzi.chatserver.pojo.vo.MessagePageListResponseVo;
import com.fengfengzi.chatserver.pojo.vo.SelfInfoResponseVo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 王丰
 * @version 1.0
 */

@Service
public class FriendService {

    @Resource
    ApplyDao applyDao;

    @Resource
    InfoDao infoDao;

    @Resource
    GroupDao groupDao;

    @Resource
    MessageDao messageDao;

    @Autowired
    MongoTemplate mongoTemplate;

    @Resource
    WebSocketHandler webSocketHandler;

    @Resource
    ConversationDao conversationDao;

    @Resource
    GroupsDao groupsDao;


    public Map<String, Object> applyList(String username) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        List<ApplyResponse> data = new ArrayList<>();

        Info user = infoDao.findInfoByUsername(username);
        if (user != null) {

            List<Apply> originalData = applyDao.findByApplicantOrReviewer(user.getId(), user.getId());

            // 流化 + 收集
            originalData = originalData.stream()
                    .filter(apply -> apply.getStatus() != 1)
                    .collect(Collectors.toList());
            System.out.println("排除所有失效申请\n");

            for (Apply apply : originalData) {

                int otherId = user.getId() == apply.getApplicant() ? apply.getReviewer() : apply.getApplicant();
                Info other = infoDao.findInfoById(otherId);


                ApplyResponse response = new ApplyResponse(
                        user.getId() == apply.getApplicant() ? user : other,
                        apply.getGroup_id(),
                        apply.getId(),
                        null,
                        user.getId() == apply.getApplicant() ? other : user,
                        apply.getStatus()
                );
                data.add(response);
            }

            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
            map.put("data", data);
        }
        map.put("code", code);
        map.put("msg", msg);
        return map;
    }

    public Map<String, Object> messagePage(String username, int friend_id) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Info existFriend = infoDao.findInfoById(friend_id);
        Info user = infoDao.findInfoByUsername(username);
        Groups groups = groupsDao.findGroupsById(friend_id);
        if (user != null && existFriend != null) {
            System.out.println("获取聊条消息类型为私聊\n");
            List<Message> originalData0 = messageDao.findByFromAndTo(user.getId(), friend_id);
            List<Message> originalData1 = messageDao.findByFromAndTo(friend_id, user.getId());
            List<Message> list = new ArrayList<>();
            list.addAll(originalData0);
            list.addAll(originalData1);
            list.sort(
                    (c1, c2) -> Long.compare(c2.getCreated_at(), c1.getCreated_at()));


            // Collections.reverse(originalData);
            // 设置消息已读
            for (Message message : list) {
                message.setStatus(0);
                messageDao.save(message);
            }

            // 清空会话未读
            Conversation conversation = conversationDao.findByFromAndTo(user.getId(), friend_id);
            conversation.setUnread_count(0);
            conversationDao.save(conversation);


            MessagePageResponse data = new MessagePageResponse(
                    1,
                    list,
                    list.size() % 10 == 0 ? list.size() / 10 : list.size() / 10 + 1,
                    10,
                    list.size()
            );

            map.put("data", data);
            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        } else if (user != null && groups != null) {
            System.out.println("获取群聊消息：\n");
            System.out.println("群聊名称：" + groups.getName());

            List<Message> list = messageDao.findByTo(friend_id);
            list.sort((c1, c2) -> Long.compare(c2.getCreated_at(), c1.getCreated_at()));

            for (Message message : list) {
                message.setStatus(0);
                messageDao.save(message);
            }

            Conversation conversation = conversationDao.findByFromAndTo(user.getId(), friend_id);
            conversation.setUnread_count(0);
            System.out.println("这里已经设置了群聊未读为 0 了呀\n");
            conversationDao.save(conversation);

//            List<MessageAdd> addList = new ArrayList<>();
//            for (Message message : list) {
//                Info currentUser = infoDao.findInfoById(message.getFrom());
//
//                MessageAdd messageAdd = new MessageAdd(
//                        message.get_id(),
//                        message.getAck(),
//                        message.getContent(),
//                        message.getCreated_at(),
//                        message.getFrom(),
//                        message.getId(),
//                        message.getStatus(),
//                        message.getTo(),
//                        message.getType(),
//                        message.getUrl(),
//                        currentUser.getNickname(),
//                        currentUser.getAvatar()
//                );
//                addList.add(messageAdd);
//            }

            MessagePageResponse data = new MessagePageResponse(
                    1,
                    list,
                    list.size() % 10 == 0 ? list.size() / 10 : list.size() / 10 + 1,
                    10,
                    list.size()
            );

            map.put("data", data);
            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();

        }
        map.put("code", code);
        map.put("msg", msg);
        return map;


    }

    public long getMessageCount() {
        return mongoTemplate.count(new Query(), "message");
    }

    public Map<String, Object> accept(String username, int id, int group_id) {

        System.out.println("接受好友申请 \n");
        System.out.println("username -> " + username + "\nid -> " + id + " \ngroup_id -> " + group_id + "\n");
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Group group = groupDao.findGroupById(group_id);
        Apply apply = applyDao.findApplyById(id);
        if (apply != null && group != null) {
            // System.out.println("有_id的则为修改是不是这样子的说" + apply.get_id());

            apply.setStatus(1); // 1 接受
            applyDao.save(apply);
            System.out.println("修改该条申请记录为失效\n");

            int[] newFriends;
            if (group.getFriends().length == 0) { //这个group是一定可以读取到的，下面那个则不一定了
                newFriends = new int[1];
                newFriends[0] = apply.getApplicant();
            } else {
                newFriends = Arrays.copyOf(group.getFriends(), group.getFriends().length + 1);
                newFriends[newFriends.length - 1] = apply.getApplicant();
            }

            group.setFriends(newFriends);
            groupDao.save(group); // 接收者的好友列表要更新，申请者的也要
            System.out.println("更新自己的好友分组：" + group.getName() + "，将新朋友加入\n");


            Group applicantGroup = groupDao.findGroupById(apply.getGroup_id());

            // !!!  数组为空的问题
            if (applicantGroup.getFriends().length == 0) {
                newFriends = new int[1];
                newFriends[0] = apply.getReviewer();
            } else {
                newFriends = Arrays.copyOf(applicantGroup.getFriends(), applicantGroup.getFriends().length + 1);
                newFriends[newFriends.length - 1] = apply.getReviewer();
            }

            applicantGroup.setFriends(newFriends);
            groupDao.save(applicantGroup);
            System.out.println("更新申请人的好友分组：" + applicantGroup.getName() + " 将接收者：" + username + " 加入\n");


            // 通知对方
            // 通知对方老子接受你的好友申请了
            Info owner = infoDao.findInfoById(apply.getReviewer());
            Info applicantUser = infoDao.findInfoById(apply.getApplicant());

            System.out.println(username + " 接受 " + applicantUser.getUsername() + " 发来的好友申请\n");
            System.out.println("并把 " + applicantUser.getUsername() + " 放置到名为：" + group.getName() + " 的分组\n");

            JSONObject user = new JSONObject();
            user.put("avatar", owner.getAvatar());
            user.put("createdAt", "2023-11-12 12:34:56");
            user.put("id", owner.getId());
            user.put("nickname", owner.getNickname());
            user.put("sex", owner.getSex());
            user.put("username", owner.getUsername());

            JSONObject responseBody = new JSONObject();
            responseBody.put("user", user);
            JSONObject responseJSON = new JSONObject();
            responseJSON.put("responseBody", responseBody);
            responseJSON.put("type", 5);

            String response = responseJSON.toString();
            System.out.println("接受友友的response" + response);

            webSocketHandler.sendApplyAcceptMessage(applicantUser.getUsername(), response);
            System.out.println("发送socket信心给申请者\n");

            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        }



        map.put("code", code);
        map.put("msg", msg);

        return map;
    }

    public Map<String, Object> add(String send, String receive, int group_id) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Info sender = infoDao.findInfoByUsername(send);
        Info receiver = infoDao.findInfoByUsername(receive);
        if (receiver == null) msg = "对方账号不存在！";
        if (receiver != null && sender != null) {
            System.out.println("都是活人");

            // 增加逻辑，如果已经是好友了，或者 如果已经发送添加申请还未回复，则不管你啦
            List<Group> groups = groupDao.findByCode(sender.getId());
            for (Group group : groups) {
                int[] friends = group.getFriends();
                if (friends == null) continue;
                for (int friend : friends) {
                    if (friend == receiver.getId()) {
                        code = ResultEnum.ERROR.getCode();
                        msg = "您已经添加了该好友，分组：[" + group.getName() + "]";
                        map.put("code", code);
                        map.put("msg", msg);
                        return map;
                    }
                }
            }

            List<Apply> existApply = applyDao.findByApplicantAndReviewer(sender.getId(), receiver.getId());
            for (Apply exist : existApply) {
                if (exist.getStatus() == 1) continue;
                if (exist.getStatus() == 0) {
                    code = ResultEnum.ERROR.getCode();
                    msg = "您已申请添加过该好友，请等待对方同意！";
                    map.put("code", code);
                    map.put("msg", msg);
                    return map;
                }
                if (exist.getStatus() == 2) {
                    code = ResultEnum.ERROR.getCode();
                    msg = "您已发送过申请，且被拒绝，请于申请列表中重新发送申请！";
                    map.put("code", code);
                    map.put("msg", msg);
                    return map;
                }
            }

            long count = mongoTemplate.count(new Query(), "apply");
            Apply apply = new Apply(
                    new ObjectId(),
                    (int) count + 2600,
                    sender.getId(),
                    receiver.getId(),
                    group_id,
                    group_id,
                    "",
                    0
            );
            applyDao.save(apply);
            System.out.println("--> 申请记录保存到数据库\n");
            // 保存

            // socket 发送给接收方相关信息

            JSONObject user = new JSONObject();
            user.put("avatar", sender.getAvatar());
            user.put("createdAt", "2023-11-12 12:34:56");
            user.put("id", sender.getId());
            user.put("nickname", send);
            user.put("sex", sender.getSex());
            user.put("username", send);

            JSONObject responseBody = new JSONObject();
            responseBody.put("user", user);
            JSONObject responseJSON = new JSONObject();
            responseJSON.put("responseBody", responseBody);
            responseJSON.put("type", 4);

            String response = responseJSON.toString();
            System.out.println("加友友的response" + response);


            // name + message
            webSocketHandler.sendApplyMessage(receive, response);
            System.out.println("--> 发送socket信息给接收方");
            //

            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        }

        map.put("code", code);
        map.put("msg", msg);

        return map;
    }

    public Map<String, Object> refuse(int id) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Apply apply = applyDao.findApplyById(id);
        if (apply != null) {
            apply.setStatus(2); // 2 拒绝
            applyDao.save(apply);
            System.out.println("同步数据库\n");

            // 告诉那个人 人家拒绝添加你啦  type 6

            Info reviewer = infoDao.findInfoById(apply.getReviewer());
            Info applicant = infoDao.findInfoById(apply.getApplicant());

            JSONObject user = new JSONObject();
            user.put("avatar", reviewer.getAvatar());
            user.put("createdAt", "2023-11-12 12:34:56");
            user.put("id", reviewer.getId());
            user.put("nickname", reviewer.getNickname());
            user.put("sex", reviewer.getSex());
            user.put("username", reviewer.getUsername());

            JSONObject responseBody = new JSONObject();
            responseBody.put("user", user);
            JSONObject responseJSON = new JSONObject();
            responseJSON.put("responseBody", responseBody);
            responseJSON.put("type", 6);

            String response = responseJSON.toString();
            System.out.println("加友友的response" + response);

            webSocketHandler.sendApplyRefuseMessage(applicant.getUsername(), response);
            System.out.println("socket通知对方 你被拒绝啦");


            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        }

        map.put("code", code);
        map.put("msg", msg);

        return map;
    }

    public Map<String, Object> again(int id) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Apply apply = applyDao.findApplyById(id);
        if (apply != null) {
            apply.setStatus(0); // 0 待处理
            applyDao.save(apply);
            System.out.println("数据库重置申请记录");

            // socket 再度通知给被加的那个友友

            Info sender = infoDao.findInfoById(apply.getApplicant());
            Info receiver = infoDao.findInfoById(apply.getReviewer());

            JSONObject user = new JSONObject();
            user.put("avatar", sender.getAvatar());
            user.put("createdAt", "2023-11-12 12:34:56");
            user.put("id", sender.getId());
            user.put("nickname", sender.getNickname());
            user.put("sex", sender.getSex());
            user.put("username", sender.getUsername());

            JSONObject responseBody = new JSONObject();
            responseBody.put("user", user);
            JSONObject responseJSON = new JSONObject();
            responseJSON.put("responseBody", responseBody);
            responseJSON.put("type", 4);

            String response = responseJSON.toString();

            webSocketHandler.sendApplyAgainMessage(receiver.getUsername(), response);
            System.out.println("socket通知对方有人申请\n");


            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        }

        map.put("code", code);
        map.put("msg", msg);

        return map;
    }

    public Map<String, Object> move(FriendMoveRequestVo mVo) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Group toGroup = groupDao.findGroupById(mVo.getGroup_id());

        int[] toFriends = toGroup.getFriends();
        for (int toFriend : toFriends) {
            if (toFriend == mVo.getId()) {
                msg = "该好友正在当前分组中，不需要移动！";
                map.put("code", code);
                map.put("msg", msg); //妈的这里"msg"写成了"mag"导致前端光有个警告框没有文字内容

                return map;
            }
        }

        Info user = infoDao.findInfoById(toGroup.getUserId());

        if (user != null) {
            List<Group> groups = groupDao.findByCode(toGroup.getUserId());

            boolean find = false;
            for (Group group : groups) {

                if (find) break;

                if (group.getFriends().length == 0) continue;
                int[] friends = group.getFriends();
                for (int j : friends) {
                    if (j == mVo.getId()) {
                        int[] newFriends = new int[friends.length - 1];
                        int index = 0;
                        for (int friend : friends) {
                            if (friend != mVo.getId())
                                newFriends[index++] = friend;
                        }
                        group.setFriends(newFriends);
                        groupDao.save(group);

                        find = true;
                        break;
                    }
                }
            }

            int[] newFriends;
            if (toGroup.getFriends().length == 0) {
                newFriends = new int[1];
                newFriends[0] = mVo.getId();
            } else {
                newFriends = Arrays.copyOf(toGroup.getFriends(), toGroup.getFriends().length + 1);
                newFriends[newFriends.length - 1] = mVo.getId();
            }

            toGroup.setFriends(newFriends);
            groupDao.save(toGroup);

            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        }

        map.put("code", code);
        map.put("msg", msg);

        return map;
    }

    public Map<String, Object> delete(String username, int id) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Info user = infoDao.findInfoByUsername(username); //自己
        Info other = infoDao.findInfoById(id); //对方

        if (user != null && other != null) {
            // 先删自己这边的
            List<Group> userGroup = groupDao.findByCode(user.getId());

            boolean find = false;
            for (Group group : userGroup) {

                if (find) break;

                int[] friends = group.getFriends();
                if (friends.length == 0) continue;

                for (int j : friends) {
                    if (j == id) {
                        int index = 0;
                        int[] newFriends = new int[friends.length - 1];
                        for (int friend : friends) {
                            if (friend != id) {
                                newFriends[index++] = friend;
                            }
                        }
                        group.setFriends(newFriends);
                        groupDao.save(group);

                        find = true;
                        break;
                    }


                }
            }

            System.out.println("删除自己这边的记录\n");

            userGroup = groupDao.findByCode(id);

            find = false;
            for (Group group : userGroup) {
                if (find) break;
                int[] friends = group.getFriends();
                if (friends.length == 0) continue;

                for (int j : friends) {
                    if (j == user.getId()) {
                        int index = 0;
                        int[] newFriends = new int[friends.length - 1];
                        for (int friend : friends) {
                            if (friend != user.getId()) {
                                newFriends[index++] = friend;
                            }
                        }
                        group.setFriends(newFriends);
                        groupDao.save(group);

                        find = true;
                        break;
                    }


                }

            }

            System.out.println("删除对方那边的好友记录\n");

            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        }
        map.put("code", code);
        map.put("msg", msg);
        return map;
    }

    public Map<String, Object> messageStatus(String username, int[] ids) {
        Map<String, Object> map = new HashMap<>();
        Info user = infoDao.findInfoByUsername(username);

        for (int id : ids) {
            Message message = messageDao.findMessageById(id);
            message.setStatus(0); //0-已读，你他妈又写错了
            messageDao.save(message);
            // 设置消息

            Conversation conversation = conversationDao.findByFromAndMessage(user.getId(), id);
            if (conversation != null) {
                conversation.setUnread_count(0);
                conversationDao.save(conversation);
            }
            // 其实我感觉是可以只考虑查最后一个id，但不确定前端是怎么写的

        }



        map.put("code", ResultEnum.SUCCESS.getCode());
        map.put("msg", ResultEnum.SUCCESS.getMessage());
        return map;
    }
}
