package com.fengfengzi.chatserver.dao;

import com.fengfengzi.chatserver.pojo.Usergroup;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 王丰
 * @version 1.0
 */
public interface UsergroupDao extends MongoRepository<Usergroup, String> {
    Usergroup findUsergroupByUserid(int userid);

}
