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

import com.icoin.trading.tradeengine.application.command.portfolio.CreatePortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.PortfolioCommandHandler;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.CancelCashReservationCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.ConfirmCashReservationCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.ReserveCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.WithdrawCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.AddItemsToPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.CancelItemReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ConfirmItemReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ReserveItemsCommand;
import com.icoin.trading.tradeengine.domain.events.portfolio.PortfolioCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashDepositedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationRejectedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashWithdrawnEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationCancelledForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationConfirmedForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemToReserveNotAvailableInPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemsAddedToPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemsReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.NotEnoughItemsAvailableToReserveInPortfolio;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.Portfolio;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.domain.model.user.UserId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jettro Coenradie
 */
public class PortfolioCommandHandlerTest {

    private FixtureConfiguration<Portfolio> fixture;
    private PortfolioId portfolioIdentifier;
    private OrderBookId orderBookIdentifier;
    private TransactionId transactionIdentifier;
    private UserId userIdentifier;

    @Before
    public void setUp() {
        fixture = Fixtures.newGivenWhenThenFixture(Portfolio.class);
        PortfolioCommandHandler commandHandler = new PortfolioCommandHandler();
        commandHandler.setRepository(fixture.getRepository());
        fixture.registerAnnotatedCommandHandler(commandHandler);
        portfolioIdentifier = new PortfolioId();
        orderBookIdentifier = new OrderBookId();
        transactionIdentifier = new TransactionId();
        userIdentifier = new UserId();
    }

    @Test
    public void testCreatePortfolio() {

        CreatePortfolioCommand command = new CreatePortfolioCommand(portfolioIdentifier, userIdentifier);
        fixture.given()
                .when(command)
                .expectEvents(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier));
    }

    /* Items related test methods */
    @Test
    public void testAddItemsToPortfolio() {
        AddItemsToPortfolioCommand command = new AddItemsToPortfolioCommand(portfolioIdentifier,
                orderBookIdentifier,
                100);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier))
                .when(command)
                .expectEvents(new ItemsAddedToPortfolioEvent(portfolioIdentifier, orderBookIdentifier, 100));
    }

    @Test
    public void testReserveItems_noItemsAvailable() {
        ReserveItemsCommand command = new ReserveItemsCommand(portfolioIdentifier,
                orderBookIdentifier,
                transactionIdentifier,
                200);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier))
                .when(command)
                .expectEvents(new ItemToReserveNotAvailableInPortfolioEvent(portfolioIdentifier, orderBookIdentifier, transactionIdentifier));
    }

    @Test
    public void testReserveItems_notEnoughItemsAvailable() {
        ReserveItemsCommand command = new ReserveItemsCommand(portfolioIdentifier,
                orderBookIdentifier,
                transactionIdentifier,
                200);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new ItemsAddedToPortfolioEvent(portfolioIdentifier, orderBookIdentifier, 100))
                .when(command)
                .expectEvents(new NotEnoughItemsAvailableToReserveInPortfolio(portfolioIdentifier,
                        orderBookIdentifier,
                        transactionIdentifier,
                        100,
                        200));
    }

    @Test
    public void testReserveItems() {
        ReserveItemsCommand command = new ReserveItemsCommand(portfolioIdentifier,
                orderBookIdentifier,
                transactionIdentifier,
                200);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new ItemsAddedToPortfolioEvent(portfolioIdentifier, orderBookIdentifier, 400))
                .when(command)
                .expectEvents(new ItemsReservedEvent(portfolioIdentifier, orderBookIdentifier, transactionIdentifier, 200));
    }

    @Test
    public void testConfirmationOfReservation() {
        ConfirmItemReservationForPortfolioCommand command =
                new ConfirmItemReservationForPortfolioCommand(portfolioIdentifier,
                        orderBookIdentifier,
                        transactionIdentifier,
                        100);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new ItemsAddedToPortfolioEvent(portfolioIdentifier, orderBookIdentifier, 400),
                new ItemsReservedEvent(portfolioIdentifier, orderBookIdentifier, transactionIdentifier, 100))
                .when(command)
                .expectEvents(new ItemReservationConfirmedForPortfolioEvent(portfolioIdentifier,
                        orderBookIdentifier,
                        transactionIdentifier,
                        100));
    }

    @Test
    public void testCancellationOfReservation() {
        CancelItemReservationForPortfolioCommand command =
                new CancelItemReservationForPortfolioCommand(portfolioIdentifier,
                        orderBookIdentifier,
                        transactionIdentifier,
                        100);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new ItemsAddedToPortfolioEvent(portfolioIdentifier, orderBookIdentifier, 400),
                new ItemsReservedEvent(portfolioIdentifier, orderBookIdentifier, transactionIdentifier, 100))
                .when(command)
                .expectEvents(new ItemReservationCancelledForPortfolioEvent(portfolioIdentifier,
                        orderBookIdentifier,
                        transactionIdentifier,
                        100));
    }

    /* Money related test methods */
    @Test
    public void testDepositingMoneyToThePortfolio() {
        DepositCashCommand command = new DepositCashCommand(portfolioIdentifier, 2000l);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier))
                .when(command)
                .expectEvents(new CashDepositedEvent(portfolioIdentifier, 2000l));
    }

    @Test
    public void testWithdrawingMoneyFromPortfolio() {
        WithdrawCashCommand command = new WithdrawCashCommand(portfolioIdentifier, 300l);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier), new CashDepositedEvent(portfolioIdentifier, 400))
                .when(command)
                .expectEvents(new CashWithdrawnEvent(portfolioIdentifier, 300l));
    }

    @Test
    public void testWithdrawingMoneyFromPortfolio_withoutEnoughMoney() {
        WithdrawCashCommand command = new WithdrawCashCommand(portfolioIdentifier, 300l);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier), new CashDepositedEvent(portfolioIdentifier, 200))
                .when(command)
                .expectEvents(new CashWithdrawnEvent(portfolioIdentifier, 300l));
    }

    @Test
    public void testMakingMoneyReservation() {
        ReserveCashCommand command = new ReserveCashCommand(portfolioIdentifier,
                transactionIdentifier,
                300l);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier), new CashDepositedEvent(portfolioIdentifier, 400))
                .when(command)
                .expectEvents(new CashReservedEvent(portfolioIdentifier, transactionIdentifier, 300l));
    }

    @Test
    public void testMakingMoneyReservation_withoutEnoughMoney() {
        ReserveCashCommand command = new ReserveCashCommand(portfolioIdentifier,
                transactionIdentifier,
                600l);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier), new CashDepositedEvent(portfolioIdentifier, 400))
                .when(command)
                .expectEvents(new CashReservationRejectedEvent(portfolioIdentifier, transactionIdentifier, 600));
    }

    @Test
    public void testCancelMoneyReservation() {
        CancelCashReservationCommand command = new CancelCashReservationCommand(
                portfolioIdentifier,
                transactionIdentifier,
                200l);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier), new CashDepositedEvent(portfolioIdentifier, 400))
                .when(command)
                .expectEvents(new CashReservationCancelledEvent(portfolioIdentifier, transactionIdentifier, 200l));
    }

    @Test
    public void testConfirmMoneyReservation() {
        ConfirmCashReservationCommand command = new ConfirmCashReservationCommand(
                portfolioIdentifier,
                transactionIdentifier,
                200l);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier), new CashDepositedEvent(portfolioIdentifier, 400))
                .when(command)
                .expectEvents(new CashReservationConfirmedEvent(portfolioIdentifier, transactionIdentifier, 200l));
    }
}
