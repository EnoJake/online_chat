package com.fengfengzi.chatserver.dao;

import com.fengfengzi.chatserver.pojo.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 王丰
 * @version 1.0
 */
public interface UserDao extends MongoRepository<User, ObjectId> {
    User findUserByUsername(String username);
    User findUserByCode(String code);
}
