package com.icoin.trading.tradeengine.application.command.portfolio;

import com.icoin.trading.tradeengine.application.command.portfolio.cash.CancelCashReservationCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.ConfirmCashReservationCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.DepositCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.ReserveCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.WithdrawCashCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.AddAmountToPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.CancelAmountReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ConfirmAmountReservationForPortfolioCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.coin.ReserveAmountCommand;
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
import com.icoin.trading.users.domain.UserId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

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
        AddAmountToPortfolioCommand command = new AddAmountToPortfolioCommand(portfolioIdentifier,
                orderBookIdentifier,
                BigDecimal.valueOf(100));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier))
                .when(command)
                .expectEvents(new ItemsAddedToPortfolioEvent(portfolioIdentifier, orderBookIdentifier, BigDecimal.valueOf(100)));
    }

    @Test
    public void testReserveItems_noItemsAvailable() {
        ReserveAmountCommand command = new ReserveAmountCommand(portfolioIdentifier,
                orderBookIdentifier,
                transactionIdentifier,
                BigDecimal.valueOf(200));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier))
                .when(command)
                .expectEvents(new ItemToReserveNotAvailableInPortfolioEvent(portfolioIdentifier, orderBookIdentifier, transactionIdentifier));
    }

    @Test
    public void testReserveItems_notEnoughItemsAvailable() {
        ReserveAmountCommand command = new ReserveAmountCommand(portfolioIdentifier,
                orderBookIdentifier,
                transactionIdentifier,
                BigDecimal.valueOf(200));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new ItemsAddedToPortfolioEvent(portfolioIdentifier, orderBookIdentifier, BigDecimal.valueOf(100)))
                .when(command)
                .expectEvents(new NotEnoughItemsAvailableToReserveInPortfolio(portfolioIdentifier,
                        orderBookIdentifier,
                        transactionIdentifier,
                        BigDecimal.valueOf(100),
                        BigDecimal.valueOf(200)));
    }

    @Test
    public void testReserveItems() {
        ReserveAmountCommand command = new ReserveAmountCommand(portfolioIdentifier,
                orderBookIdentifier,
                transactionIdentifier,
                BigDecimal.valueOf(200));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new ItemsAddedToPortfolioEvent(portfolioIdentifier, orderBookIdentifier, BigDecimal.valueOf(400)))
                .when(command)
                .expectEvents(new ItemsReservedEvent(portfolioIdentifier, orderBookIdentifier, transactionIdentifier, BigDecimal.valueOf(200)));
    }

    @Test
    public void testConfirmationOfReservation() {
        ConfirmAmountReservationForPortfolioCommand command =
                new ConfirmAmountReservationForPortfolioCommand(portfolioIdentifier,
                        orderBookIdentifier,
                        transactionIdentifier,
                        BigDecimal.valueOf(100));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new ItemsAddedToPortfolioEvent(portfolioIdentifier, orderBookIdentifier, BigDecimal.valueOf(400)),
                new ItemsReservedEvent(portfolioIdentifier, orderBookIdentifier, transactionIdentifier, BigDecimal.valueOf(100)))
                .when(command)
                .expectEvents(new ItemReservationConfirmedForPortfolioEvent(portfolioIdentifier,
                        orderBookIdentifier,
                        transactionIdentifier,
                        BigDecimal.valueOf(100)));
    }

    @Test
    public void testCancellationOfReservation() {
        CancelAmountReservationForPortfolioCommand command =
                new CancelAmountReservationForPortfolioCommand(portfolioIdentifier,
                        orderBookIdentifier,
                        transactionIdentifier,
                        BigDecimal.valueOf(100));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new ItemsAddedToPortfolioEvent(portfolioIdentifier, orderBookIdentifier, BigDecimal.valueOf(400)),
                new ItemsReservedEvent(portfolioIdentifier, orderBookIdentifier, transactionIdentifier, BigDecimal.valueOf(100)))
                .when(command)
                .expectEvents(new ItemReservationCancelledForPortfolioEvent(portfolioIdentifier,
                        orderBookIdentifier,
                        transactionIdentifier,
                        BigDecimal.valueOf(100)));
    }

    /* Money related test methods */
    @Test
    public void testDepositingMoneyToThePortfolio() {
        DepositCashCommand command = new DepositCashCommand(portfolioIdentifier, BigDecimal.valueOf(2000l));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier))
                .when(command)
                .expectEvents(new CashDepositedEvent(portfolioIdentifier, BigDecimal.valueOf(2000l)));
    }

    @Test
    public void testWithdrawingMoneyFromPortfolio() {
        WithdrawCashCommand command = new WithdrawCashCommand(portfolioIdentifier, BigDecimal.valueOf(300l));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier), new CashDepositedEvent(portfolioIdentifier, BigDecimal.valueOf(400)))
                .when(command)
                .expectEvents(new CashWithdrawnEvent(portfolioIdentifier, BigDecimal.valueOf(300l)));
    }

    @Test
    public void testWithdrawingMoneyFromPortfolio_withoutEnoughMoney() {
        WithdrawCashCommand command = new WithdrawCashCommand(portfolioIdentifier, BigDecimal.valueOf(300l));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier), new CashDepositedEvent(portfolioIdentifier, BigDecimal.valueOf(200)))
                .when(command)
                .expectEvents(new CashWithdrawnEvent(portfolioIdentifier, BigDecimal.valueOf(300l)));
    }

    @Test
    public void testMakingMoneyReservation() {
        ReserveCashCommand command = new ReserveCashCommand(portfolioIdentifier,
                transactionIdentifier,
                BigDecimal.valueOf(300l));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier), new CashDepositedEvent(portfolioIdentifier, BigDecimal.valueOf(400)))
                .when(command)
                .expectEvents(new CashReservedEvent(portfolioIdentifier, transactionIdentifier, BigDecimal.valueOf(300l)));
    }

    @Test
    public void testMakingMoneyReservation_withoutEnoughMoney() {
        ReserveCashCommand command = new ReserveCashCommand(portfolioIdentifier,
                transactionIdentifier,
                BigDecimal.valueOf(600l));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier), new CashDepositedEvent(portfolioIdentifier, BigDecimal.valueOf(400)))
                .when(command)
                .expectEvents(new CashReservationRejectedEvent(portfolioIdentifier, transactionIdentifier, BigDecimal.valueOf(600)));
    }

    @Test
    public void testCancelMoneyReservation() {
        CancelCashReservationCommand command = new CancelCashReservationCommand(
                portfolioIdentifier,
                transactionIdentifier,
                BigDecimal.valueOf(200l));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier), new CashDepositedEvent(portfolioIdentifier, BigDecimal.valueOf(400)))
                .when(command)
                .expectEvents(new CashReservationCancelledEvent(portfolioIdentifier, transactionIdentifier, BigDecimal.valueOf(200l)));
    }

    @Test
    public void testConfirmMoneyReservation() {
        ConfirmCashReservationCommand command = new ConfirmCashReservationCommand(
                portfolioIdentifier,
                transactionIdentifier,
                BigDecimal.valueOf(200l));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier), new CashDepositedEvent(portfolioIdentifier, BigDecimal.valueOf(400)))
                .when(command)
                .expectEvents(new CashReservationConfirmedEvent(portfolioIdentifier, transactionIdentifier, BigDecimal.valueOf(200l)));
    }
}