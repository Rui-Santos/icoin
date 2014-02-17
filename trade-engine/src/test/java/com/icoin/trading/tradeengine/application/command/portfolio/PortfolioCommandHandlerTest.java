package com.icoin.trading.tradeengine.application.command.portfolio;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.EqualsWithMoneyFieldMatcher;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.CancelCashReservationCommand;
import com.icoin.trading.tradeengine.application.command.portfolio.cash.ClearReservedCashCommand;
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
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservedClearedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashWithdrawnEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemAddedToPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationCancelledForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservationConfirmedForPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.ItemToReserveNotAvailableInPortfolioEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.coin.NotEnoughItemAvailableToReserveInPortfolio;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.Portfolio;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.users.domain.model.user.UserId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.axonframework.test.matchers.Matchers.sequenceOf;

/**
 * @author Jettro Coenradie
 */
public class PortfolioCommandHandlerTest {

    private FixtureConfiguration<Portfolio> fixture;
    private PortfolioId portfolioIdentifier;
    private OrderBookId orderBookIdentifier;
    private CoinId coinId;
    private TransactionId transactionIdentifier;
    private UserId userIdentifier;

    @Before
    public void setUp() {
        fixture = Fixtures.newGivenWhenThenFixture(Portfolio.class);
        PortfolioCommandHandler commandHandler = new PortfolioCommandHandler();
        commandHandler.setRepository(fixture.getRepository());
        fixture.registerAnnotatedCommandHandler(commandHandler);
        portfolioIdentifier = new PortfolioId();
        coinId = new CoinId();
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
        AddAmountToPortfolioCommand command =
                new AddAmountToPortfolioCommand(portfolioIdentifier,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier))
                .when(command)
                .expectEvents(
                        new ItemAddedToPortfolioEvent(
                                portfolioIdentifier,
                                coinId,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100))));
    }

    @Test
    public void testReserveItems_noItemsAvailable() {
        ReserveAmountCommand command = new ReserveAmountCommand(
                portfolioIdentifier,
                coinId,
                transactionIdentifier,
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(5)));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier))
                .when(command)
                .expectEvents(new ItemToReserveNotAvailableInPortfolioEvent(
                        portfolioIdentifier, coinId, transactionIdentifier));
    }

    @Test
    public void testReserveItems_notEnoughItemsAvailable() {
        ReserveAmountCommand command = new ReserveAmountCommand(
                portfolioIdentifier,
                coinId,
                transactionIdentifier,
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(5)));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new ItemAddedToPortfolioEvent(
                        portfolioIdentifier,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100))))
                .when(command)
                .expectEvents(new NotEnoughItemAvailableToReserveInPortfolio(
                        portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        Money.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)).toBigMoney(),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(205))));
    }

    @Test
    public void testReserveItems() {
        ReserveAmountCommand command = new ReserveAmountCommand(
                portfolioIdentifier,
                coinId,
                transactionIdentifier,
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(200)),
                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(5)));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new ItemAddedToPortfolioEvent(
                        portfolioIdentifier,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(400))))
                .when(command)
                .expectEvents(new ItemReservedEvent(
                        portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(205))));
    }

    @Test
    public void testConfirmationOfReservation() {
        ConfirmAmountReservationForPortfolioCommand command =
                new ConfirmAmountReservationForPortfolioCommand(
                        portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(5)));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new ItemAddedToPortfolioEvent(
                        portfolioIdentifier,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(400))),
                new ItemReservedEvent(portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(105))))
                .when(command)
                .expectEvents(new ItemReservationConfirmedForPortfolioEvent(
                        portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(5))));
    }

    @Test
    public void testCancellationOfReservation() {
        CancelAmountReservationForPortfolioCommand command =
                new CancelAmountReservationForPortfolioCommand(
                        portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(5)));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new ItemAddedToPortfolioEvent(
                        portfolioIdentifier,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(400))),
                new ItemReservedEvent(portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100))))
                .when(command)
                .expectEvents(new ItemReservationCancelledForPortfolioEvent(portfolioIdentifier,
                        coinId,
                        transactionIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(5))));
    }

    /* Money related test methods */
    @Test
    public void testDepositingMoneyToThePortfolio() {
        DepositCashCommand command =
                new DepositCashCommand(
                        portfolioIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 2000L));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier))
                .when(command)
                .expectEvents(new CashDepositedEvent(portfolioIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 2000L)));
    }

    @Test
    public void testWithdrawingMoneyFromPortfolio() {
        Date current = currentTime();
        WithdrawCashCommand command = new WithdrawCashCommand(
                portfolioIdentifier,
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(300)),
                current);
        fixture.given(new PortfolioCreatedEvent(
                portfolioIdentifier,
                userIdentifier),
                new CashDepositedEvent(
                        portfolioIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 400)))
                .when(command)
                .expectEventsMatching(sequenceOf(
                        new EqualsWithMoneyFieldMatcher<CashWithdrawnEvent>(
                                new CashWithdrawnEvent(portfolioIdentifier,
                                        BigMoney.ofScale(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(300), 1),
                                        current))
                ));
    }

    @Test
    public void testWithdrawingMoneyFromPortfolio_withoutEnoughMoney() {
        Date current = currentTime();
        WithdrawCashCommand command = new WithdrawCashCommand(portfolioIdentifier,
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 300L), current);
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new CashDepositedEvent(portfolioIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 200)))
                .when(command)
                .expectEvents(new CashWithdrawnEvent(portfolioIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 300L), current));
    }

    @Test
    public void testMakingMoneyReservation() {
        ReserveCashCommand command = new ReserveCashCommand(
                portfolioIdentifier,
                transactionIdentifier,
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 300L),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new CashDepositedEvent(portfolioIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 400L)))
                .when(command)
                .expectEvents(new CashReservedEvent(
                        portfolioIdentifier,
                        transactionIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 300L),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10)));
    }

    @Test
    public void testMakingMoneyReservation_withoutEnoughMoney() {
        ReserveCashCommand command = new ReserveCashCommand(portfolioIdentifier,
                transactionIdentifier,
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 600L),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 6));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new CashDepositedEvent(portfolioIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 400)))
                .when(command)
                .expectEvents(new CashReservationRejectedEvent(portfolioIdentifier,
                        transactionIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 600),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 6)));
    }

    @Test
    public void testCancelMoneyReservation() {
        CancelCashReservationCommand command = new CancelCashReservationCommand(
                portfolioIdentifier,
                transactionIdentifier,
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 200L),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 5));
        fixture.given(new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new CashDepositedEvent(portfolioIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 400L)))
                .when(command)
                .expectEvents(
                        new CashReservationCancelledEvent(
                                portfolioIdentifier,
                                transactionIdentifier,
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 200L),
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 5)));
    }

    @Test
    public void testConfirmMoneyReservation() {
        ConfirmCashReservationCommand command = new ConfirmCashReservationCommand(
                portfolioIdentifier,
                transactionIdentifier,
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 500L),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 200L));
        fixture.given(
                new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new CashDepositedEvent(portfolioIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 700L)))
                .when(command)
                .expectEvents(new CashReservationConfirmedEvent(
                        portfolioIdentifier,
                        transactionIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 500),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 200)));
    }

    @Test
    public void testClearReservedCash() {
        ClearReservedCashCommand command = new ClearReservedCashCommand(
                portfolioIdentifier,
                transactionIdentifier,
                orderBookIdentifier,
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 5L),
                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 2L));
        fixture.given(
                new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier),
                new CashDepositedEvent(portfolioIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 700L)),
                new CashReservationConfirmedEvent(
                        portfolioIdentifier,
                        transactionIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 495),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 198)))
                .when(command)
                .expectEvents(new CashReservedClearedEvent(
                        portfolioIdentifier,
                        transactionIdentifier,
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 7L)));
    }
}