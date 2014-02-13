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

import com.icoin.trading.users.domain.event.PasswordChangedEvent;
import com.icoin.trading.users.domain.event.UserAuthenticatedEvent;
import com.icoin.trading.users.domain.event.UserCreatedEvent;
import com.icoin.trading.users.domain.event.WithdrawPasswordChangedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

/**
 * @author Jettro Coenradie
 */
public class User extends AbstractAnnotatedAggregateRoot {
    private static final long serialVersionUID = 3291411359839192350L;
    @AggregateIdentifier
    private UserId userId;
    private String password;
    private String withdrawPassword;
    private String email;

    protected User() {
    }

    public User(UserId userId, String username, String firstName, String lastName, Identifier identifier, String email, String password) {
        apply(new UserCreatedEvent(userId, username, firstName, lastName, identifier, email, password));
    }

    public boolean authenticate(String encodedPassword, String operatingIp) {
        boolean success = this.password.equals(encodedPassword);
        if (success) {
            apply(new UserAuthenticatedEvent(userId, operatingIp));
        }
        return success;
    }

    public void changePassword(String encodedPassword, String encodedConfirmedPassword, String operatingIp) {
        apply(new PasswordChangedEvent(userId, encodedPassword, encodedConfirmedPassword, operatingIp));
    }

    public void changeWithdrawPassword(String encodedPassword, String encodedConfirmedPassword, String operatingIp) {
        apply(new WithdrawPasswordChangedEvent(userId, encodedPassword, encodedConfirmedPassword, operatingIp));
    }

    @EventHandler
    public void onUserCreated(UserCreatedEvent event) {
        this.userId = event.getUserIdentifier();
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
