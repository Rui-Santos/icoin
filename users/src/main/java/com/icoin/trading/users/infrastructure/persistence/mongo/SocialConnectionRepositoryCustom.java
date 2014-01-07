package com.icoin.trading.users.infrastructure.persistence.mongo;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-6-21
 * Time: PM8:57
 * To change this template use File | Settings | File Templates.
 */

import com.icoin.trading.users.domain.model.social.SocialConnection;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jihual
 * Date: 6/21/13
 * Time: 10:40 AM
 * This.
 *
 * @author jihual
 */
public interface SocialConnectionRepositoryCustom<T extends SocialConnection, ID extends Serializable> {
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