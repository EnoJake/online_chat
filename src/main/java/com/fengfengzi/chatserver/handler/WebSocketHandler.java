package com.fengfengzi.chatserver.handler;


import com.alibaba.fastjson.JSONObject;
import com.fengfengzi.chatserver.dao.ConversationDao;
import com.fengfengzi.chatserver.dao.GroupsDao;
import com.fengfengzi.chatserver.dao.InfoDao;
import com.fengfengzi.chatserver.dao.MessageDao;
import com.fengfengzi.chatserver.pojo.Conversation;
import com.fengfengzi.chatserver.pojo.Groups;
import com.fengfengzi.chatserver.pojo.Info;
import com.fengfengzi.chatserver.pojo.Message;
import com.fengfengzi.chatserver.service.FriendService;
import com.fengfengzi.chatserver.utils.JwtUtils;
import com.fengfengzi.chatserver.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.*;
import org.springframework.stereotype.*;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 王丰
 * @version 1.0
 */

@Slf4j
@Component
@ServerEndpoint("/websocket/{token}") // 提供给客户端一个操作终端

public class WebSocketHandler {

    /*
    * 问题可能出在 MongoTemplate ， MessageDao 和 FriendService 没有正确注入到 WebSocketHandler 类中。
    * 这是因为 @ServerEndpoint 注解与 Spring 的依赖注入机制不兼容，导致 @Autowired 和 @Resource 注解无法正常工作。
    * 注解用不了，咱原生走一波
     * */

    //@Autowired
    private MongoTemplate mongoTemplate;

    //@Resource
    private MessageDao messageDao;

    private ConversationDao conversationDao;

    //@Resource
    private static FriendService friendService;

    private InfoDao infoDao;

    private GroupsDao groupsDao;

    @Autowired
    private void setFriendService(FriendService friendService) {
        WebSocketHandler.friendService = friendService;
    }

    long messageCount = 12200 + 1;


    /**
     * 记录当前在线连接数
     * */
    public static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void handleOpen(Session session, @PathParam("token") String token) {
        if (StringUtils.isEmpty(token)) {
            sendMessage(session, "[websocket ERROR] 客户端Token错误，连接失败");
        } else {
            String username = JwtUtils.parseJwt(token).getSubject();
            sessionMap.put(username, session);
            System.out.println("保存用户会话 -> [" + username + "]: session id = " + session.getId());
        }

        if (WebSocketHandler.friendService == null) {
            System.out.println("依旧为 null");
        }
//        if (this.mongoTemplate == null) {
//            this.mongoTemplate = (MongoTemplate) SpringContextHolder.getBean("mongoTemplate");
//        }
        if (this.messageDao == null) {
            this.messageDao = SpringContextHolder.getBean("messageDao");
        }

        if (this.mongoTemplate == null) {
            this.mongoTemplate = SpringContextHolder.getBean("mongoTemplate");
        }

        if (this.conversationDao == null) {
            this.conversationDao = SpringContextHolder.getBean("conversationDao");
        }

        if (this.infoDao == null) {
            this.infoDao = SpringContextHolder.getBean("infoDao");
        }

        if (this.groupsDao == null) {
            this.groupsDao = SpringContextHolder.getBean("groupsDao");
        }
//        if (friendService == null) {
//
//            this.friendService = (FriendService) SpringContextHolder.getBean("friendService");
//        }
        log.info("[websocket]客户端创建连接，session ID = {} ", session.getId());
    }

    @OnClose
    public void handleClose(Session session, @PathParam("token") String token) {
        String username = JwtUtils.parseJwt(token).getSubject();
        sessionMap.remove(username);
        System.out.println("删除用户会话 -> [" + username + "]: session id = " + session.getId());
        log.info("[websocket]客户端断开websocket连接，session ID = {} ", session.getId());

    }

    @OnError
    public void handleError(Session session, Throwable throwable) {
        log.info("[websocket]出现错误，session ID = {} ", session.getId());
        log.info("[websocket]出现错误，throwable = {} ", throwable);
    }

    @OnMessage
    public void handleMessage(Session session, String message) {
        if (WebSocketHandler.friendService == null) {
            System.out.println("依旧为 null");
        }

        // 这里接收消息，
        JSONObject jsonMessage = JSONObject.parseObject(message);
        int type = jsonMessage.getInteger("type");
        switch (type) {
            case 0:
                sendMessage(session, message); // 心跳，直接返回
                break;
            case 1:

                // 保存到数据库
                long count = mongoTemplate.count(new Query(), "message");
                JSONObject innerMessage = jsonMessage.getJSONObject("requestBody").getJSONObject("message");

                Info fromUser = infoDao.findInfoById(innerMessage.getInteger("from"));
                Info existUser = infoDao.findInfoById(innerMessage.getInteger("to"));
                if (existUser != null) {
                    System.out.println("======私聊发送======\n");
                    Message newMessage = new Message(
                            new ObjectId(),
                            innerMessage.getString("ack"),
                            innerMessage.getString("content"),
                            System.currentTimeMillis(),
                            innerMessage.getInteger("from"),
                            (int) count + 12200,
                            1, // 0-已读
                            innerMessage.getInteger("to"),
                            innerMessage.getInteger("type"),
                            innerMessage.getString("url"),
                            fromUser.getAvatar(),
                            fromUser.getNickname()
                    );
                    messageDao.save(newMessage);
                    System.out.println("数据保存到数据库\n");

                    // 更新对面的未读消息数，己方肯定是0
                    // 双方会话都要更新
                    Conversation conversationTo = conversationDao.findByFromAndTo(innerMessage.getInteger("to"), innerMessage.getInteger("from"));
                    Conversation conversationFrom = conversationDao.findByFromAndTo(innerMessage.getInteger("from"), innerMessage.getInteger("to"));

                    if (conversationTo == null) {
                        System.out.println("对方没有建立会话\n");
                        List<Conversation> conversations = conversationDao.findAll();
                        int nowId = conversations.get(conversations.size() - 1).getId() + 1;
                        System.out.println("生成新的会话id：" + nowId);

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        String dateString = formatter.format(date);
                        System.out.println("生成新的时间格式：" + dateString);
                        conversationTo = new Conversation(
                                new ObjectId(),
                                nowId,
                                innerMessage.getInteger("to"),
                                innerMessage.getInteger("from"),
                                dateString,
                                1,
                                0,
                                0,
                                System.currentTimeMillis()
                        );
                        conversationDao.save(conversationTo);
                    }
                    System.out.println("会话更新的一般操作：\n");
                    conversationFrom.setMessage(newMessage.getId());
                    conversationTo.setMessage(newMessage.getId());
                    conversationFrom.setTimestamp(newMessage.getCreated_at());
                    conversationTo.setTimestamp(newMessage.getCreated_at());
                    System.out.println("更新双方会话的显示消息和更新时间\n");
                    //conversationFrom.setUnread_count(0);
                    // 轮不到这里来设置
                    conversationTo.setUnread_count(conversationTo.getUnread_count() + 1);
                    // 这里把未读消息数加1
                    System.out.println("对方会话未读消息数 + 1\n");
                    conversationDao.save(conversationFrom);
                    conversationDao.save(conversationTo);


                    // 回馈给本地
                    innerMessage.put("createdAt", newMessage.getCreated_at());
                    innerMessage.put("id", newMessage.getId());

                    // ===
                    innerMessage.put("avatar", newMessage.getAvatar());
                    innerMessage.put("nickname", newMessage.getNickname());
                    // ===

                    JSONObject responseBody = new JSONObject();
                    responseBody.put("message", innerMessage);
                    JSONObject response = new JSONObject();
                    response.put("responseBody", responseBody);
                    response.put("type", 2);
                    sendMessage(session, response.toString());
                    System.out.println("定制化JSON返回样式\n");
                    System.out.println("type 2 | 己方ack确认\n");

                    // 通知对方
                    response.put("type", 1);
                    Info user = infoDao.findInfoById(innerMessage.getInteger("to"));
                    sendMessage(sessionMap.get(user.getUsername()), response.toString());
                    System.out.println("type 1 | 在线接收消息 | 如果对方同时在线\n");

                } else {
                    System.out.println("======群聊发送======\n");
                    Message newMessage = new Message(
                            new ObjectId(),
                            innerMessage.getString("ack"),
                            innerMessage.getString("content"),
                            System.currentTimeMillis(),
                            innerMessage.getInteger("from"),
                            (int) count + 12200,
                            0, // 0-已读 群聊而言这个属性已经不重要了
                            innerMessage.getInteger("to"),
                            innerMessage.getInteger("type"),
                            innerMessage.getString("url"),
                            fromUser.getAvatar(),
                            fromUser.getNickname()
                    );
                    messageDao.save(newMessage);
                    System.out.println("消息内容：" + newMessage.getContent());
                    System.out.println("消息id：" + newMessage.getId());

                    Conversation conversationFrom = conversationDao.findByFromAndTo(innerMessage.getInteger("from"), innerMessage.getInteger("to"));
                    conversationFrom.setMessage(newMessage.getId());
                    conversationFrom.setTimestamp(newMessage.getCreated_at());
                    conversationDao.save(conversationFrom);
                    System.out.println("更新发送者：" + conversationFrom.getFrom() + " 会话显示\n");

                    Groups groups = groupsDao.findGroupsById(innerMessage.getInteger("to"));
                    String[] members = groups.getMembers();
                    int[] memberss = groups.getMemberss();
                    System.out.println("查询群聊\n");
                    System.out.println("群聊名称：" + groups.getName());
                    System.out.println("群聊成员：" + Arrays.toString(members));

                    // 回馈给本地
                    // 这里没有更新 新的消息类型的操作
                    innerMessage.put("createdAt", newMessage.getCreated_at());
                    innerMessage.put("id", newMessage.getId());

                    // ===
                    innerMessage.put("avatar", newMessage.getAvatar());
                    innerMessage.put("nickname", newMessage.getNickname());
                    // ===

                    JSONObject responseBody = new JSONObject();
                    responseBody.put("message", innerMessage);
                    JSONObject response = new JSONObject();
                    response.put("responseBody", responseBody);
                    response.put("type", 2);
                    sendMessage(session, response.toString());
                    System.out.println("ack确认，type = 2\n");

                    System.out.println("逐个向群成员发送消息\n\n");
                    for (int i = 0; i < memberss.length; i++) {

                        if (memberss[i] == innerMessage.getInteger("from")) {
                            System.out.println("跳过发送消息给自己，应为自己有ack确认\n");
                            System.out.println("跳过：" + members[i]);
                            System.out.println("------\n");
                            continue;
                        }


                        System.out.println("成员 " + i + " :\n");
                        System.out.println("姓名：" + members[i]);
                        System.out.println("id：" + memberss[i]);
                        Conversation conversation = conversationDao.findByFromAndTo(memberss[i], innerMessage.getInteger("to"));

                        if (conversation == null) {
                            System.out.println("成员无会话，生成新的会话\n");
                            List<Conversation> conversations = conversationDao.findAll();
                            int nowId = conversations.get(conversations.size() - 1).getId() + 1;
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date();
                            String dateString = formatter.format(date);
                            System.out.println("会话id：" + nowId);
                            System.out.println("会话时间格式：" + dateString);
                            // 生成会话id和时间格式
                            conversation = new Conversation(
                                    new ObjectId(),
                                    nowId,
                                    memberss[i],
                                    innerMessage.getInteger("to"),
                                    dateString,
                                    2,
                                    1, // *.*
                                    newMessage.getId(),
                                    newMessage.getCreated_at()
                            );
                            conversationDao.save(conversation);
                            System.out.println("会话类型：2\n");
                            System.out.println("会话消息：" + newMessage.getContent());
                            System.out.println("会话更新时间：" + newMessage.getCreated_at());

                        } else {
                            System.out.println("成员有会话，更新会话");
                            conversation.setUnread_count(conversation.getUnread_count() + 1);
                            System.out.println("更新未读消息数为：" + conversation.getUnread_count());
                            // 更新未读消息数
                            System.out.println("群聊未读消息数 + 1");
                            conversation.setMessage(newMessage.getId());
                            conversation.setTimestamp(newMessage.getCreated_at());
                            System.out.println("更新未读消息数：" + conversation.getUnread_count());
                            System.out.println("更新消息内容：" + newMessage.getContent());
                            System.out.println("更新最后更新时间：" + newMessage.getCreated_at());
                            conversationDao.save(conversation);
                        }

                        // 通知对方
                        response.put("type", 1);
                        sendMessage(sessionMap.get(members[i]), response.toString());
                        System.out.println("通知群聊消息接收者接收者：" + members[i]);
                    }

                }


                break;
        }

    }

    // 发送数据到客户端
    private void sendMessage(Session session, String message) {
        if (Objects.isNull(session)) {
            return;
        }
        try {
            session.getBasicRemote().sendText(message); // 发送数据
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // 发送来了私聊消息，存数据库，发送给接收方和发送方

    //@Async
//    private void saveMessage(JSONObject data) {
//        LocalDateTime currentDateTime = LocalDateTime.now();
//        // LocalDateTime expiredDateTime = currentDateTime.plusHours(10);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String createdAt = currentDateTime.format(formatter);
//        // 当前时间
//
//        long count = mongoTemplate.count(new Query(), "message");
//        String id = Long.toString(count + 12200);
//
//        Message save = new Message(
//                new ObjectId(),
//                data.getString("ack"),
//                data.getString("content"),
//                createdAt,
//                Long.toString(System.currentTimeMillis()),
//                data.getInteger("from").toString(),
//                id,
//                "0",
//                data.getInteger("to").toString(),
//                data.getInteger("type").toString(),
//                data.getString("url")
//        );
////
//        messageDao.save(save);
//        System.out.println("保存成功");
//    }

    public void sendApplyMessage(String receive, String message) {

        Session session = sessionMap.get(receive);
        if (Objects.isNull(session)) {
            System.out.println("对方不在线");
        } else {
            try {
                session.getBasicRemote().sendText(message);
                System.out.println("对方在线，在线发送");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendApplyRefuseMessage(String applicant, String message) {

        Session session = sessionMap.get(applicant);
        if (Objects.isNull(session)) {
            System.out.println("被拒绝的那个友友不在线");
        } else {
            try {
                session.getBasicRemote().sendText(message);
                System.out.println("被拒绝的那个友友在线，在线发送");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendApplyAgainMessage(String receive, String message) {

        Session session = sessionMap.get(receive);
        if (Objects.isNull(session)) {
            System.out.println("再次被加好友的友友不在线");
        } else {
            try {
                session.getBasicRemote().sendText(message);
                System.out.println("再次被加友友的友友在线，在线发送");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendApplyAcceptMessage(String receive, String message) {

        Session session = sessionMap.get(receive);
        if (Objects.isNull(session)) {
            System.out.println("告诉你我同意添加你了");
        } else {
            try {
                session.getBasicRemote().sendText(message);
                System.out.println("被告知同意的友友在线，在线发送");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

