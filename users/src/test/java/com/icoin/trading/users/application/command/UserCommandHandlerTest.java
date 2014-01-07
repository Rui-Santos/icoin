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

import com.icoin.trading.users.domain.model.user.Identifier;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import com.icoin.trading.users.application.command.UserCommandHandler;
import com.icoin.trading.users.application.command.AuthenticateUserCommand;
import com.icoin.trading.users.application.command.CreateUserCommand;
import com.icoin.trading.users.domain.event.UserAuthenticatedEvent;
import com.icoin.trading.users.domain.event.UserCreatedEvent;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.domain.model.user.User;
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
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");

        fixture.given()
                .when(new CreateUserCommand(aggregateIdentifier,
                        "Buyer 1",
                        "Mr",
                        "Buyer One",
                        identifier,
                        "buyer1@163.com",
                        "buyer1"))
                .expectEvents(new UserCreatedEvent(aggregateIdentifier,
                        "Buyer 1",
                        "Mr",
                        "Buyer One",
                        identifier,
                        "buyer1@163.com",
                        DigestUtils.sha1("buyer1")));
    }

    @Test
    public void testHandleAuthenticateUser() throws Exception {
        UserId aggregateIdentifier = new UserId();
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");

        UserEntry userEntry = new UserEntry();
        userEntry.setUsername("buyer1");
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        Mockito.when(userQueryRepository.findByUsername("buyer1")).thenReturn(userEntry);

        fixture.given(new UserCreatedEvent(aggregateIdentifier,
                "Buyer 1",
                "Mr",
                "Buyer One",
                identifier,
                "buyer1@163.com",
                DigestUtils.sha1("buyer1")))
                .when(new AuthenticateUserCommand("buyer1", "buyer1".toCharArray()))
                .expectEvents(new UserAuthenticatedEvent(aggregateIdentifier));
    }
}
