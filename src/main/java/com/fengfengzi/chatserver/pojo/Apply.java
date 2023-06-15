package com.fengfengzi.chatserver.pojo;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Document(collection = "apply")
public class Apply {
    @Id
    private ObjectId _id;
    private int id; // 唯一，用于检索
    private int applicant;
    private int reviewer;
    private int group_id;
    private int code;
    private String remark;
    private int status;
}
