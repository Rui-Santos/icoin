package com.icoin.trading.tradeengine.saga;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-25
 * Time: AM8:23
 * To change this template use File | Settings | File Templates.
 */

import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationRejectedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservedEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionPartiallyExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.saga.matchers.AddItemsToPortfolioCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.CancelMoneyReservationFromPortfolioCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.ConfirmMoneyReservationFromPortfolionCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.ConfirmTransactionCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.CreateBuyOrderCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.ExecutedTransactionCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.ReserveMoneyFromPortfolioCommandMatcher;
import org.axonframework.test.saga.AnnotatedSagaTestFixture;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.axonframework.test.matchers.Matchers.andNoMore;
import static org.axonframework.test.matchers.Matchers.exactSequenceOf;

/**
 * @author Jettro Coenradie
 */
public class BuyTradeManagerSagaTest {

    private static final BigDecimal TOTAL_ITEMS = BigDecimal.valueOf(100);
    private static final BigDecimal PRICE_PER_ITEM = BigDecimal.TEN;

    private TransactionId transactionIdentifier = new TransactionId();
    private OrderBookId orderbookIdentifier = new OrderBookId();
    private PortfolioId portfolioIdentifier = new PortfolioId();

    private AnnotatedSagaTestFixture fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new AnnotatedSagaTestFixture(BuyTradeManagerSaga.class);
    }

    @Test
    public void testHandle_SellTransactionStarted() throws Exception {
        fixture.givenAggregate(transactionIdentifier).published()
                .whenAggregate(transactionIdentifier).publishes(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        orderbookIdentifier,
                        portfolioIdentifier,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(new ReserveMoneyFromPortfolioCommandMatcher(
                                portfolioIdentifier,
                                TOTAL_ITEMS.multiply(PRICE_PER_ITEM))));
    }

    @Test
    public void testHandle_MoneyIsReserved() {
        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        orderbookIdentifier,
                        portfolioIdentifier,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM))
                .whenAggregate(portfolioIdentifier).publishes(
                new CashReservedEvent(portfolioIdentifier, transactionIdentifier,
                        TOTAL_ITEMS
                                .multiply(PRICE_PER_ITEM)))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(new ConfirmTransactionCommandMatcher(
                                transactionIdentifier)));
    }

    @Test
    public void testHandle_NotEnoughMoneyToReserved() {
        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        orderbookIdentifier,
                        portfolioIdentifier,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM))
                .whenAggregate(portfolioIdentifier).publishes(
                new CashReservationRejectedEvent(
                        portfolioIdentifier, transactionIdentifier, TOTAL_ITEMS.multiply(PRICE_PER_ITEM)))
                .expectActiveSagas(0);
    }

    @Test
    public void testHandle_TransactionConfirmed() {
        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        orderbookIdentifier,
                        portfolioIdentifier,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM))
                .andThenAggregate(portfolioIdentifier).published(
                new CashReservedEvent(
                        portfolioIdentifier,
                        transactionIdentifier,
                        TOTAL_ITEMS.multiply(PRICE_PER_ITEM)))
                .whenAggregate(transactionIdentifier).publishes(new BuyTransactionConfirmedEvent(transactionIdentifier,new Date()))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(exactSequenceOf(
                        new CreateBuyOrderCommandMatcher(portfolioIdentifier,
                                orderbookIdentifier,
                                TOTAL_ITEMS,
                                PRICE_PER_ITEM)));
    }

    @Test
    public void testHandle_TransactionCancelled() {
        fixture.givenAggregate(transactionIdentifier).published(new BuyTransactionStartedEvent(transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                TOTAL_ITEMS,
                PRICE_PER_ITEM))
                .whenAggregate(transactionIdentifier).publishes(
                new BuyTransactionCancelledEvent(transactionIdentifier, TOTAL_ITEMS, BigDecimal.ZERO))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(exactSequenceOf(new CancelMoneyReservationFromPortfolioCommandMatcher(
                        portfolioIdentifier,
                        TOTAL_ITEMS.multiply(PRICE_PER_ITEM))));
    }

    @Test
    public void testHandle_TradeExecutedPlaced() {
        OrderId sellOrderIdentifier = new OrderId();
        OrderId buyOrderIdentifier = new OrderId();

        TransactionId sellTransactionIdentifier = new TransactionId();
        fixture.givenAggregate(transactionIdentifier).published(new BuyTransactionStartedEvent(transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                TOTAL_ITEMS,
                PRICE_PER_ITEM))
                .andThenAggregate(portfolioIdentifier).published(new CashReservedEvent(
                portfolioIdentifier, transactionIdentifier,
                TOTAL_ITEMS.multiply(PRICE_PER_ITEM)))
                .andThenAggregate(transactionIdentifier).published(new BuyTransactionConfirmedEvent(transactionIdentifier,new Date()))
                .whenAggregate(orderbookIdentifier).publishes(new TradeExecutedEvent(orderbookIdentifier,
                TOTAL_ITEMS,
                BigDecimal.valueOf(99),
                buyOrderIdentifier.toString(), //todo change
                sellOrderIdentifier.toString(), //todo change
                transactionIdentifier,
                sellTransactionIdentifier))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(exactSequenceOf(new ExecutedTransactionCommandMatcher(TOTAL_ITEMS,
                        BigDecimal.valueOf(99),
                        transactionIdentifier),
                        andNoMore()));
    }

    @Test
    public void testHandle_BuyTransactionExecuted() {
        OrderId sellOrderIdentifier = new OrderId();
        OrderId buyOrderIdentifier = new OrderId();
        TransactionId sellTransactionIdentifier = new TransactionId();

        fixture.givenAggregate(transactionIdentifier).published(new BuyTransactionStartedEvent(transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                TOTAL_ITEMS,
                PRICE_PER_ITEM))
                .andThenAggregate(portfolioIdentifier).published(new CashReservedEvent(
                portfolioIdentifier, transactionIdentifier,
                TOTAL_ITEMS.multiply(PRICE_PER_ITEM)))
                .andThenAggregate(transactionIdentifier).published(new BuyTransactionConfirmedEvent(transactionIdentifier,new Date()))
                .andThenAggregate(orderbookIdentifier).published(new TradeExecutedEvent(orderbookIdentifier, TOTAL_ITEMS,
                BigDecimal.valueOf(99),
                buyOrderIdentifier.toString(),//todo change
                sellOrderIdentifier.toString(),//todo change
                transactionIdentifier,
                sellTransactionIdentifier))
                .whenAggregate(transactionIdentifier).publishes(new BuyTransactionExecutedEvent(transactionIdentifier, TOTAL_ITEMS, BigDecimal.valueOf(99)))
                .expectActiveSagas(0)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(
                                new ConfirmMoneyReservationFromPortfolionCommandMatcher(portfolioIdentifier,
                                        TOTAL_ITEMS.multiply(BigDecimal.valueOf(99))),
                                new AddItemsToPortfolioCommandMatcher(portfolioIdentifier,
                                        orderbookIdentifier,
                                        TOTAL_ITEMS)));
    }

    @Test
    public void testHandle_BuyTransactionPartiallyExecuted() {
        OrderId sellOrderIdentifier = new OrderId();
        OrderId buyOrderIdentifier = new OrderId();
        TransactionId sellTransactionIdentifier = new TransactionId();

        fixture.givenAggregate(transactionIdentifier).published(new BuyTransactionStartedEvent(transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                TOTAL_ITEMS,
                PRICE_PER_ITEM))
                .andThenAggregate(portfolioIdentifier).published(new CashReservedEvent(
                portfolioIdentifier, transactionIdentifier,
                TOTAL_ITEMS.multiply(PRICE_PER_ITEM)))
                .andThenAggregate(transactionIdentifier).published(new BuyTransactionConfirmedEvent(transactionIdentifier,new Date()))
                .andThenAggregate(orderbookIdentifier).published(new TradeExecutedEvent(orderbookIdentifier,
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(99),
                buyOrderIdentifier.toString(),//todo change
                sellOrderIdentifier.toString(),//todo change
                transactionIdentifier,
                sellTransactionIdentifier))
                .whenAggregate(transactionIdentifier).publishes(new BuyTransactionPartiallyExecutedEvent(transactionIdentifier,
                BigDecimal.valueOf(50), BigDecimal.valueOf(50), BigDecimal.valueOf(99)))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(
                                new ConfirmMoneyReservationFromPortfolionCommandMatcher(portfolioIdentifier, BigDecimal.valueOf(50 * 99)),
                                new AddItemsToPortfolioCommandMatcher(portfolioIdentifier, orderbookIdentifier, BigDecimal.valueOf(50))));
    }

    @Test
    public void testHandle_MultipleBuyTransactionPartiallyExecuted() {
        OrderId sellOrderIdentifier = new OrderId();
        OrderId buyOrderIdentifier = new OrderId();
        TransactionId sellTransactionIdentifier = new TransactionId();

        fixture.givenAggregate(transactionIdentifier).published(new BuyTransactionStartedEvent(transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                TOTAL_ITEMS,
                PRICE_PER_ITEM))
                .andThenAggregate(portfolioIdentifier).published(new CashReservedEvent(
                portfolioIdentifier, transactionIdentifier,
                TOTAL_ITEMS.multiply(PRICE_PER_ITEM)))
                .andThenAggregate(transactionIdentifier).published(new BuyTransactionConfirmedEvent(transactionIdentifier,new Date()))
                .andThenAggregate(orderbookIdentifier).published(new TradeExecutedEvent(orderbookIdentifier,
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(9),
                buyOrderIdentifier.toString(),//todo change
                sellOrderIdentifier.toString(),//todo change
                transactionIdentifier,
                sellTransactionIdentifier))
                .whenAggregate(transactionIdentifier).publishes(
                new BuyTransactionPartiallyExecutedEvent(
                        transactionIdentifier, BigDecimal.valueOf(50), BigDecimal.valueOf(50), BigDecimal.valueOf(99)))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(
                                new ConfirmMoneyReservationFromPortfolionCommandMatcher(portfolioIdentifier, BigDecimal.valueOf(50 * 99)),
                                new AddItemsToPortfolioCommandMatcher(portfolioIdentifier, orderbookIdentifier, BigDecimal.valueOf(50))));
    }
}