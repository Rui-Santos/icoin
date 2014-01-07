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
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInController;
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
public class SocialConfig {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private Environment environment;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private TextEncryptor textEncryptor;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private SocialConnectionRepositoryMongo socialConnectionRepositoryMongo;

//    @Inject
//    private DataSource dataSource;

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
    public ConnectionFactoryLocator connectionFactoryLocator() {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();

        registry.addConnectionFactory(new WeiboConnectionFactory(environment.getProperty("weibo.consumerKey"),
                environment.getProperty("weibo.consumerSecret")));
        return registry;
    }

    @Bean
    @Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
    public UsersConnectionRepository usersConnectionRepository() {
        return new SocialUsersConnectionRepository(socialConnectionRepositoryMongo, connectionFactoryLocator(), textEncryptor);
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public org.springframework.social.connect.ConnectionRepository connectionRepository() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
        }
        return usersConnectionRepository().createConnectionRepository(authentication.getName());
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public Weibo weibo() {
        Connection<Weibo> weibo = connectionRepository().findPrimaryConnection(Weibo.class);
        return weibo != null ? weibo.getApi() : new WeiboTemplate();
    }

    @Bean
    public ConnectController connectController() {
        ConnectController connectController = new ConnectController(connectionFactoryLocator(), connectionRepository());
        return connectController;
    }

    @Bean
    public ProviderSignInController providerSignInController(RequestCache requestCache) {
        final ProviderSignInController controller = new ProviderSignInController(
                connectionFactoryLocator(),
                usersConnectionRepository(),
                new SimpleSignInAdapter(requestCache));

        controller.setSignUpUrl("/signup");

        return controller;
    }

}