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

import com.icoin.trading.users.domain.model.user.InvalidIdentityException;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.domain.model.user.User;
import com.icoin.trading.users.domain.model.user.UsernameAlreadyInUseException;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import com.icoin.trading.users.domain.model.user.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.notNull;

/**
 * @author Jettro Coenradie
 */
@Component
public class UserCommandHandler {

    private Repository<User> repository;

    private UserQueryRepository userQueryRepository;

    @CommandHandler
    public UserId handleCreateUser(CreateUserCommand command) {
        hasLength(command.getUsername());
        notNull(command.getIdentifier());

        if(!command.getIdentifier().isValid()){
            throw new InvalidIdentityException(command.getIdentifier());
        }

        UserAccount account = userQueryRepository.findByUsername(command.getUsername());

        if (account != null) {
            throw new UsernameAlreadyInUseException(command.getUsername());
        }
        UserId identifier = command.getUserId();
        User user = new User(identifier,
                command.getUsername(),
                command.getFirstName(),
                command.getLastName(),
                command.getIdentifier(),
                command.getEmail(),
                command.getPassword());
        repository.add(user);
        return identifier;
    }

    @CommandHandler
    public UserAccount handleAuthenticateUser(AuthenticateUserCommand command) {
        UserAccount account = userQueryRepository.findByUsername(command.getUserName());

        if (account == null) {
            return null;
        }
        boolean success = onUser(account.getPrimaryKey()).authenticate(command.getPassword());
        return success ? account : null;
    }

    private User onUser(String userId) {
        return repository.load(new UserId(userId), null);
    }


    @Autowired
    @Qualifier("userRepository")
    public void setRepository(Repository<User> userRepository) {
        this.repository = userRepository;
    }

    @Autowired
    public void setUserRepository(UserQueryRepository userRepository) {
        this.userQueryRepository = userRepository;
    }
}
