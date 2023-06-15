package com.fengfengzi.chatserver.dao;

import com.fengfengzi.chatserver.pojo.Groups;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 王丰
 * @version 1.0
 */
public interface GroupsDao extends MongoRepository<Groups, String> {
    Groups findGroupsById(int id);
    Groups findGroupsByName(String name);
}
