package com.icoin.trading.users.infrastructure.email;

import com.homhon.core.operation.RetryExecutor;
import com.homhon.core.operation.RetryingCallback;
import com.homhon.util.Strings;
import com.icoin.trading.users.domain.ForgetPasswordEmailSender;
import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.users.domain.model.function.UserPasswordResetRepository;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
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
    private JavaMailSender mailSender;
    private VelocityEngine velocityEngine;
    private String from = "admin@icoin.com";
    private UserPasswordResetRepository userPasswordResetRepository;
    private String templateLocation;
    private String  subject = "User Password Reset";

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserPasswordResetRepository(UserPasswordResetRepository userPasswordResetRepository) {
        this.userPasswordResetRepository = userPasswordResetRepository;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    //http://javopedia.com/spring/sending-email-using-velocity-spring-and-java/

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

        final MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(userPasswordReset.getEmail());
                message.setFrom(from); // could be parameterized...
                message.setSubject(subject);
                Map model = new HashMap();
                model.put("user", userPasswordReset);
                model.put("token", token);
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        velocityEngine, templateLocation, "utf-8", model);
                message.setText(text, true);
            }
        };
        new RetryExecutor<Void>() {
            @Override
            protected Void perform() {
                logger.info("forget password email being sent to {}", userPasswordReset.getEmail() );
                mailSender.send(preparator);
                return null;
            }
        }.execute(3, 500, new RetryingCallback<Void>() {

            @Override
            public Void onFailure(Throwable cause) {
                logger.error("cannot send msg to", userPasswordReset.getEmail(), cause);
                return null;
            }
        });
    }
}