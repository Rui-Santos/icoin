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

package com.icoin.trading.coin.domain;

import com.icoin.trading.coin.command.CoinCommandHandler;
import com.icoin.trading.coin.command.CreateCoinCommand;
import com.icoin.trading.coin.event.CoinCreatedEvent;
import com.icoin.trading.api.users.UserId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jettro Coenradie
 */
public class CompanyCommandHandlerTest {

    private FixtureConfiguration fixture;

    @Before
    public void setUp() {
        fixture = Fixtures.newGivenWhenThenFixture(Coin.class);
        CoinCommandHandler commandHandler = new CoinCommandHandler();
        commandHandler.setRepository(fixture.getRepository());
        fixture.registerAnnotatedCommandHandler(commandHandler);
    }

    @Test
    public void testCreateCompany() {
        CoinId aggregateIdentifier = new CoinId();
        UserId userId = new UserId();
        CreateCoinCommand command = new CreateCoinCommand(aggregateIdentifier, userId, "TestItem", 1000, 10000);

        fixture.given()
                .when(command)
                .expectEvents(new CoinCreatedEvent(aggregateIdentifier, "TestItem", 1000, 10000));
    }
}
