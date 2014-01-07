package com.icoin.trading.tradeengine.application.listener;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.application.command.coin.AddOrderBookToCoinCommand;
import com.icoin.trading.tradeengine.application.command.order.CreateOrderBookCommand;
import com.icoin.trading.tradeengine.domain.events.coin.CoinCreatedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.BigMoney;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-3
 * Time: PM9:40
 * To change this template use File | Settings | File Templates.
 */
public class CoinOrderBookListenerTest {
    @Test
    public void testHandleCoinCreated() throws Exception {
        final CoinId coinId = new CoinId("BTC");
        final String coinName = "BTC Coin Name";
        final BigMoney price = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10);
        final BigMoney amount = BigMoney.of(Constants.CURRENCY_UNIT_BTC, 100);

        final CoinOrderBookListener listener = new CoinOrderBookListener();
        final CommandGateway gateway = mock(CommandGateway.class);

        listener.setCommandGateway(gateway);

        listener.handleCoinCreated(new CoinCreatedEvent(
                coinId,
                coinName,
                price,
                amount));

        ArgumentCaptor<CommandSupport> captor = ArgumentCaptor.forClass(CommandSupport.class);

        verify(gateway, times(2)).send(captor.capture());


        final List<CommandSupport> commands = captor.getAllValues();

        assertThat(commands, not(empty()));
        assertThat(commands, hasSize(2));
        CommandSupport commandSupport = commands.get(0);
        assertThat(commandSupport, instanceOf(CreateOrderBookCommand.class));
        CreateOrderBookCommand createOrderBookCommand = (CreateOrderBookCommand) commands.get(0);
        final OrderBookId orderBookIdentifier = createOrderBookCommand.getOrderBookIdentifier();
        assertThat(createOrderBookCommand.getCurrencyPair(), equalTo(new CurrencyPair(coinId.toString())));
        assertThat(createOrderBookCommand.getOrderBookIdentifier(), notNullValue());


        commandSupport = commands.get(1);
        assertThat(commandSupport, instanceOf(AddOrderBookToCoinCommand.class));
        AddOrderBookToCoinCommand addOrderBookToCoinCommand = (AddOrderBookToCoinCommand) commands.get(1);
        assertThat(addOrderBookToCoinCommand.getCurrencyPair(), equalTo(new CurrencyPair(coinId.toString())));
        assertThat(addOrderBookToCoinCommand.getOrderBookId(), equalTo(orderBookIdentifier));
        assertThat(addOrderBookToCoinCommand.getCoinId(), equalTo(coinId));
    }
}
