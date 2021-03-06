package com.icoin.trading.tradeengine.application.command.transaction;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.coin.domain.CurrencyPair;
import com.icoin.trading.api.tradeengine.command.transaction.CancelTransactionCommand;
import com.icoin.trading.api.tradeengine.command.transaction.ConfirmTransactionCommand;
import com.icoin.trading.api.tradeengine.command.transaction.ExecutedTransactionCommand;
import com.icoin.trading.api.tradeengine.command.transaction.StartBuyTransactionCommand;
import com.icoin.trading.api.tradeengine.command.transaction.StartSellTransactionCommand;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionCancelledEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionConfirmedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionPartiallyExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionStartedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.SellTransactionStartedEvent;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.commission.Commission;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicy;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicyFactory;
import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.transaction.Transaction;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-4
 * Time: PM12:51
 * To change this template use File | Settings | File Templates.
 */
public class TransactionCommandHandlerTest {
    private FixtureConfiguration fixture;
    OrderBookId orderBook = new OrderBookId();
    CoinId coinId = new CoinId();
    PortfolioId portfolio = new PortfolioId();
    TransactionId transactionId = new TransactionId();
    CommissionPolicyFactory policyFactory;
    CommissionPolicy policy;

    @Before
    public void setUp() {
        fixture = Fixtures.newGivenWhenThenFixture(Transaction.class);
        TransactionCommandHandler commandHandler = new TransactionCommandHandler();
        commandHandler.setRepository(fixture.getRepository());

        policyFactory = mock(CommissionPolicyFactory.class);
        policy = mock(CommissionPolicy.class);
        when(policyFactory.createCommissionPolicy(any(Order.class))).thenReturn(policy);

        commandHandler.setCommissionPolicyFactory(policyFactory);
        fixture.registerAnnotatedCommandHandler(commandHandler);
    }

    @Test
    public void testStartBuyTransaction() {
        Date time = currentTime();
        when(policy.calculateBuyCommission(any(Order.class)))
                .thenReturn(new Commission(BigMoney.of(CurrencyUnit.of(Currencies.EUR), BigDecimal.valueOf(10)),
                        "test buy"));

        StartBuyTransactionCommand command =
                new StartBuyTransactionCommand(
                        transactionId,
                        coinId,
                        CurrencyPair.BTC_EUR,
                        orderBook,
                        portfolio,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                        BigMoney.of(CurrencyUnit.EUR, BigDecimal.valueOf(20)),
                        time);
        fixture.given()
                .when(command)
                .expectEvents(
                        new BuyTransactionStartedEvent(
                                transactionId,
                                coinId,
                                orderBook,
                                portfolio,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                                BigMoney.of(CurrencyUnit.EUR, BigDecimal.valueOf(20)),
                                BigMoney.of(CurrencyUnit.EUR, BigDecimal.valueOf(4000)).toMoney().toBigMoney(),
                                BigMoney.of(CurrencyUnit.EUR, BigDecimal.valueOf(10)).toMoney().toBigMoney(),
                                time));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(policy).calculateBuyCommission(captor.capture());
        final Order order = captor.getValue();

        assertThat(order, notNullValue());
        assertThat(order.getTradeAmount(), equalTo(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200))));
        assertThat(order.getItemPrice(), equalTo(BigMoney.of(CurrencyUnit.EUR, BigDecimal.valueOf(20))));
        assertThat(order.getItemRemaining(), equalTo(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200))));
        assertThat(order.getPortfolioId(), equalTo(portfolio));
        assertThat(order.getOrderBookId(), equalTo(orderBook));
        assertThat(order.getCurrencyPair(), equalTo(CurrencyPair.BTC_EUR));
//        assertThat(order.getCoinId(), equalTo(coinId));
    }

    @Test
    public void testStartSellTransaction() {
        Date time = currentTime();
        when(policy.calculateSellCommission(any(Order.class)))
                .thenReturn(new Commission(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                        "test sell"));

        StartSellTransactionCommand command =
                new StartSellTransactionCommand(
                        transactionId,
                        coinId,
                        CurrencyPair.BTC_CAD,
                        orderBook,
                        portfolio,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                        BigMoney.of(CurrencyUnit.CAD, BigDecimal.valueOf(20)),
                        time);
        fixture.given()
                .when(command)
                .expectEvents(
                        new SellTransactionStartedEvent(
                                transactionId,
                                coinId,
                                orderBook,
                                portfolio,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                                BigMoney.of(CurrencyUnit.CAD, BigDecimal.valueOf(20)),
                                BigMoney.of(CurrencyUnit.CAD, BigDecimal.valueOf(4000)).toMoney().toBigMoney(),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)).toMoney().toBigMoney(),
                                time));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(policy).calculateSellCommission(captor.capture());
        final Order order = captor.getValue();

        assertThat(order, notNullValue());
        assertThat(order.getTradeAmount(), equalTo(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200))));
        assertThat(order.getItemPrice(), equalTo(BigMoney.of(CurrencyUnit.CAD, BigDecimal.valueOf(20))));
        assertThat(order.getItemRemaining(), equalTo(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200))));
        assertThat(order.getPortfolioId(), equalTo(portfolio));
        assertThat(order.getOrderBookId(), equalTo(orderBook));
        assertThat(order.getCurrencyPair(), equalTo(CurrencyPair.BTC_CAD));
//        assertThat(order.getCoinId(), equalTo(coinId));
    }

    @Test
    public void testConfirmTransaction() {
        final Date confirmDate = new Date();
        ConfirmTransactionCommand command = new ConfirmTransactionCommand(transactionId, confirmDate);
        fixture.given(new BuyTransactionStartedEvent(
                transactionId,
                coinId,
                orderBook,
                portfolio,
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(20)),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(4000)),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10)),
                confirmDate))
                .when(command)
                .expectEvents(new BuyTransactionConfirmedEvent(transactionId, confirmDate));
    }

    @Test
    public void testCancelTransaction() {
        Date time = currentTime();
        CancelTransactionCommand command = new CancelTransactionCommand(transactionId,
                time);
        fixture.given(new BuyTransactionStartedEvent(
                transactionId,
                coinId,
                orderBook,
                portfolio,
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(20)),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(4000)),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10)),
                time))
                .when(command)
                .expectEvents(new BuyTransactionCancelledEvent(
                        transactionId, coinId, time));
    }

    @Test
    public void testCancelTransaction_partiallyExecuted() {
        Date time = currentTime();
        CancelTransactionCommand command = new CancelTransactionCommand(transactionId,
                time);
        fixture.given(new BuyTransactionStartedEvent(
                transactionId,
                coinId,
                orderBook,
                portfolio,
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(4000)),
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                time),
                new BuyTransactionPartiallyExecutedEvent(transactionId,
                        coinId, BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(4000)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                        time)
        )
                .when(command)
                .expectEvents(new BuyTransactionCancelledEvent(transactionId, coinId,
                        time));
    }

    @Test
    public void testExecuteTransaction() {
        Date time = currentTime();
        ExecutedTransactionCommand command =
                new ExecutedTransactionCommand(transactionId,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(20)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(4000)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                        time);
        fixture.given(new BuyTransactionStartedEvent(transactionId,
                coinId, orderBook,
                portfolio,
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(20)),
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(4000)),
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                time),
                new BuyTransactionConfirmedEvent(transactionId,
                        new Date()))
                .when(command)
                .expectEvents(new BuyTransactionExecutedEvent(transactionId,
                        coinId, BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(20)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(4000)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                        time));
    }

    @Test
    public void testExecuteTransaction_partiallyExecuted() {
        Date time = currentTime();
        ExecutedTransactionCommand command =
                new ExecutedTransactionCommand(transactionId,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(20)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(4000)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                        time);
        fixture.given(new BuyTransactionStartedEvent(transactionId,
                coinId, orderBook,
                portfolio,
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(20)),
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(4000)),
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                time),
                new BuyTransactionConfirmedEvent(transactionId, new Date()))
                .when(command)
                .expectEvents(new BuyTransactionPartiallyExecutedEvent(transactionId,
                        coinId, BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(20)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(4000)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                        time));
    }

    @Test
    public void testExecuteTransaction_completeAfterPartiallyExecuted() {
        Date time = currentTime();
        ExecutedTransactionCommand command = new ExecutedTransactionCommand(transactionId,
                coinId,
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(150)),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(20)),
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(4000)),
                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                time);
        fixture.given(
                new BuyTransactionStartedEvent(transactionId,
                        coinId,
                        orderBook,
                        portfolio,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(20)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(4000)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                        time),
                new BuyTransactionConfirmedEvent(transactionId, new Date()),
                new BuyTransactionPartiallyExecutedEvent(transactionId,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(20)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(4000)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                        time))
                .when(command)
                .expectEvents(new BuyTransactionExecutedEvent(
                        transactionId,
                        coinId, BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(150)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(20)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(4000)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(20)),
                        time));
    }
}
