package com.fengfengzi.chatserver.service;

import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.dao.ConversationDao;
import com.fengfengzi.chatserver.dao.GroupsDao;
import com.fengfengzi.chatserver.dao.InfoDao;
import com.fengfengzi.chatserver.dao.UsergroupDao;
import com.fengfengzi.chatserver.pojo.Conversation;
import com.fengfengzi.chatserver.pojo.Groups;
import com.fengfengzi.chatserver.pojo.Info;
import com.fengfengzi.chatserver.pojo.Usergroup;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 王丰
 * @version 1.0
 */

@Service
public class GroupService {

    @Resource
    InfoDao infoDao;

    @Resource
    UsergroupDao usergroupDao;

    @Resource
    GroupsDao groupsDao;

    @Resource
    ConversationDao conversationDao;

    @Autowired
    MongoTemplate mongoTemplate;

    public Map<String, Object> list(String username) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        List<Groups> data = new ArrayList<>();

        Info user = infoDao.findInfoByUsername(username);
        if (user != null) {
            Usergroup usergroup = usergroupDao.findUsergroupByUserid(user.getId());
            // 群聊单独一个表，后面再说
            if (usergroup != null) {
                int[] groups = usergroup.getGroupsid();
                for (int group : groups) {
                    Groups groups1 = groupsDao.findGroupsById(group);
                    data.add(groups1);
                }
            }
            map.put("data", data);
            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        }
        map.put("code", code);
        map.put("msg", msg);

        return map;
    }

    // 创建新的群聊
    public Map<String, Object> create(String username, String name) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Info user = infoDao.findInfoByUsername(username);

        Groups existGroup = groupsDao.findGroupsByName(name);
        if (existGroup != null) {
            msg = "该群聊已被创建！";
            map.put("code", code);
            map.put("msg", msg);
            return map;
        }

        long count = mongoTemplate.count(new Query(), "groups");

        String[] members = new String[1];
        members[0] = username;
        int[] memberss = new int[1];
        memberss[0] = user.getId();
        String[] avatars = new String[1];
        avatars[0] = user.getAvatar();
        Groups groups = new Groups(
                new ObjectId(),
                (int) count + 22800,
                name,
                "http://124.223.50.19:8080/groups.jpg",
                members,
                memberss,
                avatars
        );
        groupsDao.save(groups);
        System.out.println("创建新的群组，保存到数据库\n");



        Usergroup existUserGroup = usergroupDao.findUsergroupByUserid(user.getId());
        if (existUserGroup == null) {
            long count1 = mongoTemplate.count(new Query(), "usergroup");
            int[] groupsid = new int[1];
            groupsid[0] = (int) count + 22800;

            Usergroup usergroup = new Usergroup(
                    new ObjectId(),
                    (int) count1 + 32800,
                    user.getId(),
                    groupsid
            );
            usergroupDao.save(usergroup);
        } else {
            int[] newGroups = Arrays.copyOf(existUserGroup.getGroupsid(), existUserGroup.getGroupsid().length + 1);
            newGroups[newGroups.length - 1] = (int) count + 22800;
            existUserGroup.setGroupsid(newGroups);
            usergroupDao.save(existUserGroup);
        }
        System.out.println("如果不存在，则新增用户群组索引，如果存在，则更新\n");


        code = ResultEnum.SUCCESS.getCode();
        msg = ResultEnum.SUCCESS.getMessage();
        map.put("data", groups);
        map.put("code", code);
        map.put("msg", msg);
        System.out.println("将新创建的群聊信息返回，主要是id及已有的名称\n");


        return map;
    }


    public Map<String, Object> add(String username, String name) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Groups existGroup = groupsDao.findGroupsByName(name);
        if (existGroup == null) {
            System.out.println("群聊不存在，添加失败\n");
            msg = "该群聊不存在！";
            map.put("code", code);
            map.put("msg", msg);
            return map;
        }

        Info user = infoDao.findInfoByUsername(username);
        Usergroup existUserGroup = usergroupDao.findUsergroupByUserid(user.getId());
        if (existUserGroup == null) {
            System.out.println("用户群组索引不存在，添加\n");
            long count1 = mongoTemplate.count(new Query(), "usergroup");
            int[] groupsid = new int[1];
            groupsid[0] = existGroup.getId();

            Usergroup usergroup = new Usergroup(
                    new ObjectId(),
                    (int) count1 + 32800,
                    user.getId(),
                    groupsid
            );
            usergroupDao.save(usergroup);
        } else {
            System.out.println("");
            int[] joinedGroups = existUserGroup.getGroupsid();
            for (int joinedGroup : joinedGroups) {
                if (existGroup.getId() == joinedGroup) {
                    System.out.println("群聊存在，但已经添加了，添加失败\n");
                    msg = "该群聊已经添加！";
                    map.put("code", code);
                    map.put("msg", msg);
                    return map;
                }
            }

            System.out.println("用户未加入该群聊\n");
            System.out.println("用户群组索引存在，更新\n");
            int[] newGroups = Arrays.copyOf(existUserGroup.getGroupsid(), existUserGroup.getGroupsid().length + 1);
            newGroups[newGroups.length - 1] = existGroup.getId();
            existUserGroup.setGroupsid(newGroups);
            usergroupDao.save(existUserGroup);
        }
        System.out.println("如果不存在，则新增用户群组索引，如果存在，则更新\n");


        String[] newMembers = Arrays.copyOf(existGroup.getMembers(), existGroup.getMembers().length + 1);
        newMembers[newMembers.length - 1] = username;
        int[] newMemberss = Arrays.copyOf(existGroup.getMemberss(), existGroup.getMemberss().length + 1);
        newMemberss[newMemberss.length - 1] = user.getId();
        String[] newAvatars = Arrays.copyOf(existGroup.getAvatars(), existGroup.getAvatars().length + 1);
        newAvatars[newAvatars.length - 1] = user.getAvatar();
        existGroup.setMembers(newMembers);
        existGroup.setMemberss(newMemberss);
        existGroup.setAvatars(newAvatars);
        groupsDao.save(existGroup);
        System.out.println("更新群聊本身的成员\n");

        code = ResultEnum.SUCCESS.getCode();
        msg = ResultEnum.SUCCESS.getMessage();

        map.put("code", code);
        map.put("msg", msg);
        map.put("data", existGroup);
        return map;
    }

    public Map<String, Object> delete(String username, String name) {
        System.out.println("退出群聊的serve\n");
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Info user = infoDao.findInfoByUsername(username);
        Groups group = groupsDao.findGroupsByName(name);
        if (user != null && group != null) {
            System.out.println("user 和 group 均存在\n");

            String[] members = group.getMembers();
            String[] newMembers = new String[members.length - 1];
            System.out.println("newMembers length = " + newMembers.length);
            int index = 0;
            int pos = 0;
            if (newMembers.length != 0) {
                for (String member : members) {
                    if (!member.equals(username)) {
                        newMembers[index++] = member;
                    } else {
                        pos = index;
                    }
                }
            }

            int[] memberss = group.getMemberss();
            int[] newMemberss = new int[memberss.length - 1];
            index = 0;
            if (newMemberss.length != 0) {
                for (int k : memberss) {
                    if (k != user.getId()) {
                        newMemberss[index++] = k;
                    }
                }
            }

            String[] avatars = group.getMembers();
            String[] newAvatars = new String[members.length - 1];
            index = 0;
            // 这里有问题，头像又不是不可以一样
            if (newAvatars.length != 0) {
                for (int i = 0; i < avatars.length; i++) {
                    if (i != pos) {
                        newAvatars[index++] = avatars[i];
                    }
                }
                // 这里没有一个相等的，导致越界了
//                for (String avatar : avatars) {
//                    if (!avatar.equals(user.getAvatar())) {
//                        newMembers[index++] = avatar;
//                    }
//                }
            }


            group.setMembers(newMembers);
            group.setMemberss(newMemberss);
            group.setAvatars(newAvatars);
            groupsDao.save(group);
            System.out.println("从群聊中删除用户信息\n");

            Usergroup userGroup = usergroupDao.findUsergroupByUserid(user.getId());
            int[] groupsId = userGroup.getGroupsid();
            int[] newGroupId = new int[groupsId.length - 1];
            index = 0;
            if (newGroupId.length != 0) {
                for (int j : groupsId) {
                    if (j != group.getId()) {
                        newGroupId[index++] = j;
                    }
                }
            }

            userGroup.setGroupsid(newGroupId);
            usergroupDao.save(userGroup);
            System.out.println("用户群聊索引表中删除群聊信息\n");

            // ====删除用户会话
            System.out.println("user.getId() = " + user.getId());
            System.out.println("group.getId() = " + group.getId());
            Conversation conversation = conversationDao.findByFromAndTo(user.getId(), group.getId());
            if (conversation != null) {
                System.out.println("find conversation");
                conversationDao.delete(conversation);
            }


            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        }

        map.put("code", code);
        map.put("msg", msg);
        return map;

    }
}
