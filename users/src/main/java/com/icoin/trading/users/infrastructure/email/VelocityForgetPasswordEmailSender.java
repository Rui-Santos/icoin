package com.icoin.trading.users.infrastructure.email;

import com.google.common.collect.ImmutableMap;
import com.homhon.util.Strings;
import com.icoin.trading.infrastructure.mail.VelocityEmailSender;
import com.icoin.trading.users.domain.ForgetPasswordEmailSender;
import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.users.domain.model.function.UserPasswordResetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jihual
 * Date: 1/9/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class VelocityForgetPasswordEmailSender implements ForgetPasswordEmailSender {
    private static Logger logger = LoggerFactory.getLogger(VelocityForgetPasswordEmailSender.class);
    private VelocityEmailSender sender;
    private String from = "admin@icoin.com";
    private UserPasswordResetRepository userPasswordResetRepository;
    private String templateLocation;
    private String subject = "User Password Reset";

    @Value("${forgetPasswordTemplate}")
    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Resource(name = "users.velocityEmailSender")
    public void setSender(VelocityEmailSender sender) {
        this.sender = sender;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserPasswordResetRepository(UserPasswordResetRepository userPasswordResetRepository) {
        this.userPasswordResetRepository = userPasswordResetRepository;
    }

    @Value("${email.username}")
    public void setFrom(String from) {
        this.from = from;
    }

    //todo change the active link
    public void sendEmail(final String token) {
        if (!Strings.hasLength(token)) {
            logger.info("token is null or empty");
            return;
        }

        final UserPasswordReset userPasswordReset = userPasswordResetRepository.findByToken(token);
        if (userPasswordReset == null) {
            logger.info("cannot find token {} anymore", userPasswordReset);
            return;
        }

        final Map<String, Object> model = ImmutableMap.of("user", (Object) userPasswordReset, "token", token);

        sender.sendEmail(
                subject,
                userPasswordReset.getEmail(),
                from,
                templateLocation,
                "utf-8",
                model,
                true);
    }
}