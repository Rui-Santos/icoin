package com.icoin.trading.infrastructure.mail;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-15
 * Time: PM8:41
 * To change this template use File | Settings | File Templates.
 */
public interface VelocityEmailSender {
    void sendEmail(String subject,
                   String to,
                   String from,
                   String templateLocation,
                   String encoding,
                   Map<String, Object> model,
                   boolean html);
}
