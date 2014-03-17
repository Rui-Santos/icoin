/* 
* Copyright 2011 the original author or authors. 
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License. 
* You may obtain a copy of the License at 
* 
*      http://www.apache.org/licenses/LICENSE-2.0 
* 
* Unless required by applicable law or agreed to in writing, software 
* distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License. 
*/
package com.icoin.trading.api.users.event;

import java.util.List;


public class UserAdminInfoChangedEvent extends UserInfoChangedEvent<UserAdminInfoChangedEvent> {
    private Identifier identifier;
    private List<String> roles;


    public UserAdminInfoChangedEvent(UserId userId,
                                     String username,
                                     String email,
                                     Identifier identifier,
                                     String mobile,
                                     String firstName,
                                     String lastName,
                                     List<String> roles) {
        super(userId, username, email, mobile, firstName, lastName);
        this.identifier = identifier;
        this.roles = roles;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "UserAdminInfoChangedEvent{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", identifier='" + identifier + '\'' +
                ", mobile='" + mobile + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roles=" + roles +
                '}';
    }
} 