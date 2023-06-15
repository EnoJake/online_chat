package com.fengfengzi.chatserver.dao;

import com.fengfengzi.chatserver.pojo.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author 王丰
 * @version 1.0
 */
public interface GroupDao extends MongoRepository<Group, String> {
    List<Group> findByCode(int code);
    Group findGroupById(int id);
}
