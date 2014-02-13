package com.icoin.trading.users.domain.event;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.users.domain.model.user.UserId;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-12
 * Time: PM10:01
 * To change this template use File | Settings | File Templates.
 */
public class WithdrawPasswordChangedEvent extends EventSupport<WithdrawPasswordChangedEvent> {
    private UserId userId;
    private String encodedPassword;
    private String encodedConfirmedPassword;
    private String operatingIp;

    public WithdrawPasswordChangedEvent(UserId userId, String encodedPassword, String encodedConfirmedPassword, String operatingIp) {
        this.userId = userId;
        this.encodedPassword = encodedPassword;
        this.encodedConfirmedPassword = encodedConfirmedPassword;
        this.operatingIp = operatingIp;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public String getEncodedConfirmedPassword() {
        return encodedConfirmedPassword;
    }

    public String getOperatingIp() {
        return operatingIp;
    }
}
