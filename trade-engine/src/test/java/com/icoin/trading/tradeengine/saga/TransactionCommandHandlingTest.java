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

import com.icoin.trading.tradeengine.application.command.transaction.TransactionCommandHandler;
import com.icoin.trading.tradeengine.application.command.transaction.command.CancelTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ConfirmTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.ExecutedTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartBuyTransactionCommand;
import com.icoin.trading.tradeengine.application.command.transaction.command.StartSellTransactionCommand;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionPartiallyExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.SellTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.Transaction;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Jettro Coenradie
 */
public class TransactionCommandHandlingTest {

    private FixtureConfiguration fixture;
    OrderBookId orderBook = new OrderBookId();
    PortfolioId portfolio = new PortfolioId();
    TransactionId transactionId = new TransactionId();

    @Before
    public void setUp() {
        fixture = Fixtures.newGivenWhenThenFixture(Transaction.class);
        TransactionCommandHandler commandHandler = new TransactionCommandHandler();
        commandHandler.setRepository(fixture.getRepository());
        fixture.registerAnnotatedCommandHandler(commandHandler);
    }

    @Test
    public void testStartBuyTransaction() {
        StartBuyTransactionCommand command = new StartBuyTransactionCommand(transactionId, orderBook, portfolio, BigDecimal.valueOf(200), BigDecimal.valueOf(20));
        fixture.given()
                .when(command)
                .expectEvents(new BuyTransactionStartedEvent(transactionId, orderBook, portfolio, BigDecimal.valueOf(200), BigDecimal.valueOf(20)));
    }

    @Test
    public void testStartSellTransaction() {
        StartSellTransactionCommand command = new StartSellTransactionCommand(transactionId, orderBook, portfolio, BigDecimal.valueOf(200), BigDecimal.valueOf(20));
        fixture.given()
                .when(command)
                .expectEvents(new SellTransactionStartedEvent(transactionId, orderBook, portfolio, BigDecimal.valueOf(200), BigDecimal.valueOf(20)));
    }

    @Test
    public void testConfirmTransaction() {
        final Date confirmDate = new Date();
        ConfirmTransactionCommand command = new ConfirmTransactionCommand(transactionId, confirmDate);
        fixture.given(new BuyTransactionStartedEvent(transactionId, orderBook, portfolio, BigDecimal.valueOf(200), BigDecimal.valueOf(20)))
                .when(command)
                .expectEvents(new BuyTransactionConfirmedEvent(transactionId, confirmDate));
    }

    @Test
    public void testCancelTransaction() {
        CancelTransactionCommand command = new CancelTransactionCommand(transactionId);
        fixture.given(new BuyTransactionStartedEvent(transactionId, orderBook, portfolio, BigDecimal.valueOf(200), BigDecimal.valueOf(20)))
                .when(command)
                .expectEvents(new BuyTransactionCancelledEvent(transactionId, BigDecimal.valueOf(200), BigDecimal.valueOf(0)));
    }

    @Test
    public void testCancelTransaction_partiallyExecuted() {
        CancelTransactionCommand command = new CancelTransactionCommand(transactionId);
        fixture.given(new BuyTransactionStartedEvent(transactionId, orderBook, portfolio, BigDecimal.valueOf(200), BigDecimal.valueOf(20)),
                new BuyTransactionPartiallyExecutedEvent(transactionId, BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.valueOf(20)))
                .when(command)
                .expectEvents(new BuyTransactionCancelledEvent(transactionId, BigDecimal.valueOf(200), BigDecimal.valueOf(100)));
    }

    @Test
    public void testExecuteTransaction() {
        ExecutedTransactionCommand command = new ExecutedTransactionCommand(transactionId, BigDecimal.valueOf(200), BigDecimal.valueOf(20));
        fixture.given(new BuyTransactionStartedEvent(transactionId, orderBook, portfolio, BigDecimal.valueOf(200), BigDecimal.valueOf(20)),
                new BuyTransactionConfirmedEvent(transactionId, new Date()))
                .when(command)
                .expectEvents(new BuyTransactionExecutedEvent(transactionId, BigDecimal.valueOf(200), BigDecimal.valueOf(20)));
    }

    @Test
    public void testExecuteTransaction_partiallyExecuted() {
        ExecutedTransactionCommand command = new ExecutedTransactionCommand(transactionId, BigDecimal.valueOf(50), BigDecimal.valueOf(20));
        fixture.given(new BuyTransactionStartedEvent(transactionId, orderBook, portfolio, BigDecimal.valueOf(200), BigDecimal.valueOf(20)),
                new BuyTransactionConfirmedEvent(transactionId,new Date()))
                .when(command)
                .expectEvents(new BuyTransactionPartiallyExecutedEvent(transactionId, BigDecimal.valueOf(50), BigDecimal.valueOf(50), BigDecimal.valueOf(20)));
    }

    @Test
    public void testExecuteTransaction_completeAfterPartiallyExecuted() {
        ExecutedTransactionCommand command = new ExecutedTransactionCommand(transactionId, BigDecimal.valueOf(150), BigDecimal.valueOf(20));
        fixture.given(new BuyTransactionStartedEvent(transactionId, orderBook, portfolio, BigDecimal.valueOf(200), BigDecimal.valueOf(20)),
                new BuyTransactionConfirmedEvent(transactionId,new Date()),
                new BuyTransactionPartiallyExecutedEvent(transactionId, BigDecimal.valueOf(50), BigDecimal.valueOf(50), BigDecimal.valueOf(20))
        )
                .when(command)
                .expectEvents(new BuyTransactionExecutedEvent(transactionId, BigDecimal.valueOf(150), BigDecimal.valueOf(20)));
        // TODO moeten we nu ook nog een partially executed event gooien?
    }
}
