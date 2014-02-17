package com.icoin.trading.users.infrastructure.listener;

import com.google.common.collect.ImmutableMap;
import com.homhon.core.operation.RetryExecutor;
import com.homhon.core.operation.RetryingCallback;
import com.homhon.util.Strings;
import com.icoin.trading.infrastructure.mail.VelocityEmailSender;
import com.icoin.trading.users.domain.event.PasswordChangedEvent;
import com.icoin.trading.users.domain.event.UserAuthenticatedEvent;
import com.icoin.trading.users.domain.event.WithdrawPasswordChangedEvent;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.apache.velocity.app.VelocityEngine;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
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
    public void handle(final UserAuthenticatedEvent event) {
        if (!Strings.hasLength(event.getUsername())) {
            logger.warn("username {} is null", event.getUsername());
            return;
        }

        final UserEntry user = userRepository.findByUsername(event.getUsername());

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
        if (!Strings.hasLength(event.getUsername())) {
            logger.warn("username {} is null", event.getUsername());
            return;
        }
        final UserEntry user = userRepository.findByUsername(event.getUsername());

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
    public void handle(final WithdrawPasswordChangedEvent event) {
        if (!Strings.hasLength(event.getUsername())) {
            logger.warn("username {} is null", event.getUsername());
            return;
        }
        final UserEntry user = userRepository.findByUsername(event.getUsername());

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
