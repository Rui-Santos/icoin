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

package com.icoin.trading.users.query;

import com.homhon.util.Strings;
import com.icoin.trading.users.domain.event.NotificationSettingsUpdatedEvent;
import com.icoin.trading.users.domain.event.PasswordChangedEvent;
import com.icoin.trading.users.domain.event.UserCreatedEvent;
import com.icoin.trading.users.domain.event.WithdrawPasswordChangedEvent;
import com.icoin.trading.users.domain.event.WithdrawPasswordCreatedEvent;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jettro Coenradie
 */
@Component
public class UserListener {
    private static Logger logger = LoggerFactory.getLogger(UserListener.class);
    private UserQueryRepository userRepository;

    @EventHandler
    public void handleUserCreated(UserCreatedEvent event) {
        UserEntry userEntry = new UserEntry();
        userEntry.setPrimaryKey(event.getUserIdentifier().toString());
        userEntry.setLastName(event.getLastName());
        userEntry.setFirstName(event.getFirstName());
        userEntry.setIdentifier(event.getIdentifier());
        userEntry.setUsername(event.getUsername());
        userEntry.setEmail(event.getEmail());
        userEntry.setPassword(event.getPassword());
        userEntry.setRoles(event.getRoles());

        userRepository.save(userEntry);
    }

    @EventHandler
    public void handleUserPasswordChanged(PasswordChangedEvent event) {
        UserEntry user = loadUser(event.getUserId(), event.getUsername());

        if (user == null) {
            return;
        }

        user.setPassword(event.getEncodedPassword());
        userRepository.save(user);
        logger.info("user {} password changed, id {}", event.getUsername(), event.getUserId());
    }

    @EventHandler
    public void handleUserWithdrawPasswordCreated(WithdrawPasswordCreatedEvent event) {
        UserEntry user = loadUser(event.getUserId(), event.getUsername());

        if (user == null) {
            return;
        }

        user.setWithdrawPassword(event.getEncodedPassword());
        userRepository.save(user);
        logger.info("user {} withdraw password created, id {}", event.getUsername(), event.getUserId());
    }

    @EventHandler
    public void handleUserWithdrawPasswordChanged(WithdrawPasswordChangedEvent event) {
        UserEntry user = loadUser(event.getUserId(), event.getUsername());

        if (user == null) {
            return;
        }

        user.setWithdrawPassword(event.getEncodedPassword());
        userRepository.save(user);
        logger.info("user {} withdraw password changed, id {}", event.getUsername(), event.getUserId());
    }

    @EventHandler
    public void handleUpdateNotification(NotificationSettingsUpdatedEvent event) {
        UserEntry user = loadUser(event.getUserId(), event.getUsername());

        if (user == null) {
            return;
        }

        user.setLogonAlert(event.isLogonAlert());
        user.setExecutedAlert(event.isExecutedAlert());
        user.setWithdrawMoneyAlert(event.isWithdrawMoneyAlert());
        user.setWithdrawItemAlert(event.isWithdrawItemAlert());
        userRepository.save(user);
        logger.info("user {} notification settings' been changed, id {}", event.getUsername(), event.getUserId());
    }

    private UserEntry loadUser(UserId userId, String username) {
        if (userId == null || !Strings.hasLength(userId.toString())) {
            logger.warn("user {} id is empty", username);
            return null;
        }

        final UserEntry user = userRepository.findOne(userId.toString());

        if (user == null) {
            logger.warn("user {} cannot be found via id {}", username, userId);
            return null;
        }

        return user;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserRepository(UserQueryRepository userRepository) {
        this.userRepository = userRepository;
    }
}
