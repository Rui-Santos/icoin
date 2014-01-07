package com.icoin.trading.users.infrastructure.persistence.mongo;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-6-21
 * Time: PM8:56
 * To change this template use File | Settings | File Templates.
 */

import com.icoin.trading.users.domain.model.social.SocialConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.util.Assert.hasLength;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

public class SocialConnectionRepositoryMongoImpl implements SocialConnectionRepositoryCustom<SocialConnection, String> {
    public static final int PRIMARY_VALUE = 1;
    private static Logger logger = LoggerFactory.getLogger(SocialConnectionRepositoryMongoImpl.class);

    @Resource(name = "users.mongoTemplate")
    private MongoTemplate mongoTemplate;

    /**
     * Returns the max connection rank for the user and the provider.
     *
     * @see com.icoin.trading.users.domain.model.social.SocialConnectionRepository#findMaxRank(String, String)
     */
    @Override
    public int findMaxRank(String userId, String providerId) {
        hasLength(userId);
        hasLength(providerId);

        Query q = query(where("userId").is(userId)
                .and("providerId").is(providerId))
                .with(new Sort(Sort.Direction.DESC, "rank"));
        SocialConnection cnn = findOne(q);

        if (cnn == null)
            return PRIMARY_VALUE;

        return cnn.getRank() + 1;
    }

    private SocialConnection findOne(Query query) {
        notNull(query);
        logger.info("Find one {} by query {}", SocialConnection.class, query);
        SocialConnection result = mongoTemplate.findOne(query, SocialConnection.class);
        logger.info("Found one {} by query {}, result is {}", SocialConnection.class, query, result);
        return result;
    }

    /**
     * Remove a connection.
     *
     * @see com.icoin.trading.users.domain.model.social.SocialConnectionRepository#remove(String, String, String)
     */
    @Override
    public void remove(String userId, String providerId, String providerUserId) {
        hasLength(userId);
        hasLength(providerId);
        hasLength(providerUserId);

        Query q = query(where("userId").is(userId)
                .and("providerId").is(providerId)
                .and("providerUserId").is(providerUserId));
        remove(q);
    }

    /**
     * Remove all the connections for a user on a provider.
     *
     * @see com.icoin.trading.users.domain.model.social.SocialConnectionRepository#remove(String, String)
     */
    @Override
    public void remove(String userId, String providerId) {
        hasLength(userId);
        hasLength(providerId);

        Query q = query(where("userId").is(userId)
                .and("providerId").is(providerId));

        remove(q);
    }

    private void remove(Query query) {
        logger.info("remove {} by query {}", SocialConnection.class, query);
        mongoTemplate.remove(query, SocialConnection.class);
        logger.info("Removed {} by query {}", SocialConnection.class, query);

    }

    /**
     * Return the primary connection.
     *
     * @see com.icoin.trading.users.domain.model.social.SocialConnectionRepository#findPrimaryConnection(String, String)
     */
    @Override
    public SocialConnection findPrimaryConnection(String userId, String providerId) {
        hasLength(userId);
        hasLength(providerId);

        Query q = query(where("userId").is(userId).
                and("providerId").is(providerId).
                and("rank").is(PRIMARY_VALUE));

        return findOne(q);
    }

    /**
     * Get the connection for user, provider and provider user id.
     *
     * @see com.icoin.trading.users.domain.model.social.SocialConnectionRepository#findConnection(String, String, String)
     */
    @Override
    public SocialConnection findConnection(String userId, String providerId, String providerUserId) {
        hasLength(userId);
        hasLength(providerId);
        hasLength(providerUserId);

        Query q = query(where("userId").is(userId)
                .and("providerId").is(providerId)
                .and("providerUserId").is(providerUserId));

        return findOne(q);
    }

    /**
     * Get all the connections for an user id.
     *
     * @see com.icoin.trading.users.domain.model.social.SocialConnectionRepository#findConnections(String)
     */
    @Override
    public List<SocialConnection> findConnections(String userId) {
        hasLength(userId);

        Query q = query(where("userId").is(userId))
                .with(new Sort(Sort.Direction.ASC, "providerId", "rank"));
        return find(q);
    }

    /**
     * Get all the connections for an user id on a provider.
     *
     * @see com.icoin.trading.users.domain.model.social.SocialConnectionRepository#findConnections(String, String)
     */
    @Override
    public List<SocialConnection> findConnections(String userId, String providerId) {
        hasLength(userId);
        hasLength(providerId);

        Query q = new Query(where("userId").is(userId)
                .and("providerId").is(providerId))
                .with(new Sort(Sort.Direction.ASC, "rank"));

        return find(q);
    }

    /**
     * Get all the connections for an user.
     *
     * @see com.icoin.trading.users.domain.model.social.SocialConnectionRepository#findConnections(String, java.util.Map)
     */
    @Override
    public List<SocialConnection> findConnections(String userId, Map<String, List<String>> providerUsers) {
        hasLength(userId);
        notEmpty(providerUsers);

        if (providerUsers == null || providerUsers.isEmpty()) {
            throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
        }

        List<Criteria> lc = new ArrayList<Criteria>();
        for (Map.Entry<String, List<String>> entry : providerUsers.entrySet()) {
            String providerId = entry.getKey();

            lc.add(where("providerId").is(providerId)
                    .and("providerUserId").in(entry.getValue()));
        }

        Query q = new Query(where("userId").is(userId)
                .orOperator(lc.toArray(new Criteria[lc.size()])))
                .with(new Sort(Sort.Direction.ASC, "providerId", "rank"));

        return find(q);
    }

    /**
     * Get the user ids on the provider.
     *
     * @see com.icoin.trading.users.domain.model.social.SocialConnectionRepository#findUserIds(String, java.util.Set)
     */
    @Override
    public Set<String> findUserIds(String providerId, Set<String> providerUserIds) {
        Query q = query(where("providerId").is(providerId)
                .and("providerUserId").in(new ArrayList<String>(providerUserIds)));

        q.fields().include("userId");

        List<? extends SocialConnection> results = mongoTemplate.find(q, SocialConnection.class);
        Set<String> userIds = new HashSet<String>();
        for (SocialConnection mc : results) {
            userIds.add(mc.getUserId());
        }

        return userIds;
    }

    /**
     * Get the user ids on the provider with a given provider user id.
     *
     * @see com.icoin.trading.users.domain.model.social.SocialConnectionRepository#findUserIds(String, String)
     */
    @Override
    public List<String> findUserIds(String providerId, String providerUserId) {
        Query q = query(where("providerId").is(providerId)
                .and("providerUserId").is(providerUserId));

        q.fields().include("userId");

        List<? extends SocialConnection> results = mongoTemplate.find(q, SocialConnection.class);
        List<String> userIds = new ArrayList<String>();
        for (SocialConnection mc : results) {
            userIds.add(mc.getUserId());
        }

        return userIds;
    }

    private List<SocialConnection> find(Query query) {
        logger.info("Find {} by query {}", SocialConnection.class, query);
        List<SocialConnection> results = mongoTemplate.find(query, SocialConnection.class);
        logger.info("Found {} by query {}, result size is {}", SocialConnection.class, query, results == null ? 0 : results.size());
        return results;
    }
}