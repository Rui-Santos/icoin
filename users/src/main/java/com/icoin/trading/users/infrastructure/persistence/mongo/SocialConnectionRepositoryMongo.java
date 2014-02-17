package com.icoin.trading.users.infrastructure.persistence.mongo;

import com.icoin.trading.users.domain.model.social.SocialConnection;
import com.icoin.trading.users.domain.model.social.SocialConnectionRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-6-20
 * Time: PM8:53
 * To change this template use File | Settings | File Templates.
 */
public interface SocialConnectionRepositoryMongo extends SocialConnectionRepository<SocialConnection, String>, SocialConnectionRepositoryCustom<SocialConnection, String>,
        CrudRepository<SocialConnection, String> {
}