package com.icoin.trading.infrastructure.mail;

import com.homhon.core.operation.RetryExecutor;
import com.homhon.core.operation.RetryingCallback;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeMessage;
import java.util.Map;

import static com.homhon.util.Strings.hasText;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-15
 * Time: PM8:43
 * To change this template use File | Settings | File Templates.
 */
public class SimpleSpringVelocityEmailSender implements VelocityEmailSender {
    private static Logger logger = LoggerFactory.getLogger(SimpleSpringVelocityEmailSender.class);
    private JavaMailSender mailSender;
    private VelocityEngine velocityEngine;

    @Override
    public void sendEmail(final String subject,
                          final String to,
                          final String from,
                          final String templateLocation,
                          final String encoding,
                          final Map<String, Object> model,
                          final boolean html) {
        if (!hasText(from) || !hasText(templateLocation)) {
            logger.info("from or template location is null: from is, template locatio is {}", from, templateLocation);
            return;
        }

        final MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(to);
                message.setFrom(from);
                message.setSubject(subject);
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        velocityEngine, templateLocation, encoding, model);
                message.setText(text, html);
            }
        };

        new RetryExecutor<Void>() {
            @Override
            protected Void perform() {
                logger.info("forget password email being sent to {}", to);
                mailSender.send(preparator);
                return null;
            }
        }.execute(3, 500, new RetryingCallback<Void>() {

            @Override
            public Void onFailure(Throwable cause) {
                logger.error("cannot send msg to", to, cause);
                return null;
            }
        });
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
}
