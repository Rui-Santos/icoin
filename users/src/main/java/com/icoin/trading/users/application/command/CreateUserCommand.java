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

package com.icoin.trading.users.application.command;

import com.homhon.base.command.CommandSupport;
import com.homhon.util.Strings;
import com.icoin.trading.api.users.domain.Identifier;
import com.icoin.trading.api.users.domain.UserId;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.hasText;
import static com.homhon.util.Asserts.notNull;

/**
 * Command to create a new user.
 *
 * @author Jettro Coenradie
 */
public class CreateUserCommand extends CommandSupport<CreateUserCommand> {
    @NotNull(message = "The provided userId cannot be null")
    private UserId userId;

    @NotNull
    @Size(min = 3, message = "The provided username cannot be null")
    private String username;

    @Size(min = 6, message = "The provided password cannot be null")
    private String password;

    @Size(min = 6, message = "The provided confirmedPassword cannot be null")
    private String confirmedPassword;

    @Email
    private String email;

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    private Identifier identifier;

    private List<String> roles;

    @NotNull
    private Date time;

    public CreateUserCommand(UserId userId,
                             String username,
                             String firstName,
                             String lastName,
                             Identifier identifier,
                             String email,
                             String password,
                             String confirmedPassword,
                             List<String> roles,
                             Date time) {
        notNull(userId, "The provided userId cannot be null");
        hasLength(username, "The provided username cannot be null");
        hasText(password, "The provided password cannot be null");
        hasText(confirmedPassword, "The provided confirmedPassword cannot be null");
//        Asserts.notNull(identifier, "The provided name cannot be null");
//        Asserts.notNull(password, "The provided password cannot be null");
//        Asserts.notNull(firstName, "The provided first name cannot be null");
//        Asserts.notNull(lastName, "The provided last name cannot be null");

        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.identifier = identifier;
        this.email = email;
        this.password = password;
        this.confirmedPassword = confirmedPassword;
        this.roles = roles;
        this.time = time;
    }

    public boolean isValid() {
        return Strings.hasText(password) && password.equals(confirmedPassword)
                && identifier != null && identifier.isValid();
    }

    public UserId getUserId() {
        return userId;
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

    public String getEmail() {
        return email;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public List<String> getRoles() {
        return roles;
    }

    public Date getTime() {
        return time;
    }
}
