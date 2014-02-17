package com.icoin.trading.webui.security;

import com.icoin.trading.users.infrastructure.persistence.mongo.SocialConnectionRepositoryMongo;
import com.icoin.trading.users.infrastructure.social.SocialUsersConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.social.weibo.api.Weibo;
import org.springframework.social.weibo.api.impl.WeiboTemplate;
import org.springframework.social.weibo.connect.WeiboConnectionFactory;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-5
 * Time: AM9:47
 * To change this template use File | Settings | File Templates.
 */
@PropertySource("classpath:com/icoin/trading/webui/security/application.properties")
@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private TextEncryptor textEncryptor;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private SocialConnectionRepositoryMongo socialConnectionRepositoryMongo;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
        final WeiboConnectionFactory connectionFactory = new WeiboConnectionFactory(env.getProperty("weibo.consumerKey"), env.getProperty("weibo.consumerSecret"));
        final OAuth2Template template = (OAuth2Template) connectionFactory.getOAuthOperations();
        template.setUseParametersForClientAuthentication(true);
        cfConfig.addConnectionFactory(connectionFactory);
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new UserIdSource() {
            @Override
            public String getUserId() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null) {
                    throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
                }
                return authentication.getName();
            }
        };
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return new SocialUsersConnectionRepository(socialConnectionRepositoryMongo, connectionFactoryLocator, textEncryptor);
    }

    @Bean
    public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
        ConnectController connectController = new ConnectController(connectionFactoryLocator, connectionRepository);
        return connectController;
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public Weibo weibo(ConnectionRepository connectionRepository) {
        Connection<Weibo> weibo = connectionRepository.findPrimaryConnection(Weibo.class);
        return weibo != null ? weibo.getApi() : new WeiboTemplate();
    }

    @Bean
    public ProviderSignInController providerSignInController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository, RequestCache requestCache) {
        //request cache new HttpSessionRequestCache()
        final ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, new SimpleSignInAdapter(requestCache));

        controller.setSignUpUrl("/signup");

        return controller;
    }
}