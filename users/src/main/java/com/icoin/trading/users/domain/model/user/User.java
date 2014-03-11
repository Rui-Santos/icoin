/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icoin.trading.users.domain.model.user;

import com.icoin.trading.users.domain.event.NotificationSettingsUpdatedEvent;
import com.icoin.trading.users.domain.event.PasswordChangedEvent;
import com.icoin.trading.users.domain.event.UserAdminInfoChangedEvent;
import com.icoin.trading.users.domain.event.UserAuthenticatedEvent;
import com.icoin.trading.users.domain.event.UserCreatedEvent;
import com.icoin.trading.users.domain.event.UserInfoChangedEvent;
import com.icoin.trading.users.domain.event.WithdrawPasswordChangedEvent;
import com.icoin.trading.users.domain.event.WithdrawPasswordCreatedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;

/**
 * @author Jettro Coenradie
 */
public class User extends AbstractAnnotatedAggregateRoot {
    private static final long serialVersionUID = 3291411359839192350L;
    @AggregateIdentifier
    private UserId userId;
    private String username;
    private String password;
    private String withdrawPassword;
    private String email;

    protected User() {
    }

    public User(UserId userId, String username, String firstName, String lastName, Identifier identifier, String email, String password, List<String> roles, Date time) {
        apply(new UserCreatedEvent(userId, username, firstName, lastName, identifier, email, password, roles, time));
    }

    public boolean authenticate(PasswordEncoder passwordEncoder, String rawPassword, String operatingIp, Date authTime) {
        boolean success = passwordEncoder.matches(rawPassword, password);
        if (success) {
            apply(new UserAuthenticatedEvent(userId, username, email, operatingIp, authTime));
        }
        return success;
    }

    public void changePassword(String encodedPassword, String encodedConfirmedPassword, String operatingIp, Date changedTime) {
        apply(new PasswordChangedEvent(userId, username, email, encodedPassword, encodedConfirmedPassword, operatingIp, changedTime));
    }

    public void changeWithdrawPassword(String encodedPassword, String encodedConfirmedPassword, String operatingIp, Date changedTime) {
        apply(new WithdrawPasswordChangedEvent(userId, username, email, encodedPassword, encodedConfirmedPassword, operatingIp, changedTime));
    }

    public void createWithdrawPassword(String encodedPassword, String encodedConfirmedPassword, String operatingIp, Date changedTime) {
        apply(new WithdrawPasswordCreatedEvent(userId, username, email, encodedPassword, encodedConfirmedPassword, operatingIp, changedTime));
    }

    public void editInfo(String email, String mobile, String firstName, String lastName) {
        apply(new UserInfoChangedEvent(userId, username, email, mobile, firstName, lastName));
    }

    public void editAdminInfo(String email, Identifier identifier, String mobile, String firstName, String lastName, List<String> roles) {
        apply(new UserAdminInfoChangedEvent(userId, username, email, identifier, mobile, firstName, lastName ,roles));
    }

    public void updateNotificationSettings(boolean logonAlert,
                                           boolean withdrawMoneyAlert,
                                           boolean withdrawItemAlert,
                                           boolean executedAlert) {
        apply(new NotificationSettingsUpdatedEvent(userId, username, logonAlert, withdrawMoneyAlert, withdrawItemAlert, executedAlert));
    }


    @EventHandler
    public void onUserInfoChanged(UserInfoChangedEvent event) {
        this.email = event.getEmail();
    }

    @EventHandler
    public void onUserCreated(UserCreatedEvent event) {
        this.userId = event.getUserIdentifier();
        this.username = event.getUsername();
        this.password = event.getPassword();
        this.email = event.getEmail();
    }

    @EventHandler
    public void onPasswordChanged(PasswordChangedEvent event) {
        this.password = event.getEncodedPassword();
    }

    @EventHandler
    public void onWithdrawPasswordChangedEvent(WithdrawPasswordChangedEvent event) {
        this.withdrawPassword = event.getEncodedPassword();
    }

    @EventHandler
    public void onWithdrawPasswordCreatedEvent(WithdrawPasswordCreatedEvent event) {
        this.withdrawPassword = event.getEncodedPassword();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getWithdrawPassword() {
        return withdrawPassword;
    }

    @Override
    public UserId getIdentifier() {
        return userId;
    }
}
