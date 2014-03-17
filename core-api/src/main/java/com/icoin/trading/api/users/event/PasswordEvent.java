package com.icoin.trading.api.users.event;

import com.homhon.base.domain.event.EventSupport;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-19
 * Time: AM1:09
 * To change this template use File | Settings | File Templates.
 */
public class PasswordEvent<T extends PasswordEvent> extends EventSupport<T> {
    private String encodedPassword;
    private String encodedConfirmedPassword;

    public PasswordEvent(String encodedPassword, String encodedConfirmedPassword) {
        this.encodedPassword = encodedPassword;
        this.encodedConfirmedPassword = encodedConfirmedPassword;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public String getEncodedConfirmedPassword() {
        return encodedConfirmedPassword;
    }
}
