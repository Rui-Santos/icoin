package com.icoin.trading.tradeengine.application.command.coin;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-25
 * Time: PM11:33
 * To change this template use File | Settings | File Templates.
 */

import com.icoin.trading.tradeengine.domain.events.coin.CoinCreatedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.Coin;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.users.domain.UserId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author Jettro Coenradie
 */
public class CoinCommandHandlerTest {

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
        CreateCoinCommand command = new CreateCoinCommand(aggregateIdentifier, userId, "TestItem",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(10000));

        fixture.given()
                .when(command)
                .expectEvents(new CoinCreatedEvent(aggregateIdentifier, "TestItem", BigDecimal.valueOf(1000), BigDecimal.valueOf(10000)));
    }
}
