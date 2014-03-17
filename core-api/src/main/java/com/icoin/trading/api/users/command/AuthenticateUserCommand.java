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

package com.icoin.trading.api.users.command;

import com.homhon.base.command.CommandSupport;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class AuthenticateUserCommand extends CommandSupport<AuthenticateUserCommand> {
    @NotNull
    @Size(min = 3)
    private final String userName;
    @NotNull
    @Size(min = 6)
    private final String password;

    private final String operatingIp;

    private final Date authTime;


    public AuthenticateUserCommand(String userName, String password, String operatingIp, Date authTime) {
        this.userName = userName;
        this.password = password;
        this.operatingIp = operatingIp;
        this.authTime = authTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getOperatingIp() {
        return operatingIp;
    }

    public Date getAuthTime() {
        return authTime;
    }
}
