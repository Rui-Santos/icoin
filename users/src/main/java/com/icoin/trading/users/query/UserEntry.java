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

import com.homhon.mongo.domainsupport.modelsupport.entity.AuditAwareEntitySupport;
import com.icoin.trading.users.domain.UserAccount;


/**
 * @author Jettro Coenradie
 */
public class UserEntry extends AuditAwareEntitySupport<UserEntry, String, Long> implements UserAccount {

    private String name;
    private String username;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPrimaryKey() {
        return this.primaryKey;
    }

    @Override
    public String getUserName() {
        return this.username;
    }

    @Override
    public String getFullName() {
        return this.name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserEntry{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
