package com.fengfengzi.chatserver.dao;

import com.fengfengzi.chatserver.pojo.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author 王丰
 * @version 1.0
 */
public interface ConversationDao extends MongoRepository<Conversation, String> {
    List<Conversation> findByFrom(int from);
    Conversation findByFromAndTo(int from, int to);
    Conversation findByFromAndMessage(int from, int message);
    Conversation findConversationById(int id);
    //List<Conversation> findByCodeaOrCodeb(int codea, int codeb);
    //Conversation findByCodeaAndCodeb(int codea, int codeb);
}
