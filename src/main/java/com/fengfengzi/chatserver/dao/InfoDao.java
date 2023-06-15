package com.fengfengzi.chatserver.dao;

import com.fengfengzi.chatserver.pojo.Info;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 王丰
 * @version 1.0
 */
public interface InfoDao extends MongoRepository<Info, String> {
    Info findInfoById(int id);
    Info findInfoByUsername(String username);
}
