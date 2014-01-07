package com.icoin.trading.webui.user;

import com.icoin.trading.users.application.command.CreateUserCommand;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-6
 * Time: PM9:47
 * To change this template use File | Settings | File Templates.
 */
public class AccountConnectionSignUp implements ConnectionSignUp {
    private static Logger logger = LoggerFactory.getLogger(AccountConnectionSignUp.class);
    private final UserQueryRepository userQueryRepository;
    private final CommandGateway gateway;

    @Inject
    public AccountConnectionSignUp(UserQueryRepository UserQueryRepository, CommandGateway gateway) {
        this.userQueryRepository = UserQueryRepository;
        this.gateway = gateway;
    }

    public String execute(Connection<?> connection) {
        UserProfile profile = connection.fetchUserProfile();

        final UserEntry created = userQueryRepository.findByUsername(profile.getUsername());

        if(created!=null){
           logger.warn("user {} is already there with id: {}.", profile.getUsername(), created.getPrimaryKey());
        }

        UserId userId = new UserId();
        CreateUserCommand createUser =
                new CreateUserCommand(userId,
                        profile.getUsername(),
                        profile.getFirstName(),
                        profile.getLastName(),
                        null,
                        profile.getEmail(),
                        null);

        gateway.sendAndWait(createUser, 60, TimeUnit.SECONDS);
        return profile.getUsername();
    }

}
