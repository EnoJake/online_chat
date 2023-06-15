package com.fengfengzi.chatserver.dao;

import com.fengfengzi.chatserver.pojo.Message;
// import com.fengfengzi.chatserver.pojo.vo.MessageVo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import java.util.List;

/**
 * @author 王丰
 * @version 1.0
 */
public interface MessageDao extends MongoRepository<Message, String> {
    List<Message> findByFromOrTo(int from, int to);
    List<Message> findByFromAndTo(int from, int to);
    Message findMessageById(int id);
    List<Message> findByTo(int to);
}