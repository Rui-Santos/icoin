package com.icoin.trading.users.infrastructure.listener;

import com.google.common.collect.ImmutableMap;
import com.homhon.util.Strings;
import com.icoin.trading.infrastructure.mail.VelocityEmailSender;
import com.icoin.trading.users.domain.event.PasswordChangedEvent;
import com.icoin.trading.users.domain.event.UserAuthenticatedEvent;
import com.icoin.trading.users.domain.event.UserCreatedEvent;
import com.icoin.trading.users.domain.event.WithdrawPasswordChangedEvent;
import com.icoin.trading.users.domain.event.WithdrawPasswordCreatedEvent;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-12
 * Time: PM11:51
 * To change this template use File | Settings | File Templates.
 */
@Component
public class UserActivityListener {
    private static Logger logger = LoggerFactory.getLogger(UserActivityListener.class);
    private VelocityEmailSender sender;
    private String from = "admin@icoin.com";

    private UserQueryRepository userRepository;

    @EventHandler
    public void handle(final UserCreatedEvent event) {
        if (!Strings.hasLength(event.getEmail())) {
            logger.info("email is null or empty for user {}, id {}", event.getUsername(), event.getUserIdentifier());
            return;
        }

        final Map<String, Object> model =
                ImmutableMap.of("username", (Object) event.getUsername());

        sender.sendEmail(
                "Successful registered",
                event.getEmail(),
                from,
                "listener/user-created.vm",
                "utf-8",
                model,
                true);

    }

    @EventHandler
    public void handle(final UserAuthenticatedEvent event) {
        UserEntry user = loadUser(event.getUserId(), event.getUsername());

        if (user == null) {
            return;
        }

        if (!user.isLogonAlert()) {
            logger.info("user switched off auth alert: {}", event);
            return;
        }

        if (!Strings.hasLength(user.getEmail())) {
            logger.info("email is null or empty");
            return;
        }

        final Map<String, Object> model =
                ImmutableMap.of("username", (Object) user.getUsername(),
                        "ip", event.getOperatingIp(),
                        "time", event.getAuthTime());

        sender.sendEmail(
                "Successful authenticated",
                user.getEmail(),
                from,
                "listener/logged-on.vm",
                "utf-8",
                model,
                true);

    }

    @EventHandler
    public void handle(final PasswordChangedEvent event) {
        UserEntry user = loadUser(event.getUserId(), event.getUsername());

        if (user == null) {
            return;
        }

        if (!user.isChangePasswordAlert()) {
            logger.info("user switched off password change alert: {}", event);
            return;
        }

        if (!Strings.hasLength(user.getEmail())) {
            logger.info("email is null or empty");
            return;
        }

        final Map<String, Object> model =
                ImmutableMap.of("username", (Object) user.getUsername(),
                        "ip", event.getOperatingIp(),
                        "time", event.getChangedTime());

        sender.sendEmail(
                "Your password has been changed",
                user.getEmail(),
                from,
                "listener/password-changed.vm",
                "utf-8",
                model,
                true);
    }

    @EventHandler
    public void handle(final WithdrawPasswordCreatedEvent event) {
        UserEntry user = loadUser(event.getUserId(), event.getUsername());

        if (user == null) {
            return;
        }

//        if (!user.isWithdrawPasswordAlert()) {
//            logger.info("user switched off withdraw password change alert: {}", event);
//            return;
//        }

        if (!Strings.hasLength(user.getEmail())) {
            logger.info("email is null or empty");
            return;
        }

        final Map<String, Object> model =
                ImmutableMap.of("username", (Object) user.getUsername(),
                        "ip", event.getOperatingIp(),
                        "time", event.getChangedTime());

        sender.sendEmail(
                "Your withdraw password has been created",
                user.getEmail(),
                from,
                "listener/withdraw-password-created.vm",
                "utf-8",
                model,
                true);
    }

    @EventHandler
    public void handle(final WithdrawPasswordChangedEvent event) {
        UserEntry user = loadUser(event.getUserId(), event.getUsername());

        if (user == null) {
            return;
        }

        if (!user.isWithdrawPasswordAlert()) {
            logger.info("user switched off withdraw password change alert: {}", event);
            return;
        }

        if (!Strings.hasLength(user.getEmail())) {
            logger.info("email is null or empty");
            return;
        }

        final Map<String, Object> model =
                ImmutableMap.of("username", (Object) user.getUsername(),
                        "ip", event.getOperatingIp(),
                        "time", event.getChangedTime());

        sender.sendEmail(
                "Your withdraw password has been changed",
                user.getEmail(),
                from,
                "listener/withdraw-password-changed.vm",
                "utf-8",
                model,
                true);
    }

    private UserEntry loadUser(UserId userId, String username) {
        if (userId == null || !Strings.hasLength(userId.toString())) {
            logger.warn("user {} id is empty", username);
            return null;
        }

        final UserEntry user = userRepository.findOne(userId.toString());

        if (user == null) {
            logger.warn("user {} cannot be found via id {}", username, userId);
            return null;
        }

        return user;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Resource(name = "users.velocityEmailSender")
    public void setSender(VelocityEmailSender sender) {
        this.sender = sender;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserRepository(UserQueryRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${email.username}")
    public void setFrom(String from) {
        this.from = from;
    }
}
