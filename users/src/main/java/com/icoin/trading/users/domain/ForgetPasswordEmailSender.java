package com.icoin.trading.users.domain;

import com.icoin.trading.users.domain.model.user.UserAccount;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-12
 * Time: PM9:56
 * To change this template use File | Settings | File Templates.
 */
public interface ForgetPasswordEmailSender {
    void sendEmail(final UserAccount userAccount, final String token, final String email);
}
