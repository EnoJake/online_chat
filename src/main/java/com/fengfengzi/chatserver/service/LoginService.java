package com.fengfengzi.chatserver.service;

import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.dao.GroupDao;
import com.fengfengzi.chatserver.dao.InfoDao;
import com.fengfengzi.chatserver.pojo.Group;
import com.fengfengzi.chatserver.pojo.Info;
import com.fengfengzi.chatserver.pojo.MessageDB;
import com.fengfengzi.chatserver.pojo.User;
import com.fengfengzi.chatserver.pojo.vo.LoginRequestVo;
import com.fengfengzi.chatserver.pojo.vo.RegisterRequestVo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 王丰
 * @version 1.0
 */

@Service
public class LoginService {

    @Resource
    InfoDao infoDao;

    @Resource
    GroupDao groupDao;

    @Autowired
    MongoTemplate mongoTemplate;


    /**
     * 登录服务
     * */
    public Map<String, Object> login(LoginRequestVo lVo) {
        Map<String, Object> map = new HashMap<>();
        Integer code;
        String msg;

        Info existUser = infoDao.findInfoByUsername(lVo.getUsername());
        if (existUser == null) {
            code = ResultEnum.ACCOUNT_NOT_FOUND.getCode();
            msg = ResultEnum.ACCOUNT_NOT_FOUND.getMessage();
            System.out.println("-->账号不存在\n");
        } else if (!lVo.getPassword().equals(existUser.getPassword())) {
            code = ResultEnum.USER_LOGIN_FAILED.getCode();
            msg = ResultEnum.USER_LOGIN_FAILED.getMessage();
            System.out.println("-->登录失败\n");
        } else {
            code = ResultEnum.LOGIN_SUCCESS.getCode();
            msg = ResultEnum.LOGIN_SUCCESS.getMessage();
        }

        map.put("code", code);
        map.put("msg", msg);
        return map;
    }

    /**
     * 注册
     * */
    public Map<String, Object> register(RegisterRequestVo rVo) {
        Map<String, Object> map = new HashMap<>();
        Integer code;
        String msg;

        Info existUser = infoDao.findInfoByUsername(rVo.getUsername());
        if (existUser != null) {
            code = ResultEnum.USER_HAS_EXIST.getCode();
            msg = ResultEnum.USER_HAS_EXIST.getMessage();
            System.out.println("-->账号已存在\n");
        } else {
            // 个人信息
            long count = mongoTemplate.count(new Query(), "info");
            int user_id = (int) (count + 2500);

            Info user = new Info(

            );
            user.set_id(new ObjectId());
            user.setUsername(rVo.getUsername());
            user.setNickname(rVo.getUsername());
            user.setPassword(rVo.getPassword());
            // =====这里加上随机头像=====
            int num = (int)(Math.random() * 22) + 1;
            String strNum = Integer.toString(num);
            String image = "http://124.223.50.19:8080/face/face" + strNum + ".jpg";
            user.setAvatar(image);

            // =====
            user.setSex(2);
            user.setId(user_id);

            infoDao.save(user);
            System.out.println("-->新账号：" + user.getUsername() + " -- 保存到数据库\n");


            // 分组信息
            count = mongoTemplate.count(new Query(), "group");
            List<Group> groups = groupDao.findAll();
            int group_id;

            if (count == 0) {
                group_id = 3500;
            } else {
                group_id = groups.get(groups.size() - 1).getId() + 1;
            }

            Group group = new Group(
                    new ObjectId(),
                    group_id,
                    "我的好友",
                    0, // 不能删除
                    user_id,
                    user_id,
                    new int[0]);
            groupDao.save(group);
            System.out.println("-->生成默认好友分组：我的好友\n");

            if (user.get_id() != null) {
                code = ResultEnum.REGISTER_SUCCESS.getCode();
                msg = ResultEnum.REGISTER_SUCCESS.getMessage();
            } else {
                code = ResultEnum.REGISTER_FAILED.getCode();
                msg = ResultEnum.REGISTER_FAILED.getMessage();
            }
        }

        map.put("code", code);
        map.put("msg", msg);
        return map;
    }
}
