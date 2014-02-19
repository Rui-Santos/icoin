package com.icoin.trading.users.domain.event;

import com.icoin.trading.users.domain.model.user.UserId;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-12
 * Time: PM10:01
 * To change this template use File | Settings | File Templates.
 */
public class PasswordChangedEvent extends PasswordEvent<PasswordChangedEvent> {
    private UserId userId;
    private String username;
    private String email;
    private String operatingIp;
    private Date changedTime;

    public PasswordChangedEvent(UserId userId,
                                String username,
                                String email,
                                String encodedPassword,
                                String encodedConfirmedPassword,
                                String operatingIp,
                                Date changedTime) {
        super(encodedPassword, encodedConfirmedPassword);
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.operatingIp = operatingIp;
        this.changedTime = changedTime;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getOperatingIp() {
        return operatingIp;
    }

    public Date getChangedTime() {
        return changedTime;
    }
}
