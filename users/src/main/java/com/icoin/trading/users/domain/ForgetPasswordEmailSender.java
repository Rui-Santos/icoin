package com.icoin.trading.users.domain;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-12
 * Time: PM9:56
 * To change this template use File | Settings | File Templates.
 */
public interface ForgetPasswordEmailSender {
    void sendEmail(final String token);
}
