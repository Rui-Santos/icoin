package com.icoin.trading.users.infrastructure.email;

import com.homhon.core.operation.RetryExecutor;
import com.homhon.core.operation.RetryingCallback;
import com.icoin.trading.users.domain.ForgetPasswordEmailSender;
import com.icoin.trading.users.domain.model.user.UserAccount;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
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
@Component
public class VelocityForgetPasswordEmailSender implements ForgetPasswordEmailSender {
    private static Logger logger = LoggerFactory.getLogger(VelocityForgetPasswordEmailSender.class);
    private JavaMailSender mailSender;
    private VelocityEngine velocityEngine;
    private String  from = "admin@icoin.com";

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    //http://javopedia.com/spring/sending-email-using-velocity-spring-and-java/

    //todo change the active link
    public void sendEmail(final UserAccount userAccount, final String token, final String email) {
        final MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(email);
                message.setFrom(from); // could be parameterized...
                Map model = new HashMap();
                model.put("user", userAccount);
                model.put("token", token);
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        velocityEngine, "com/icoin/trading/users/infrastructure/email/forgot-password-email.vm", "utf-8", model);
                message.setText(text, true);
            }
        };
        new RetryExecutor<Void>() {
            @Override
            protected Void perform() {
                mailSender.send(preparator);
                return null;
            }
        }.execute(3, 500, new RetryingCallback<Void>() {

            @Override
            public Void onFailure(Throwable cause) {
                logger.error("cannot send msg to", email, cause);
                return null;
            }
        });
    }
}