package com.icoin.trading.tradeengine.application.command.coin;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-25
 * Time: PM11:33
 * To change this template use File | Settings | File Templates.
 */

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.events.coin.CoinCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.coin.OrderBookAddedToCoinEvent;
import com.icoin.trading.tradeengine.domain.model.coin.Coin;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
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
    public void testCreateCoin() {
        CoinId aggregateIdentifier = new CoinId();
        CreateCoinCommand command = new CreateCoinCommand(aggregateIdentifier, "TestItem",
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1000)),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10000)));

        fixture.given()
                .when(command)
                .expectEvents(new CoinCreatedEvent(aggregateIdentifier, "TestItem",
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1000)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10000))));
    }

    @Test
    public void testAddOrderBook() {
        CoinId aggregateIdentifier = new CoinId();
        OrderBookId orderBookId = new OrderBookId();
        final CurrencyPair xpmCny = CurrencyPair.XPM_CNY;
        AddOrderBookToCoinCommand command = new AddOrderBookToCoinCommand(aggregateIdentifier,
                orderBookId, xpmCny);

        fixture.given(
                new CoinCreatedEvent(aggregateIdentifier,
                        "TestItem",
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1000)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10000))))
                .when(command)
                .expectEvents(
                        new OrderBookAddedToCoinEvent(
                                aggregateIdentifier,
                                orderBookId,
                                xpmCny));
    }
}
