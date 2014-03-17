package com.icoin.trading.api.users.event;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.users.event.UserId;


public class UserInfoChangedEvent<T extends UserInfoChangedEvent> extends EventSupport<T> {
    protected UserId userId;
    protected String username;
    protected String email;
    protected String mobile;
    protected String firstName;
    protected String lastName;

    public UserInfoChangedEvent(UserId userId,
                                String username,
                                String email,
                                String mobile,
                                String firstName,
                                String lastName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserId getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "UserInfoChangedEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}