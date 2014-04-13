package com.icoin.trading.users.infrastructure.social;

import com.google.common.collect.Lists;
import com.icoin.trading.users.domain.model.social.SocialConnection;
import com.icoin.trading.users.infrastructure.persistence.mongo.SocialConnectionRepositoryMongo;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.springframework.util.Assert.notEmpty;

/**
 * User: jihual
 * Date: 6/20/13
 * Time: 3:54 PM
 * This is an adapter to map with spring social framework.
 *
 * @author jihual
 */
public class SocialConnectionRepositoryAdapter implements ConnectionRepository {

    private final String userId;

    private final SocialConnectionRepositoryMongo socialConnectionRepository;

    private final ConnectionFactoryLocator connectionFactoryLocator;

    private final TextEncryptor textEncryptor;

    public SocialConnectionRepositoryAdapter(String userId, SocialConnectionRepositoryMongo socialConnectionRepository,
                                             ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor) {
        this.userId = userId;
        this.socialConnectionRepository = socialConnectionRepository;
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.textEncryptor = textEncryptor;
    }

    public MultiValueMap<String, Connection<?>> findAllConnections() {
        List<SocialConnection> userSocialConnectionList = socialConnectionRepository
                .findConnections(userId);

        MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
        Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
        for (String registeredProviderId : registeredProviderIds) {
            connections.put(registeredProviderId, Collections.<Connection<?>>emptyList());
        }
        for (SocialConnection userSocialConnection : userSocialConnectionList) {
            String providerId = userSocialConnection.getProviderId();
            if (connections.get(providerId).size() == 0) {
                connections.put(providerId, new LinkedList<Connection<?>>());
            }
            connections.add(providerId, buildConnection(userSocialConnection));
        }
        return connections;
    }

    public List<Connection<?>> findConnections(String providerId) {
        List<Connection<?>> resultList = new LinkedList<Connection<?>>();
        List<SocialConnection> userSocialConnectionList = this.socialConnectionRepository
                .findConnections(userId, providerId);
        for (SocialConnection userSocialConnection : userSocialConnectionList) {
            resultList.add(buildConnection(userSocialConnection));
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    public <A> List<Connection<A>> findConnections(Class<A> apiType) {
        List<?> connections = findConnections(getProviderId(apiType));
        return (List<Connection<A>>) connections;
    }

    public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUsers) {
        notEmpty(providerUsers, "Unable to execute find: no providerUsers provided");
        List<SocialConnection> resultList =
                socialConnectionRepository.findConnections(userId, providerUsers);

        MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<String, Connection<?>>();

        for (SocialConnection socialConnection : resultList) {
            Connection<?> connection = buildConnection(socialConnection);

            String providerId = connection.getKey().getProviderId();
            List<String> inputProvideUserIds = providerUsers.get(providerId);
            List<Connection<?>> connections = connectionsForUsers.get(providerId);
            if (connections == null) {
                connections = Lists.newArrayListWithCapacity(inputProvideUserIds.size());
                connectionsForUsers.put(providerId, connections);
            }


            String providerUserId = connection.getKey().getProviderUserId();
            int connectionIndex = inputProvideUserIds.indexOf(providerUserId);
            connections.set(connectionIndex, connection);
        }

        return connectionsForUsers;
    }

    public Connection<?> getConnection(ConnectionKey connectionKey) {
        SocialConnection userSocialConnection = socialConnectionRepository
                .findConnection(userId, connectionKey.getProviderId(),
                        connectionKey.getProviderUserId());
        if (userSocialConnection != null) {
            return buildConnection(userSocialConnection);
        }
        throw new NoSuchConnectionException(connectionKey);
    }

    @SuppressWarnings("unchecked")
    public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
        String providerId = getProviderId(apiType);
        return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
    }

    @SuppressWarnings("unchecked")
    public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
        String providerId = getProviderId(apiType);
        Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
        if (connection == null) {
            throw new NotConnectedException(providerId);
        }
        return connection;
    }

    @SuppressWarnings("unchecked")
    public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
        String providerId = getProviderId(apiType);
        return (Connection<A>) findPrimaryConnection(providerId);
    }

    public void addConnection(Connection<?> connection) {
        int rank = socialConnectionRepository.findMaxRank(userId, connection.getKey().getProviderId());


        SocialConnection userSocialConnection = socialConnectionRepository
                .findConnection(userId, connection.getKey().getProviderId(),
                        connection.getKey().getProviderUserId());
        if (userSocialConnection == null) {
            //not allow one providerId connect to multiple userId
            ConnectionData data = connection.createData();
            SocialConnection socialConnection = createSocialConnection(data);
            socialConnection.setRank(rank);
            socialConnectionRepository.save(socialConnection);
        } else {
            throw new DuplicateConnectionException(connection.getKey());
        }
    }

    public void updateConnection(Connection<?> connection) {
        ConnectionData data = connection.createData();
        SocialConnection socialConnection = socialConnectionRepository
                .findConnection(userId, connection.getKey().getProviderId(), connection
                        .getKey().getProviderUserId());
        if (socialConnection != null) {
            socialConnection.setDisplayName(data.getDisplayName());
            socialConnection.setProfileUrl(data.getProfileUrl());
            socialConnection.setImageUrl(data.getImageUrl());
            socialConnection.setAccessToken(encrypt(data.getAccessToken()));
            socialConnection.setSecret(encrypt(data.getSecret()));
            socialConnection.setRefreshToken(encrypt(data.getRefreshToken()));
            socialConnection.setExpireTime(data.getExpireTime());
            socialConnectionRepository.save(socialConnection);
        }
    }

    public void removeConnections(String providerId) {
        socialConnectionRepository.remove(userId, providerId);
    }

    public void removeConnection(ConnectionKey connectionKey) {
        socialConnectionRepository.remove(userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
    }

    // internal helpers

    private Connection<?> buildConnection(SocialConnection socialConnection) {
        ConnectionData connectionData = new ConnectionData(socialConnection.getProviderId(),
                socialConnection.getProviderUserId(), socialConnection.getDisplayName(),
                socialConnection.getProfileUrl(), socialConnection.getImageUrl(),
                decrypt(socialConnection.getAccessToken()), decrypt(socialConnection.getSecret()),
                decrypt(socialConnection.getRefreshToken()), socialConnection.getExpireTime());
        ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionData
                .getProviderId());
        return connectionFactory.createConnection(connectionData);
    }

    private Connection<?> findPrimaryConnection(String providerId) {
        List<SocialConnection> userSocialConnectionList = socialConnectionRepository
                .findConnections(userId, providerId);

        return buildConnection(userSocialConnectionList.get(0));
    }

    private <A> String getProviderId(Class<A> apiType) {
        return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
    }

    private String encrypt(String text) {
        return text != null ? textEncryptor.encrypt(text) : text;
    }

    private String decrypt(String encryptedText) {
        return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
    }


    private SocialConnection createSocialConnection(ConnectionData data) {
        if (data == null) {
            return null;
        }

        SocialConnection socialConnection = new SocialConnection();
        socialConnection.setUserId(userId);
        socialConnection.setProviderId(data.getProviderId());
        socialConnection.setProviderUserId(data.getProviderUserId());
        socialConnection.setRank(0);

        socialConnection.setDisplayName(data.getDisplayName());
        socialConnection.setProfileUrl(data.getProfileUrl());
        socialConnection.setImageUrl(data.getImageUrl());
        socialConnection.setAccessToken(encrypt(data.getAccessToken()));
        socialConnection.setSecret(encrypt(data.getSecret()));
        socialConnection.setRefreshToken(encrypt(data.getRefreshToken()));
        socialConnection.setExpireTime(data.getExpireTime());

        return socialConnection;
    }
}