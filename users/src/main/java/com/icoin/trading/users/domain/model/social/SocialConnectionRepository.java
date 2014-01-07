package com.icoin.trading.users.domain.model.social;

import com.homhon.base.domain.repository.GenericCrudRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-6-15
 * Time: AM10:14
 * To change this template use File | Settings | File Templates.
 */

public interface SocialConnectionRepository<T extends SocialConnection, ID extends Serializable> extends GenericCrudRepository<T, ID> {

    int findMaxRank(String userId, String providerId);

    void remove(String userId, String providerId, String providerUserId);

    void remove(String userId, String providerId);

    T findPrimaryConnection(String userId,
                            String providerId);

    T findConnection(String userId,
                     String providerId, String providerUserId);

    List<T> findConnections(String userId);

    List<T> findConnections(String userId,
                            String providerId);

    List<T> findConnections(String userId,
                            Map<String, List<String>> providerUsers);

    Set<ID> findUserIds(String providerId,
                        Set<String> providerUserIds);

    List<ID> findUserIds(String providerId,
                         String providerUserId);

}
