package com.fengfengzi.chatserver.service;

import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.dao.*;
import com.fengfengzi.chatserver.pojo.*;
import com.fengfengzi.chatserver.pojo.vo.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 王丰
 * @version 1.0
 */

@Service
public class ConversationService {

    @Resource
    InfoDao infoDao;

    @Resource
    ConversationDao conversationDao;

    @Autowired
    MongoTemplate mongoTemplate;

    @Resource
    UserDao userDao;

    @Resource
    MessageDao messageDao;

    @Resource
    GroupsDao groupsDao;

    public Map<String, Object> list(String username) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        List<ConversationResponse> data = new ArrayList<>();

        Info from = infoDao.findInfoByUsername(username);

        Info partner;
        if (from != null) {
            from.setPassword(null);
            // user 即为本人的 from 对象
            Info to;
            Message message;

            List<Conversation> originalData = conversationDao.findByFrom(from.getId());
            originalData.sort((c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));
            System.out.println("按最后更新时间排列所有会话\n");
            //boolean isBreak = false;
            System.out.println("遍历所有初始会话，生成响应数据\n");
            for (Conversation conversation : originalData) {
                // 是不是因为名字不能叫做id啊

                to = infoDao.findInfoById(conversation.getTo());
                // 考虑群聊的情况
                if (to == null) {
                    System.out.println("群聊会话的情况，冒充成私聊会话，哈哈哈哈哈：\n");
                    Groups groups = groupsDao.findGroupsById(conversation.getTo());
                    to = new Info(
                            groups.get_id(),
                            conversation.getTo(),
                            groups.getName(),
                            groups.getName(),
                            "",
                            2,
                            groups.getAvatar(),
                            ""
                    );
                    System.out.println("群聊名称：" + groups.getName());
                    System.out.println("会话拥有者：" + username);
                    System.out.println("群聊未读消息数：" + conversation.getUnread_count());
                }
                to.setPassword(null);

                if (conversation.getMessage() == 0) {
                    message = null; // 这里只能为null，不能初始化
                } else {
                    message = messageDao.findMessageById(conversation.getMessage());
                }


                ConversationResponse response = new ConversationResponse(
                        conversation.getCreated_at(),
                        from,
                        conversation.getId(),
                        message, // 为 new Message() 会被解析成其实时间 1970-01-01
                        to,
                        conversation.getType(),
                        conversation.getUnread_count()
                );
                data.add(response);
            }

            //if (!isBreak) {
            map.put("data", data);
            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
            //}
        }

        map.put("code", code);
        map.put("msg", msg);
        return map;
        // 妈的没返回，槽
    }

    public Map<String, Object> create(String username, int id) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Info from = infoDao.findInfoByUsername(username);
        Info to = infoDao.findInfoById(id);

        if (from != null && to != null) {
            System.out.println("创建新的私聊会话\n");
            from.setPassword(null);
            to.setPassword(null); //隐藏密码，别把密码作为个人信息给传过去了

            // findByFromOrTo会把两个人有关的所有消息都集中，导致消息错位
            List<Message> messages0 = messageDao.findByFromAndTo(from.getId(), id); //我发给你
            List<Message> messages1 = messageDao.findByFromAndTo(id, from.getId()); //你发给我
            List<Message> messages = new ArrayList<>();
            messages.addAll(messages0);
            messages.addAll(messages1);
            messages.sort((c1, c2) -> Long.compare(c2.getCreated_at(), c1.getCreated_at()));

            Message message;
            if (messages.isEmpty()) {
                message = new Message();
                System.out.println("空会话\n");
            } else {
                message = messages.get(0);
                System.out.println("获取最新的一条消息：" + message.getContent());
                System.out.println("消息创建时间戳：" + message.getCreated_at());
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String created_at = formatter.format(date); //然而并不是根据这个来排序的，并没有什么卵用

            // 生成会话id
            int conversation_id;
            List<Conversation> conversations = conversationDao.findAll();
            if (conversations.size() == 0) {
                conversation_id = 1150;
            } else {
                conversation_id = conversations.get(conversations.size() - 1).getId() + 1;
            }
            System.out.println("生成新的会话id：" + conversation_id);


            Conversation conversation = new Conversation(
                    new ObjectId(),
                    conversation_id,
                    from.getId(),
                    to.getId(),
                    created_at,
                    1,
                    0,
                    messages.isEmpty() ? 0 : message.getId(),
                    messages.isEmpty() ? System.currentTimeMillis() : message.getCreated_at()
                    //这里的两个时间格式会有一点点的差距，不管了拉 | 现在改好了啦
            );
            conversationDao.save(conversation);
            System.out.println("保存新的会话\n");

            ConversationResponse response = new ConversationResponse(
                    created_at,
                    from,
                    conversation_id,
                    message,
                    to,
                    1,
                    0
            );

            map.put("data", response);
            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        } else if (from != null){

            System.out.println("群聊的情况了\n");
            // id为群聊的情况了
            from.setPassword(null);

            // 得到群聊
            Groups groups = groupsDao.findGroupsById(id);

            // 群聊消息查询很简单嘞
            List<Message> messages = messageDao.findByTo(id);
            messages.sort((c1, c2) -> Long.compare(c2.getCreated_at(), c1.getCreated_at()));

            Message message;
            if (messages.isEmpty()) {
                message = new Message();
            } else {
                message = messages.get(0);
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String created_at = formatter.format(date);

            // 生成会话id
            int conversation_id;
            List<Conversation> conversations = conversationDao.findAll();
            if (conversations.size() == 0) {
                conversation_id = 1150;
            } else {
                conversation_id = conversations.get(conversations.size() - 1).getId() + 1;
            }
            System.out.println("生成新的会话id：" + conversation_id);

            Conversation conversation = new Conversation(
                    new ObjectId(),
                    conversation_id,
                    from.getId(),
                    id,
                    created_at,
                    2,
                    0, // 点击事件的时候才会创建，这时候已经置零了
                    messages.isEmpty() ? 0 : message.getId(),
                    messages.isEmpty() ? System.currentTimeMillis() : message.getCreated_at()
            );
            conversationDao.save(conversation);
            System.out.println("保存新的会话\n");

            Info to1 = new Info(
                    groups.get_id(),
                    id,
                    groups.getName(),
                    groups.getName(),
                    "",
                    2,
                    groups.getAvatar(),
                    ""
            );

            ConversationResponse response = new ConversationResponse(
                    created_at,
                    from,
                    conversation_id,
                    message,
                    to1,
                    1,
                    0
            );
            map.put("data", response);
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

        // 删除会话是没有这么多的顾虑的
        Info user = infoDao.findInfoByUsername(username);
        Conversation conversation = conversationDao.findConversationById(id);
        if (conversation != null && user != null) {
            if (conversation.getFrom() == user.getId()) {
                System.out.println("是本人进行删除会话操作\n");
                conversationDao.delete(conversation);

                code = ResultEnum.SUCCESS.getCode();
                msg = ResultEnum.SUCCESS.getMessage();
            }
        }

        map.put("code", code);
        map.put("msg", msg);
        return map;

    }

//    public Map<String, Object> getConversationList(String username) {
//
//        Map<String, Object> map = new HashMap<>();
//        Integer code;
//        String msg;
//
//
//        User user = userDao.findUserByUsername(username);
//
//        if (user == null) {
//            code = ResultEnum.ERROR.getCode();
//            msg = ResultEnum.ERROR.getMessage();
//        } else {
//            OppositeVo[] talked = user.getTalked();
//            List<MessageVo> messages = messageDao.findByFromOrTo(user.getCode(), user.getCode());
//
//            List<Conversation> list = new ArrayList<>();
//
//            for (int i = 0; i < talked.length; i++) {
//                list.add(new Conversation());
//                list.get(i).setOpposite(talked[i]);
//
//                String opposite = talked[i].getUserid();
//                List<MessageVo> tmp = new ArrayList<>();
//
//                for (MessageVo message : messages) {
//                    if (Objects.equals(message.getFrom(), opposite) ||
//                            Objects.equals(message.getTo(), opposite)) {
//                        tmp.add(message);
//
//                    }
//                }
//                list.get(i).setMessages(tmp);
//                System.out.println("list now " + list);
//            }
//
//            map.put("data", list);
//
//            code = ResultEnum.SUCCESS.getCode();
//            msg = ResultEnum.SUCCESS.getMessage();
//
//        }
//        System.out.println("conversation list ->" + code + " " + msg);
//        map.put("code", code);
//        map.put("msg", msg);
//        return map;
//
//    }




}
