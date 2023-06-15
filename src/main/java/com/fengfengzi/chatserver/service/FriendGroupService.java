package com.fengfengzi.chatserver.service;

import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.dao.GroupDao;
import com.fengfengzi.chatserver.dao.InfoDao;
import com.fengfengzi.chatserver.pojo.Group;
import com.fengfengzi.chatserver.pojo.GroupResponse;
import com.fengfengzi.chatserver.pojo.Info;
import com.fengfengzi.chatserver.pojo.vo.FriendGroupCreateResponseVo;
import com.fengfengzi.chatserver.pojo.vo.GroupFriendResponseVo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 王丰
 * @version 1.0
 */

@Service
public class FriendGroupService {

    @Resource
    InfoDao infoDao;

    @Resource
    GroupDao groupDao;

    @Autowired
    MongoTemplate mongoTemplate;

    public Map<String, Object> list(String username) {
        Map<String, Object> map = new HashMap<>();
        Integer code;
        String msg;

        Info user = infoDao.findInfoByUsername(username);
        if (user == null) {
            code = ResultEnum.ERROR.getCode();
            msg = ResultEnum.ERROR.getMessage();
        } else {
            List<Group> originalData = groupDao.findByCode(user.getId());
            List<GroupResponse> data = new ArrayList<>();

            for (Group group : originalData) {

                // 好友数组转Info
                List<Info> friends = new ArrayList<>();
                int[] originalFriends = group.getFriends();
                for (int originalFriend : originalFriends) {
                    Info friend = infoDao.findInfoById(originalFriend);
                    friends.add(friend);
                }
                System.out.println("好友数组转Info\n");

                // 生成response
                GroupResponse response = new GroupResponse(
                        group.getCan_deleted(),
                        "2023-05-20 12:30:25",
                        friends,
                        group.getId(),
                        group.getName(),
                        group.getUserId()
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

    public Map<String, Object> add(String username, String groupName) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();


        Info user = infoDao.findInfoByUsername(username);
        if (user != null) {
            // 获取长度生成id
            List<Group> groups = groupDao.findAll();
            long count = groups.get(groups.size() - 1).getId() + 1;

            Group group = new Group(
                    new ObjectId(),
                    (int) count,
                    groupName,
                    1,
                    user.getId(),
                    user.getId(),
                    new int[0]
            );
            groupDao.save(group);

            FriendGroupCreateResponseVo data = new FriendGroupCreateResponseVo(
                    1,
                    "2023-05-22 12:34:56",
                    (int) count, // 这行出了bug，   + 3500
                    groupName,
                    user.getId()
            );
            map.put("data", data);
            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        }

        map.put("code", code);
        map.put("msg", msg);

        return map;
    }

    public Map<String, Object> delete(int id) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Group group = groupDao.findGroupById(id);
        if (group != null) {

            if (group.getFriends().length == 0) {
                groupDao.delete(group);
                System.out.println("删除该分组记录数据\n");

                code = ResultEnum.SUCCESS.getCode();
                msg = ResultEnum.SUCCESS.getMessage();
            } else {
                System.out.println("分组内尚有好友，无法删除\n");
                msg = "请先删除分组中的好友";
            }


        }

        map.put("code", code);
        map.put("msg", msg);


        return map;
    }
}
