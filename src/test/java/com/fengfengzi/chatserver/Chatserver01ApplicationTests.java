package com.fengfengzi.chatserver;

//import com.fengfengzi.chatserver.pojo.User;
import com.fengfengzi.chatserver.pojo.MessageDB;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

@SpringBootTest
class Chatserver01ApplicationTests {

//	@Autowired
//	private MongoTemplate mongoTemplate;

//	@Test
//	void contextLoads() {
//		User user = new User();
//		user.set_id(new ObjectId());
//		user.setUsername("Catherine");
//		user.setPassword("8dhf3uy44");
//		mongoTemplate.save(user);
//	}

//	@Test
//	void find() {
//		List<User> all = mongoTemplate.findAll(User.class);
//		System.out.println(all);
//
//	}
//    @Resource
//    NewMessageDao newMessageDao;

    @Test
    void dbTest() {

        MessageDB message = new MessageDB(
                new ObjectId(),
                "",
                "这是第一条消息的内容部分",
                System.currentTimeMillis(),
                2500,
                12200,
                1,
                2501,
                1,
                ""
        );

//        newMessageDao.save(message);
    }

}
