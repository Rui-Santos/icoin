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

package com.icoin.trading.api.users.event;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.users.domain.Identifier;
import com.icoin.trading.api.users.domain.UserId;

import java.util.Date;
import java.util.List;

/**
 * Event to indicate a new user has been created.
 *
 * @author Jettro Coenradie
 */
public class UserCreatedEvent extends EventSupport<UserCreatedEvent> {
    private UserId userId;
    private String username;
    private String firstName;
    private String lastName;
    private Identifier identifier;
    private String email;
    private String password;
    private List<String> roles;
    private Date time;

    public UserCreatedEvent(UserId userId,
                            String username,
                            String firstName,
                            String lastName,
                            Identifier identifier,
                            String email,
                            String password,
                            List<String> roles,
                            Date time) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.identifier = identifier;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public UserId getUserIdentifier() {
        return this.userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public Date getTime() {
        return time;
    }
}
