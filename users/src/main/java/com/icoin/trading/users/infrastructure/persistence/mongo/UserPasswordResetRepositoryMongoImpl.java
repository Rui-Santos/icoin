package com.icoin.trading.users.infrastructure.persistence.mongo;

import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.hasText;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-6-20
 * Time: PM8:53
 * To change this template use File | Settings | File Templates.
 */
public class UserPasswordResetRepositoryMongoImpl implements UserPasswordResetRepositoryCustom {

    private MongoTemplate mongoTemplate;

    @Resource(name = "users.mongoTemplate")
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<UserPasswordReset> findNotExpiredByEmail(String email, String ip, Date fromDate, Date currentDate) {
        hasText(email);
        hasText(ip);
        notNull(fromDate);
        notNull(currentDate);

        final Query query = new Query()
                .addCriteria(Criteria.where("email").is(email))
                .addCriteria(Criteria.where("ip").is(ip))
                .addCriteria(Criteria.where("expirationDate").gte(fromDate).lt(currentDate))
                .with(new Sort(Sort.Direction.DESC, "expirationDate"));

        final List<UserPasswordReset> resets = mongoTemplate.find(query, UserPasswordReset.class);

        return resets;
    }


    public List<UserPasswordReset> findNotExpiredByEmail(String email, Date fromDate, Date currentDate) {
        hasText(email);
        notNull(fromDate);
        notNull(currentDate);

        final Query query = new Query()
                .addCriteria(Criteria.where("email").is(email))
                .addCriteria(Criteria.where("expirationDate").gte(fromDate).lt(currentDate))
                .with(new Sort(Sort.Direction.DESC, "expirationDate"));

        final List<UserPasswordReset> resets = mongoTemplate.find(query, UserPasswordReset.class);

        return resets;
    }
}