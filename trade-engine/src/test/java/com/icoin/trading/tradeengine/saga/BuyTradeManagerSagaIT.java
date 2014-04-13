package com.icoin.trading.tradeengine.saga;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-11-25
 * Time: AM8:23
 * To change this template use File | Settings | File Templates.
 */

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.OrderId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.tradeengine.events.portfolio.cash.CashReservationRejectedEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.cash.CashReservedEvent;
import com.icoin.trading.api.tradeengine.events.trade.TradeExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionCancelledEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionConfirmedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionPartiallyExecutedEvent;
import com.icoin.trading.api.tradeengine.events.transaction.BuyTransactionStartedEvent;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicyFactory;
import com.icoin.trading.tradeengine.domain.model.commission.FixedRateCommissionPolicy;
import com.icoin.trading.tradeengine.domain.model.order.Order;
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
import java.math.RoundingMode;
import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.axonframework.test.matchers.Matchers.andNoMore;
import static org.axonframework.test.matchers.Matchers.exactSequenceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Jettro Coenradie
 */
public class BuyTradeManagerSagaIT {

    private static final BigMoney TOTAL_ITEMS = BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100));
    private static final BigMoney PRICE_PER_ITEM = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(100));
    private static final BigMoney TOTAL_MONEY = TOTAL_ITEMS.convertedTo(PRICE_PER_ITEM.getCurrencyUnit(), PRICE_PER_ITEM.getAmount());
    private static final BigMoney TOTAL_COMMISSION = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(5));

    private TransactionId transactionIdentifier = new TransactionId();
    private CoinId coinId = new CoinId();
    private OrderBookId orderBookIdentifier = new OrderBookId();
    private PortfolioId buyPortfolioId = new PortfolioId();
    private PortfolioId sellPortfolioId = new PortfolioId();

    private AnnotatedSagaTestFixture fixture;
    private CommissionPolicyFactory commissionPolicyFactory = mock(CommissionPolicyFactory.class);

    @Before
    public void setUp() throws Exception {
        fixture = new AnnotatedSagaTestFixture(BuyTradeManagerSaga.class);
        when(commissionPolicyFactory.createCommissionPolicy(any(Order.class)))
                .thenReturn(new FixedRateCommissionPolicy());

        fixture.registerResource(commissionPolicyFactory);
    }

    @Test
    public void testHandle_BuyTransactionStarted() throws Exception {
        Date time = currentTime();
        fixture.givenAggregate(transactionIdentifier).published()
                .whenAggregate(transactionIdentifier).publishes(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        coinId,
                        orderBookIdentifier,
                        buyPortfolioId,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM,
                        TOTAL_MONEY,
                        TOTAL_COMMISSION,
                        time))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(new ReserveMoneyFromPortfolioCommandMatcher(
                                buyPortfolioId,
                                transactionIdentifier,
                                TOTAL_MONEY,
                                TOTAL_COMMISSION)));
    }

    @Test
    public void testHandle_MoneyIsReserved() {
        Date time = currentTime();
        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        coinId,
                        orderBookIdentifier,
                        buyPortfolioId,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM,
                        TOTAL_MONEY,
                        TOTAL_COMMISSION,
                        time))
                .whenAggregate(buyPortfolioId).publishes(
                new CashReservedEvent(buyPortfolioId,
                        transactionIdentifier,
                        TOTAL_MONEY,
                        TOTAL_COMMISSION,
                        time))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(new ConfirmTransactionCommandMatcher(
                                transactionIdentifier)));
    }

    @Test
    public void testHandle_NotEnoughMoneyToReserved() {
        Date time = currentTime();
        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        coinId,
                        orderBookIdentifier,
                        buyPortfolioId,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM,
                        TOTAL_MONEY,
                        TOTAL_COMMISSION,
                        time))
                .whenAggregate(buyPortfolioId).publishes(
                new CashReservationRejectedEvent(
                        buyPortfolioId,
                        transactionIdentifier,
                        TOTAL_MONEY,
                        TOTAL_COMMISSION,
                        time))
                .expectActiveSagas(0);
    }

    @Test
    public void testHandle_TransactionConfirmed() {
        Date time = currentTime();
        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        coinId,
                        orderBookIdentifier,
                        buyPortfolioId,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM,
                        TOTAL_MONEY,
                        TOTAL_COMMISSION,
                        time))
                .andThenAggregate(buyPortfolioId).published(
                new CashReservedEvent(
                        buyPortfolioId,
                        transactionIdentifier,
                        TOTAL_MONEY,
                        TOTAL_COMMISSION,
                        time))
                .whenAggregate(transactionIdentifier).publishes(new BuyTransactionConfirmedEvent(transactionIdentifier, new Date()))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(exactSequenceOf(
                        new CreateBuyOrderCommandMatcher(buyPortfolioId,
                                orderBookIdentifier,
                                TOTAL_ITEMS,
                                PRICE_PER_ITEM)));
    }

    @Test
    public void testHandle_TransactionCancelled() {
        Date time = currentTime();
        fixture.givenAggregate(transactionIdentifier).published(new BuyTransactionStartedEvent(transactionIdentifier,
                coinId,
                orderBookIdentifier,
                buyPortfolioId,
                TOTAL_ITEMS,
                PRICE_PER_ITEM,
                TOTAL_MONEY,
                TOTAL_COMMISSION,
                time))
                .whenAggregate(transactionIdentifier).publishes(
                new BuyTransactionCancelledEvent(transactionIdentifier,
                        coinId,
                        time))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(new CancelMoneyReservationFromPortfolioCommandMatcher(
                                buyPortfolioId,
                                TOTAL_MONEY)));
    }

    @Test
    public void testHandle_TradeExecutedPlaced() {
        Date time = currentTime();
        OrderId sellOrderIdentifier = new OrderId();
        OrderId buyOrderIdentifier = new OrderId();


        TransactionId sellTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new BuyTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                buyPortfolioId,
                                TOTAL_ITEMS,
                                PRICE_PER_ITEM,
                                TOTAL_MONEY,
                                TOTAL_COMMISSION,
                                time))
                .andThenAggregate(buyPortfolioId)
                .published(
                        new CashReservedEvent(
                                buyPortfolioId,
                                transactionIdentifier,
                                TOTAL_MONEY,
                                TOTAL_COMMISSION,
                                time))
                .andThenAggregate(transactionIdentifier)
                .published(new BuyTransactionConfirmedEvent(transactionIdentifier, new Date()))
                .whenAggregate(orderBookIdentifier)
                .publishes(
                        new TradeExecutedEvent(
                                orderBookIdentifier,
                                coinId,
                                TOTAL_ITEMS,
                                TOTAL_MONEY,
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99)),
                                buyOrderIdentifier.toString(),
                                sellOrderIdentifier.toString(),
                                TOTAL_COMMISSION.minus(1),
                                BigMoney.of(CurrencyUnit.of("BTC"), 10),
                                transactionIdentifier,
                                sellTransactionIdentifier,
                                buyPortfolioId,
                                sellPortfolioId,
                                tradeTime,
                                TradeType.SELL))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(exactSequenceOf(
                        new ExecutedTransactionCommandMatcher(TOTAL_ITEMS,
                                TOTAL_MONEY,
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99)),
                                TOTAL_COMMISSION.minus(1),
                                transactionIdentifier,
                                coinId),
                        andNoMore()));
    }

    @Test
    public void testHandle_BuyTransactionExecuted() {
        Date time = currentTime();
        OrderId sellOrderIdentifier = new OrderId();
        OrderId buyOrderIdentifier = new OrderId();
        TransactionId sellTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        final BigDecimal price = BigDecimal.valueOf(99);
        fixture.givenAggregate(transactionIdentifier)
                .published(
                        new BuyTransactionStartedEvent(
                                transactionIdentifier,
                                coinId,
                                orderBookIdentifier,
                                buyPortfolioId,
                                TOTAL_ITEMS,
                                PRICE_PER_ITEM,
                                TOTAL_MONEY,
                                TOTAL_COMMISSION,
                                time))
                .andThenAggregate(buyPortfolioId)
                .published(
                        new CashReservedEvent(
                                buyPortfolioId, transactionIdentifier,
                                TOTAL_MONEY,
                                TOTAL_COMMISSION,
                                time))
                .andThenAggregate(transactionIdentifier)
                .published(new BuyTransactionConfirmedEvent(transactionIdentifier, new Date()))
                .andThenAggregate(orderBookIdentifier)
                .published(
                        new TradeExecutedEvent(
                                orderBookIdentifier,
                                coinId,
                                TOTAL_ITEMS,
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, price),
                                TOTAL_ITEMS.convertedTo(PRICE_PER_ITEM.getCurrencyUnit(), price),
                                buyOrderIdentifier.toString(),//todo change
                                sellOrderIdentifier.toString(),//todo change
                                TOTAL_COMMISSION,
                                BigMoney.of(CurrencyUnit.of("BTC"), 10),
                                transactionIdentifier,
                                sellTransactionIdentifier,
                                buyPortfolioId,
                                sellPortfolioId,
                                tradeTime,
                                TradeType.SELL))
                .whenAggregate(transactionIdentifier)
                .publishes(
                        new BuyTransactionExecutedEvent(
                                transactionIdentifier,
                                coinId,
                                TOTAL_ITEMS,
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, price),
                                TOTAL_ITEMS.convertedTo(PRICE_PER_ITEM.getCurrencyUnit(), price),
                                TOTAL_COMMISSION,
                                time))
                .expectActiveSagas(0)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(
                                new ConfirmMoneyReservationFromPortfolionCommandMatcher(buyPortfolioId,
                                        TOTAL_ITEMS.convertedTo(PRICE_PER_ITEM.getCurrencyUnit(), price),
                                        TOTAL_COMMISSION),
                                new AddItemToPortfolioCommandMatcher(buyPortfolioId,
                                        coinId,
                                        TOTAL_ITEMS)));
    }

    @Test
    public void testHandle_BuyTransactionPartiallyExecuted() {
        Date time = currentTime();
        OrderId sellOrderIdentifier = new OrderId();
        OrderId buyOrderIdentifier = new OrderId();
        TransactionId sellTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(
                        transactionIdentifier,
                        coinId,
                        orderBookIdentifier,
                        buyPortfolioId,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM,
                        TOTAL_MONEY,
                        TOTAL_COMMISSION,
                        time))
                .andThenAggregate(buyPortfolioId)
                .published(
                        new CashReservedEvent(
                                buyPortfolioId,
                                transactionIdentifier,
                                TOTAL_MONEY,
                                TOTAL_COMMISSION,
                                time))
                .andThenAggregate(transactionIdentifier)
                .published(
                        new BuyTransactionConfirmedEvent(transactionIdentifier, new Date()))
                .andThenAggregate(orderBookIdentifier)
                .published(
                        new TradeExecutedEvent(
                                orderBookIdentifier,
                                coinId,
                                TOTAL_ITEMS.dividedBy(2, RoundingMode.HALF_EVEN),
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99)),
                                TOTAL_MONEY,
                                buyOrderIdentifier.toString(),//todo change
                                sellOrderIdentifier.toString(),//todo change
                                TOTAL_MONEY,
                                TOTAL_COMMISSION,
                                transactionIdentifier,
                                sellTransactionIdentifier,
                                buyPortfolioId,
                                sellPortfolioId,
                                tradeTime,
                                TradeType.SELL))
                .whenAggregate(transactionIdentifier)
                .publishes(
                        new BuyTransactionPartiallyExecutedEvent(
                                transactionIdentifier,
                                coinId,
                                TOTAL_ITEMS.dividedBy(2, RoundingMode.HALF_EVEN),
                                TOTAL_ITEMS,
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99)),
                                TOTAL_ITEMS.dividedBy(2, RoundingMode.HALF_EVEN).convertedTo(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99)),
                                TOTAL_COMMISSION,
                                time))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(
                                new ConfirmMoneyReservationFromPortfolionCommandMatcher(
                                        buyPortfolioId,
                                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(50 * 99)),
                                        TOTAL_COMMISSION),
                                new AddItemToPortfolioCommandMatcher(
                                        buyPortfolioId,
                                        coinId,
                                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)))));
    }

    @Test
    public void testHandle_MultipleBuyTransactionPartiallyExecuted() {
        Date time = currentTime();
        OrderId sellOrderIdentifier = new OrderId();
        OrderId buyOrderIdentifier = new OrderId();
        TransactionId sellTransactionIdentifier = new TransactionId();
        final Date tradeTime = currentTime();

        fixture.givenAggregate(transactionIdentifier).published(
                new BuyTransactionStartedEvent(transactionIdentifier,
                        coinId,
                        orderBookIdentifier,
                        buyPortfolioId,
                        TOTAL_ITEMS,
                        PRICE_PER_ITEM,
                        TOTAL_MONEY,
                        TOTAL_COMMISSION,
                        time))
                .andThenAggregate(buyPortfolioId).published(
                new CashReservedEvent(
                        buyPortfolioId,
                        transactionIdentifier,
                        TOTAL_MONEY,
                        TOTAL_COMMISSION,
                        time))
                .andThenAggregate(transactionIdentifier)
                .published(new BuyTransactionConfirmedEvent(transactionIdentifier, new Date()))
                .andThenAggregate(orderBookIdentifier).published(
                new TradeExecutedEvent(
                        orderBookIdentifier,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(50 * 99)),
                        buyOrderIdentifier.toString(),//todo change
                        sellOrderIdentifier.toString(),//todo change
                        TOTAL_COMMISSION.dividedBy(2, RoundingMode.HALF_EVEN),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(5)),
                        transactionIdentifier,
                        sellTransactionIdentifier,
                        buyPortfolioId,
                        sellPortfolioId,
                        tradeTime,
                        TradeType.SELL))
                .whenAggregate(transactionIdentifier)
                .publishes(
                        new BuyTransactionPartiallyExecutedEvent(
                                transactionIdentifier,
                                coinId,
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)),
                                BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)),
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(99)),
                                BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(50 * 99)),
                                TOTAL_COMMISSION.dividedBy(2, RoundingMode.HALF_EVEN),
                                time))
                .expectActiveSagas(1)
                .expectDispatchedCommandsMatching(
                        exactSequenceOf(
                                new ConfirmMoneyReservationFromPortfolionCommandMatcher(
                                        buyPortfolioId,
                                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(50 * 99)),
                                        TOTAL_COMMISSION.dividedBy(2, RoundingMode.HALF_EVEN)),
                                new AddItemToPortfolioCommandMatcher(
                                        buyPortfolioId,
                                        coinId,
                                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(50)))));
    }
}