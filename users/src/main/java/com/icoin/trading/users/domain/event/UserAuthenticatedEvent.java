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

package com.icoin.trading.users.domain.event;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.users.domain.UserId;

import java.util.Date;

/**
 * Event to indicate that the user with the specified userId has been authenticated.
 *
 * @author Jettro Coenradie
 */
public class UserAuthenticatedEvent extends EventSupport<UserAuthenticatedEvent> {
    private final UserId userId;
    private final String username;
    private final String email;
    private final String operatingIp;
    private Date authTime;

    public UserAuthenticatedEvent(UserId userId,
                                  String username,
                                  String email,
                                  String operatingIp,
                                  Date authTime) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.operatingIp = operatingIp;
        this.authTime = authTime;
    }

    public UserId getUserId() {
        return this.userId;
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

    public Date getAuthTime() {
        return authTime;
    }

    @Override
    public String toString() {
        return "UserAuthenticatedEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", operatingIp='" + operatingIp + '\'' +
                ", authTime=" + authTime +
                '}';
    }
}
