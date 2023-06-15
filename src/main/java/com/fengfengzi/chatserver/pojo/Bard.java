package com.fengfengzi.chatserver.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author 王丰
 * @version 1.0
 */

@Data
@NoArgsConstructor
@Document(collection = "bard")
public class Bard {
    @Id
    private ObjectId _id;
    @Indexed(unique = true)
    private String writer;
    private String work;
}
