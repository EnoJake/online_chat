package com.fengfengzi.chatserver.service;

import com.fengfengzi.chatserver.common.ResultEnum;
import com.fengfengzi.chatserver.dao.InfoDao;
import com.fengfengzi.chatserver.dao.MessageDao;
import com.fengfengzi.chatserver.dao.UserDao;
import com.fengfengzi.chatserver.pojo.Group;
import com.fengfengzi.chatserver.pojo.Info;
import com.fengfengzi.chatserver.pojo.User;
import com.fengfengzi.chatserver.pojo.vo.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 王丰
 * @version 1.0
 */

@Service
public class UserService {

    @Resource
    InfoDao infoDao;

    @Autowired
    MongoTemplate mongoTemplate;

    @Resource
    MessageDao messageDao;

    /**
     * 注册服务
     * */
//    public Map<String, Object> register(RegisterRequestVo rVo) {
//        Map<String, Object> map = new HashMap<>();
//        Integer code;
//        String msg;
//
//
//        User existUser = userDao.findUserByUsername(rVo.getUsername());
//
//        if (existUser != null) {
//            System.out.println("得到userDao");
//            code = ResultEnum.USER_HAS_EXIST.getCode();
//            msg = ResultEnum.USER_HAS_EXIST.getMessage();
//        } else {
//            User user = new User();
//            user.set_id(new ObjectId());
//            user.setUsername(rVo.getUsername());
//            user.setPassword(rVo.getPassword());
//            user.setGroups(new Group[]{new Group()});
//
//            long count = mongoTemplate.count(new Query(), "users0");
//            String user_id = Integer.toString((int) (count + 2500));
//            user.setUser_id(user_id);
//            // ===== add
//            user.setCode(user_id);
//            // =====
//            userDao.save(user);
//
//            System.out.println("user_id = " + user_id);
//
//            if (user.get_id() != null) {
//                code = ResultEnum.REGISTER_SUCCESS.getCode();
//                msg = ResultEnum.REGISTER_SUCCESS.getMessage();
//            } else {
//                code = ResultEnum.REGISTER_FAILED.getCode();
//                msg = ResultEnum.REGISTER_FAILED.getMessage();
//            }
//        }
//        map.put("username", rVo.getUsername());
//        map.put("code", code);
//        map.put("msg", msg);
//        return map;
//    }


    /**
     * 登录服务
     * */
//    public Map<String, Object> login(LoginRequestVo lVo) {
//        Map<String, Object> map = new HashMap<>();
//        Integer code;
//        String msg;
//        System.out.println("come in login serve");
//        User existUser = userDao.findUserByUsername(lVo.getUsername());
//        System.out.println("after dao");
//        if (existUser == null) {
//            code = ResultEnum.ACCOUNT_NOT_FOUND.getCode();
//            msg = ResultEnum.ACCOUNT_NOT_FOUND.getMessage();
//        } else if (!lVo.getPassword().equals(existUser.getPassword())){
//            code = ResultEnum.USER_LOGIN_FAILED.getCode();
//            msg = ResultEnum.USER_LOGIN_FAILED.getMessage();
//        } else {
//            code = ResultEnum.LOGIN_SUCCESS.getCode();
//            msg = ResultEnum.LOGIN_SUCCESS.getMessage();
//
//            System.out.println("login success");
//            map.put("user_id", existUser.getUser_id());
//        }
//
//
//
//        map.put("username", lVo.getUsername());
//        map.put("code", code);
//        map.put("msg", msg);
//        return map;
//
//    }

    public Map<String, Object> selfInfo(String username) {
        Map<String, Object> map = new HashMap<>();
        Integer code;
        String msg;

        Info user = infoDao.findInfoByUsername(username);
        if (user == null) {
            code = ResultEnum.ERROR.getCode();
            msg = ResultEnum.ERROR.getMessage();
            System.out.println("账号查询失败\n");
        } else {
            // 密码别给出去了
            user.setPassword(null);
            System.out.println("密码隐藏\n");

            map.put("data", user);
            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        }

        map.put("code", code);
        map.put("msg", msg);
        return map;
    }


    public Map<String, Object> editPassword(String username, EditPasswordVo eVo) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Info user = infoDao.findInfoByUsername(username);
        if (user == null) {
            code = ResultEnum.ERROR.getCode();
            msg = ResultEnum.ERROR.getMessage();
            System.out.println("账号查询失败\n");
        } else if (!user.getPassword().equals(eVo.getOld_password())){
            code = ResultEnum.OLD_PASSWORD_ERROR.getCode();
            msg = ResultEnum.OLD_PASSWORD_ERROR.getMessage();
            System.out.println("原始密码输入错误\n");
        } else {
            user.setPassword(eVo.getNew_password());
            infoDao.save(user);
            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        }

        map.put("code", code);
        map.put("msg", msg);
        return map;
    }

    public Map<String, Object> editBasic(String username, EditBasicVo eVo) {
        Map<String, Object> map = new HashMap<>();
        Integer code = ResultEnum.ERROR.getCode();
        String msg = ResultEnum.ERROR.getMessage();

        Info user = infoDao.findInfoByUsername(username);
        if (user == null) {
            code = ResultEnum.ERROR.getCode();
            msg = ResultEnum.ERROR.getMessage();
            System.out.println("账号查询失败\n");
        } else {
            user.setAvatar(eVo.getAvatar());
            user.setNickname(eVo.getNickname());
            user.setSex(eVo.getSex());
            infoDao.save(user);
            System.out.println("允许修改个人信息\n");

            code = ResultEnum.SUCCESS.getCode();
            msg = ResultEnum.SUCCESS.getMessage();
        }

        map.put("code", code);
        map.put("msg", msg);
        return map;

    }
}
