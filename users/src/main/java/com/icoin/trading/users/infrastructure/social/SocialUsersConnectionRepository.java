package com.icoin.trading.users.infrastructure.social;

import com.icoin.trading.users.infrastructure.persistence.mongo.SocialConnectionRepositoryMongo;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Resource(name= "users.usersConnectionRepository")
public class SocialUsersConnectionRepository implements UsersConnectionRepository {

    private final SocialConnectionRepositoryMongo userSocialConnectionRepository;

    private final ConnectionFactoryLocator connectionFactoryLocator;

    private final TextEncryptor textEncryptor;

    private ConnectionSignUp connectionSignUp;

    public SocialUsersConnectionRepository(SocialConnectionRepositoryMongo userSocialConnectionRepository,
                                           ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor) {
        this.userSocialConnectionRepository = userSocialConnectionRepository;
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.textEncryptor = textEncryptor;
    }

    /**
     * The command to execute to create a new local user profile in the event no user id could be mapped to a connection.
     * Allows for implicitly creating a user profile from connection data during a provider sign-in attempt.
     * Defaults to null, indicating explicit sign-up will be required to complete the provider sign-in attempt.
     *
     * @see #findUserIdsWithConnection(Connection)
     */
    public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
        this.connectionSignUp = connectionSignUp;
    }

    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        ConnectionKey key = connection.getKey();
        List<String> localUserIds =
                userSocialConnectionRepository.findUserIds(key.getProviderId(), key.getProviderUserId());

        if (localUserIds.size() == 0 && connectionSignUp != null) {
            String newUserId = connectionSignUp.execute(connection);
            if (newUserId != null) {
                createConnectionRepository(newUserId).addConnection(connection);
                return Arrays.asList(newUserId);
            }
        }
        return localUserIds;
    }

    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        return userSocialConnectionRepository.findUserIds(providerId, providerUserIds);
    }

    public ConnectionRepository createConnectionRepository(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        return new SocialConnectionRepositoryAdapter(userId, userSocialConnectionRepository, connectionFactoryLocator, textEncryptor);
    }

}
