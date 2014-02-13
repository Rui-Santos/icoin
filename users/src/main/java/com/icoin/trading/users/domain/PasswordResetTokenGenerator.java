package com.icoin.trading.users.domain;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-12
 * Time: PM10:08
 * To change this template use File | Settings | File Templates.
 */
public interface PasswordResetTokenGenerator {
    String generate(String userName, String ip, Date date);
}
