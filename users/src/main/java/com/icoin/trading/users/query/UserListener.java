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

import com.icoin.trading.users.domain.event.UserCreatedEvent;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jettro Coenradie
 */
@Component
public class UserListener {

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

        userRepository.save(userEntry);
    }

    @Autowired
    public void setUserRepository(UserQueryRepository userRepository) {
        this.userRepository = userRepository;
    }
}
