package com.fengfengzi.chatserver.dao;

import com.fengfengzi.chatserver.pojo.Apply;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author 王丰
 * @version 1.0
 */
public interface ApplyDao extends MongoRepository<Apply, String> {
    List<Apply> findByApplicantOrReviewer(int applicant, int reviewer);
    List<Apply> findByApplicantAndReviewer(int applicant, int reviewer);
    Apply findApplyById(int id);
}
