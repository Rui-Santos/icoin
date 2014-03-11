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

package com.icoin.trading.tradeengine.saga;

import com.icoin.trading.tradeengine.application.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.CancelAmountReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ConfirmAmountReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ReserveAmountCommand;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemToReserveNotAvailableInPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.NotEnoughItemAvailableToReserveInPortfolio;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionPartiallyExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.TradeType;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.saga.matchers.ConfirmTransactionCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.CreateSellOrderCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.ExecutedTransactionCommandMatcher;
import org.axonframework.test.saga.AnnotatedSagaTestFixture;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.axonframework.test.matchers.Matchers.andNoMore;
import static org.axonframework.test.matchers.Matchers.exactSequenceOf;

/**
 * @author Jettro Coenradie
 */
public class SellTradeManagerSagaIT {

    private TransactionId transactionIdentifier = new TransactionId();
    private CoinId coinId = new CoinId();
    private OrderBookId orderBookIdentifier = new OrderBookId();
    private PortfolioId portfolioIdentifier = new PortfolioId();

    private AnnotatedSagaTestFixture fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new AnnotatedSagaTestFixture(SellTradeManagerSaga.class);
    }

    @Test
    public void testHandle_SellTransactionStarted() throws Exception {
        Date time = currentTime();
        fixture.givenAggregate(transactionIdentifier)
                .published()
                .whenAggregate(transactionIdentifier)
                .publishes(
                        new SellTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(1000)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new ReserveAmountCommand(portfolioIdentifier,
                                coinId,
                                transactionIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time)
                )
                /*.expectDispatchedCommandsMatching(exactSequenceOf(
                        new ReservedItemCommandMatcher(orderBookIdentifier,
                                portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)))))*/;
    }

    @Test
    public void testHandle_ItemsReserved() {
        Date time = currentTime();
        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new SellTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(1000)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time))
                .whenAggregate(portfolioIdentifier)
                .publishes(
                        new ItemReservedEvent(
                                portfolioIdentifier,
                                coinId,
                                transactionIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                time))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(exactSequenceOf(new ConfirmTransactionCommandMatcher(
                        transactionIdentifier)));
    }

    @Test
    public void testHandle_TransactionConfirmed() {
        Date time = currentTime();
        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new SellTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(1000)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time))
                .andThenAggregate(portfolioIdentifier)
                .published(
                        new ItemReservedEvent(
                                portfolioIdentifier,
                                coinId,
                                transactionIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                time))
                .whenAggregate(transactionIdentifier).publishes(
                new SellTransactionConfirmedEvent(transactionIdentifier, new Date()))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(exactSequenceOf(
                        new CreateSellOrderCommandMatcher(
                                portfolioIdentifier,
                                orderBookIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10)))));
    }


    @Test
    public void testHandle_NotEnoughItemsToReserve() {
        Date time = currentTime();
        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new SellTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(1000)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time))
                .whenAggregate(portfolioIdentifier)
                .publishes(
                        new NotEnoughItemAvailableToReserveInPortfolio(
                                portfolioIdentifier,
                                coinId,
                                transactionIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                time))
                .expectActiveSagas(0);
    }

    @Test
    public void testHandle_ItemToReserveNotAvailableInPortfolio() {
        Date time = currentTime();
        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new SellTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(1000)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time))
                .whenAggregate(portfolioIdentifier)
                .publishes(
                        new ItemToReserveNotAvailableInPortfolioEvent(
                                portfolioIdentifier,
                                coinId,
                                transactionIdentifier,
                                time))
                .expectActiveSagas(0);
    }

    @Test
    public void testHandle_TransactionCancelled() {
        Date time = currentTime();
        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new SellTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(1000)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time))
                .whenAggregate(transactionIdentifier)
                .publishes(
                        new SellTransactionCancelledEvent(transactionIdentifier, coinId,
                                time))
                .expectActiveSagas(0)
                .expectDispatchedCommandsEqualTo(new CancelAmountReservationForPortfolioCommand(
                        portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                        time));
    }

    @Test
    public void testHandle_TransactionPartiallyCancelled() {
        Date time = currentTime();
        OrderId buyOrderIdentifier = new OrderId();
        OrderId sellOrderIdentifier = new OrderId();
        TransactionId buyTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new SellTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(1000)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time))
                .andThenAggregate(portfolioIdentifier).published(
                new ItemReservedEvent(
                        portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        time))
                .andThenAggregate(transactionIdentifier).published(
                new SellTransactionConfirmedEvent(
                        transactionIdentifier,
                        tradeTime),
                new TradeExecutedEvent(orderBookIdentifier,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(101)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10200)),
                        buyOrderIdentifier.toString(),
                        sellOrderIdentifier.toString(),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(2)),
                        buyTransactionIdentifier,
                        transactionIdentifier,
                        tradeTime,
                        TradeType.BUY))
                .whenAggregate(transactionIdentifier)
                .publishes(
                        new SellTransactionCancelledEvent(transactionIdentifier, coinId,
                                time))
                .expectActiveSagas(0)
                .expectDispatchedCommandsEqualTo(new CancelAmountReservationForPortfolioCommand(
                        portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                        time));
    }

    @Test
    public void testHandle_TradeExecutedPlaced() {
        Date time = currentTime();
        OrderId buyOrderIdentifier = new OrderId();
        OrderId sellOrderIdentifier = new OrderId();
        TransactionId buyTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new SellTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(1000)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time))
                .andThenAggregate(portfolioIdentifier).published(
                new ItemReservedEvent(
                        portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        time))
                .andThenAggregate(transactionIdentifier).published(
                new SellTransactionConfirmedEvent(
                        transactionIdentifier,
                        new Date()))
                .whenAggregate(orderBookIdentifier)
                .publishes(new TradeExecutedEvent(orderBookIdentifier,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(102)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10200)),
                        buyOrderIdentifier.toString(),
                        sellOrderIdentifier.toString(),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(2)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1)),
                        buyTransactionIdentifier,
                        transactionIdentifier,
                        tradeTime,
                        TradeType.BUY))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(exactSequenceOf(
                        new ExecutedTransactionCommandMatcher(
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(102)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10200)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1)),
                                transactionIdentifier, coinId),
                        andNoMore()));
    }

    @Test
    public void testHandle_SellTransactionExecuted() {
        Date time = currentTime();
        OrderId buyOrderIdentifier = new OrderId();
        OrderId sellOrderIdentifier = new OrderId();
        TransactionId buyTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new SellTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(1000)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time))
                .andThenAggregate(portfolioIdentifier).published(
                new ItemReservedEvent(
                        portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        time))
                .andThenAggregate(transactionIdentifier)
                .published(
                        new SellTransactionConfirmedEvent(
                                transactionIdentifier, new Date()))
                .andThenAggregate(orderBookIdentifier)
                .published(
                        new TradeExecutedEvent(
                                orderBookIdentifier,
                                coinId,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(102)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10200)),
                                buyOrderIdentifier.toString(),
                                sellOrderIdentifier.toString(),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(2)),
                                buyTransactionIdentifier,
                                transactionIdentifier,
                                tradeTime,
                                TradeType.BUY))
                .whenAggregate(transactionIdentifier)
                .publishes(
                        new SellTransactionExecutedEvent(
                                transactionIdentifier,
                                coinId,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(102)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10200)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1)),
                                time))
                .expectActiveSagas(0)
                .expectDispatchedCommandsEqualTo(
                        new ConfirmAmountReservationForPortfolioCommand(
                                portfolioIdentifier,
                                coinId,
                                transactionIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1)),
                                time),
                        new DepositCashCommand(portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(100 * 102)),
                                time));
    }

    @Test
    public void testHandle_SellTransactionExecutedWithCommissionAdjust() {
        Date time = currentTime();
        OrderId buyOrderIdentifier = new OrderId();
        OrderId sellOrderIdentifier = new OrderId();
        TransactionId buyTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new SellTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(1000)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time))
                .andThenAggregate(portfolioIdentifier).published(
                new ItemReservedEvent(
                        portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(110)),
                        time))
                .andThenAggregate(transactionIdentifier)
                .published(
                        new SellTransactionConfirmedEvent(
                                transactionIdentifier, new Date()))
                .andThenAggregate(orderBookIdentifier)
                .published(
                        new TradeExecutedEvent(
                                orderBookIdentifier,
                                coinId,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(102)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10200)),
                                buyOrderIdentifier.toString(),
                                sellOrderIdentifier.toString(),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(12)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(2)),
                                buyTransactionIdentifier,
                                transactionIdentifier,
                                tradeTime,
                                TradeType.BUY))
                .whenAggregate(transactionIdentifier)
                .publishes(
                        new SellTransactionExecutedEvent(
                                transactionIdentifier,
                                coinId,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(102)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10200)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(12)),
                                time))
                .expectActiveSagas(0)
                .expectDispatchedCommandsEqualTo(
                        new ConfirmAmountReservationForPortfolioCommand(
                                portfolioIdentifier,
                                coinId,
                                transactionIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time),
                        new DepositCashCommand(portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(100 * 102)),
                                time));
    }

    @Test
    public void testHandle_SellTransactionPartiallyExecuted() {
        Date time = currentTime();
        OrderId buyOrderIdentifier = new OrderId();
        OrderId sellOrderIdentifier = new OrderId();
        TransactionId buyTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new SellTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(10)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(1000)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)),
                                time))
                .andThenAggregate(portfolioIdentifier)
                .published(
                        new ItemReservedEvent(
                                portfolioIdentifier,
                                coinId,
                                transactionIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                time))
                .andThenAggregate(transactionIdentifier).published(
                new SellTransactionConfirmedEvent(transactionIdentifier, new Date()))
                .andThenAggregate(orderBookIdentifier).published(
                new TradeExecutedEvent(
                        orderBookIdentifier,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(102)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10200)),
                        buyOrderIdentifier.toString(),//todo change
                        sellOrderIdentifier.toString(),//todo change
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1)),
                        BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(2)),
                        buyTransactionIdentifier,
                        transactionIdentifier,
                        tradeTime,
                        TradeType.BUY))
                .whenAggregate(transactionIdentifier)
                .publishes(
                        new SellTransactionPartiallyExecutedEvent(
                                transactionIdentifier,
                                coinId,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(75)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(102)),
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(50 * 102)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(2)) ,
                                time
                        ))
                .expectActiveSagas(1)
                .expectDispatchedCommandsEqualTo(
                        new ConfirmAmountReservationForPortfolioCommand(
                                portfolioIdentifier,
                                coinId,
                                transactionIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(2)),
                                time),
                        new DepositCashCommand(portfolioIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.CNY), BigDecimal.valueOf(50 * 102)),
                                time));
    }
}
