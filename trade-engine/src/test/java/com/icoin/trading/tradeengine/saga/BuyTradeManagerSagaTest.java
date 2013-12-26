package com.icoin.trading.tradeengine.saga;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-25
 * Time: AM8:23
 * To change this template use File | Settings | File Templates.
 */

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationRejectedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservedEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionPartiallyExecutedEvent;
import com.icoin.trading.tradeengine.domain.events.transaction.BuyTransactionStartedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicyFactory;
import com.icoin.trading.tradeengine.domain.model.commission.FixedRateCommissionPolicy;
import com.icoin.trading.tradeengine.domain.model.order.AbstractOrder;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.TradeType;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.saga.matchers.AddItemToPortfolioCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.CancelMoneyReservationFromPortfolioCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.ConfirmMoneyReservationFromPortfolionCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.ConfirmTransactionCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.CreateBuyOrderCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.ExecutedTransactionCommandMatcher;
import com.icoin.trading.tradeengine.saga.matchers.ReserveMoneyFromPortfolioCommandMatcher;
import org.axonframework.test.saga.AnnotatedSagaTestFixture;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static com.homhon.mongo.TimeUtils.currentTime;
import static org.axonframework.test.matchers.Matchers.andNoMore;
import static org.axonframework.test.matchers.Matchers.exactSequenceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Jettro Coenradie
 */
public class BuyTradeManagerSagaTest {

    private static final BigMoney TOTAL_ITEMS = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100));
    private static final BigMoney PRICE_PER_ITEM = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(100));
    private static final BigMoney TOTAL_MONEY = TOTAL_ITEMS.convertedTo(PRICE_PER_ITEM.getCurrencyUnit(), PRICE_PER_ITEM.getAmount());

    private TransactionId transactionIdentifier = new TransactionId();
    private OrderBookId orderbookIdentifier = new OrderBookId();
    private PortfolioId portfolioIdentifier = new PortfolioId();

    private AnnotatedSagaTestFixture fixture;
    private CommissionPolicyFactory commissionPolicyFactory = mock(CommissionPolicyFactory.class);

    @Before
    public void setUp() throws Exception {
        fixture = new AnnotatedSagaTestFixture(BuyTradeManagerSaga.class);
        when(commissionPolicyFactory.createCommissionPolicy(any(AbstractOrder.class)))
                .thenReturn(new FixedRateCommissionPolicy());

        fixture.registerResource(commissionPolicyFactory);
    }

    @Test
    public void testHandle_SellTransactionStarted() throws Exception {
        fixture.givenAggregate(transactionIdentifier).published()
                .whenAggregate(transactionIdentifier).publishes(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        coinId,
                        orderbookIdentifier,
                        portfolioIdentifier,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(new ReserveMoneyFromPortfolioCommandMatcher(
                                portfolioIdentifier,
                                TOTAL_ITEMS.convertedTo(PRICE_PER_ITEM.getCurrencyUnit(), PRICE_PER_ITEM.getAmount()))));
    }

    @Test
    public void testHandle_MoneyIsReserved() {
        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        coinId,
                        orderbookIdentifier,
                        portfolioIdentifier,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM))
                .whenAggregate(portfolioIdentifier).publishes(
                new CashReservedEvent(portfolioIdentifier,
                        transactionIdentifier,
                        TOTAL_MONEY))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(new ConfirmTransactionCommandMatcher(
                                transactionIdentifier)));
    }

    @Test
    public void testHandle_NotEnoughMoneyToReserved() {
        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        coinId,
                        orderbookIdentifier,
                        portfolioIdentifier,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM))
                .whenAggregate(portfolioIdentifier).publishes(
                new CashReservationRejectedEvent(
                        portfolioIdentifier,
                        transactionIdentifier,
                        TOTAL_MONEY))
                .expectActiveSagas(0);
    }

    @Test
    public void testHandle_TransactionConfirmed() {
        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        coinId,
                        orderbookIdentifier,
                        portfolioIdentifier,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM))
                .andThenAggregate(portfolioIdentifier).published(
                new CashReservedEvent(
                        portfolioIdentifier,
                        transactionIdentifier,
                        TOTAL_MONEY))
                .whenAggregate(transactionIdentifier).publishes(new BuyTransactionConfirmedEvent(transactionIdentifier, new Date()))
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
                coinId,
                orderbookIdentifier,
                portfolioIdentifier,
                TOTAL_ITEMS,
                PRICE_PER_ITEM))
                .whenAggregate(transactionIdentifier).publishes(
                new BuyTransactionCancelledEvent(transactionIdentifier,
                        TOTAL_ITEMS,
                        BigMoney.zero(CurrencyUnit.of(Currencies.BTC)),
                        PRICE_PER_ITEM))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(new CancelMoneyReservationFromPortfolioCommandMatcher(
                                portfolioIdentifier,
                                TOTAL_MONEY)));
    }

    @Test
    public void testHandle_TradeExecutedPlaced() {
        OrderId sellOrderIdentifier = new OrderId();
        OrderId buyOrderIdentifier = new OrderId();


        TransactionId sellTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new BuyTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderbookIdentifier,
                                portfolioIdentifier,
                                TOTAL_ITEMS,
                                PRICE_PER_ITEM))
                .andThenAggregate(portfolioIdentifier)
                .published(
                        new CashReservedEvent(
                                portfolioIdentifier,
                                transactionIdentifier,
                                TOTAL_MONEY))
                .andThenAggregate(transactionIdentifier)
                .published(new BuyTransactionConfirmedEvent(transactionIdentifier, new Date()))
                .whenAggregate(orderbookIdentifier)
                .publishes(
                        new TradeExecutedEvent(
                                orderbookIdentifier,
                                TOTAL_ITEMS,
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99)),
                                buyOrderIdentifier.toString(),
                                sellOrderIdentifier.toString(),
                                transactionIdentifier,
                                sellTransactionIdentifier,
                                tradeTime,
                                TradeType.SELL))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(exactSequenceOf(
                        new ExecutedTransactionCommandMatcher(TOTAL_ITEMS,
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99)),
                                transactionIdentifier),
                        andNoMore()));
    }

    @Test
    public void testHandle_BuyTransactionExecuted() {
        OrderId sellOrderIdentifier = new OrderId();
        OrderId buyOrderIdentifier = new OrderId();
        TransactionId sellTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        final BigDecimal price = BigDecimal.valueOf(99);
        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new BuyTransactionStartedEvent(
                                transactionIdentifier,
                                coinId, orderbookIdentifier,
                                portfolioIdentifier,
                                TOTAL_ITEMS,
                                PRICE_PER_ITEM))
                .andThenAggregate(portfolioIdentifier)
                .published(
                        new CashReservedEvent(
                                portfolioIdentifier, transactionIdentifier,
                                TOTAL_MONEY))
                .andThenAggregate(transactionIdentifier)
                .published(new BuyTransactionConfirmedEvent(transactionIdentifier, new Date()))
                .andThenAggregate(orderbookIdentifier)
                .published(
                        new TradeExecutedEvent(
                                orderbookIdentifier,
                                TOTAL_ITEMS,
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99)),
                                buyOrderIdentifier.toString(),//todo change
                                sellOrderIdentifier.toString(),//todo change
                                transactionIdentifier,
                                sellTransactionIdentifier,
                                tradeTime,
                                TradeType.SELL))
                .whenAggregate(transactionIdentifier)
                .publishes(
                        new BuyTransactionExecutedEvent(
                                transactionIdentifier,
                                coinId, TOTAL_ITEMS,
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, price)))
                .expectActiveSagas(0)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(
                                new ConfirmMoneyReservationFromPortfolionCommandMatcher(portfolioIdentifier,
                                        TOTAL_ITEMS.convertedTo(PRICE_PER_ITEM.getCurrencyUnit(), BigDecimal.valueOf(99))),
                                new AddItemToPortfolioCommandMatcher(portfolioIdentifier,
                                        orderbookIdentifier,
                                        TOTAL_ITEMS)));
    }

    @Test
    public void testHandle_BuyTransactionPartiallyExecuted() {
        OrderId sellOrderIdentifier = new OrderId();
        OrderId buyOrderIdentifier = new OrderId();
        TransactionId sellTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(
                        transactionIdentifier,
                        coinId, orderbookIdentifier,
                        portfolioIdentifier,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM))
                .andThenAggregate(portfolioIdentifier)
                .published(
                        new CashReservedEvent(
                                portfolioIdentifier,
                                transactionIdentifier,
                                TOTAL_MONEY))
                .andThenAggregate(transactionIdentifier)
                .published(
                        new BuyTransactionConfirmedEvent(transactionIdentifier, new Date()))
                .andThenAggregate(orderbookIdentifier)
                .published(
                        new TradeExecutedEvent(
                                orderbookIdentifier,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99)),
                                buyOrderIdentifier.toString(),//todo change
                                sellOrderIdentifier.toString(),//todo change
                                transactionIdentifier,
                                sellTransactionIdentifier,
                                tradeTime,
                                TradeType.SELL))
                .whenAggregate(transactionIdentifier)
                .publishes(
                        new BuyTransactionPartiallyExecutedEvent(
                                transactionIdentifier,
                                coinId, BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99))))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(
                                new ConfirmMoneyReservationFromPortfolionCommandMatcher(
                                        portfolioIdentifier,
                                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(50 * 99))),
                                new AddItemToPortfolioCommandMatcher(
                                        portfolioIdentifier,
                                        orderbookIdentifier,
                                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)))));
    }

    @Test
    public void testHandle_MultipleBuyTransactionPartiallyExecuted() {
        OrderId sellOrderIdentifier = new OrderId();
        OrderId buyOrderIdentifier = new OrderId();
        TransactionId sellTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        coinId,
                        orderbookIdentifier,
                        portfolioIdentifier,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM))
                .andThenAggregate(portfolioIdentifier).published(
                new CashReservedEvent(
                        portfolioIdentifier,
                        transactionIdentifier,
                        TOTAL_MONEY))
                .andThenAggregate(transactionIdentifier)
                .published(new BuyTransactionConfirmedEvent(transactionIdentifier, new Date()))
                .andThenAggregate(orderbookIdentifier).published(
                new TradeExecutedEvent(
                        orderbookIdentifier,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99)),
                        buyOrderIdentifier.toString(),//todo change
                        sellOrderIdentifier.toString(),//todo change
                        transactionIdentifier,
                        sellTransactionIdentifier,
                        tradeTime,
                        TradeType.SELL))
                .whenAggregate(transactionIdentifier)
                .publishes(
                        new BuyTransactionPartiallyExecutedEvent(
                                transactionIdentifier,
                                coinId, BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99))))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(
                                new ConfirmMoneyReservationFromPortfolionCommandMatcher(
                                        portfolioIdentifier,
                                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(50 * 99))),
                                new AddItemToPortfolioCommandMatcher(
                                        portfolioIdentifier,
                                        orderbookIdentifier,
                                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)))));
    }
}