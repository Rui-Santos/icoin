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

package com.icoin.trading.app.command.user;

import com.icoin.trading.users.domain.UserId;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import com.icoin.trading.users.application.command.UserCommandHandler;
import com.icoin.trading.users.application.command.AuthenticateUserCommand;
import com.icoin.trading.users.application.command.CreateUserCommand;
import com.icoin.trading.users.domain.event.UserAuthenticatedEvent;
import com.icoin.trading.users.domain.event.UserCreatedEvent;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.domain.User;
import com.icoin.trading.users.util.DigestUtils;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Jettro Coenradie
 */
public class UserCommandHandlerTest {

    private FixtureConfiguration fixture;

    private UserQueryRepository userQueryRepository;

    @Before
    public void setUp() {
        userQueryRepository = Mockito.mock(UserQueryRepository.class);

        fixture = Fixtures.newGivenWhenThenFixture(User.class);
        UserCommandHandler commandHandler = new UserCommandHandler();
        commandHandler.setRepository(fixture.getRepository());
        commandHandler.setUserRepository(userQueryRepository);
        fixture.registerAnnotatedCommandHandler(commandHandler);
    }


    @Test
    public void testHandleCreateUser() throws Exception {
        UserId aggregateIdentifier = new UserId();
        fixture.given()
                .when(new CreateUserCommand(aggregateIdentifier, "Buyer 1", "buyer1", "buyer1"))
                .expectEvents(new UserCreatedEvent(aggregateIdentifier, "Buyer 1", "buyer1", DigestUtils.sha1("buyer1")));
    }

    @Test
    public void testHandleAuthenticateUser() throws Exception {
        UserId aggregateIdentifier = new UserId();

        UserEntry userEntry = new UserEntry();
        userEntry.setUsername("buyer1");
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setName("Buyer One");
        Mockito.when(userQueryRepository.findByUsername("buyer1")).thenReturn(userEntry);

        fixture.given(new UserCreatedEvent(aggregateIdentifier, "Buyer 1", "buyer1", DigestUtils.sha1("buyer1")))
                .when(new AuthenticateUserCommand("buyer1", "buyer1".toCharArray()))
                .expectEvents(new UserAuthenticatedEvent(aggregateIdentifier));
    }
}
