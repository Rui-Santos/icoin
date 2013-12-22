package com.icoin.trading.tradeengine.application.command.order.handler;

import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.application.command.order.ExecuteBuyOrderCommand;
import com.icoin.trading.tradeengine.application.command.order.handler.BuyOrderExecutor;
import com.icoin.trading.tradeengine.application.command.order.handler.OrderExecutorHelper;
import com.icoin.trading.tradeengine.domain.events.order.OrderBookCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.order.RefreshedHighestBuyPriceEvent;
import com.icoin.trading.tradeengine.domain.events.order.RefreshedLowestSellPriceEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
import com.icoin.trading.tradeengine.domain.model.order.SellOrderRepository;
import com.icoin.trading.tradeengine.domain.model.order.TradeType;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/4/13
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
//@Ignore
public class BuyOrderExecutorIT {
    private OrderBookId orderBookId = new OrderBookId();
    private OrderId orderId = new OrderId();
    private OrderId highestBuyOrderId = new OrderId();
    private OrderId lowestSellOrderId = new OrderId();
    private BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.valueOf(100.009));
    private BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.03));
    private BigMoney highestBuyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.01));
    private LocalDate placeDate = LocalDate.now();
    private PortfolioId portfolioId = new PortfolioId();
    private TransactionId transactionId = new TransactionId();
    private BuyOrder buyOrder;

    private FixtureConfiguration fixture;
    private SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
    private BuyOrderRepository buyOrderRepository = mock(BuyOrderRepository.class);
    private OrderExecutorHelper helper = new OrderExecutorHelper();
    private BuyOrderExecutor commandHandler;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(OrderBook.class);
        commandHandler = new BuyOrderExecutor();
        fixture.registerAnnotatedCommandHandler(commandHandler);
        commandHandler.setOrderBookRepository(fixture.getRepository());

        OrderExecutorHelper orderExecutorHelper = new OrderExecutorHelper();
        orderExecutorHelper.setBuyOrderRepository(buyOrderRepository);
        orderExecutorHelper.setSellOrderRepository(sellOrderRepository);
        commandHandler.setOrderExecutorHelper(orderExecutorHelper);

        buyOrder = new BuyOrder();
        buyOrder.setOrderBookId(orderBookId);
        buyOrder.setTransactionId(transactionId);
        buyOrder.setPrimaryKey(orderId.toString());
        buyOrder.setPortfolioId(portfolioId);
        buyOrder.setPlaceDate(placeDate.toDate());

        helper.setBuyOrderRepository(buyOrderRepository);
        helper.setSellOrderRepository(sellOrderRepository);

        commandHandler.setOrderExecutorHelper(helper);
    }

    @Test
    public void testExecuteBuyOrderPriceLeHighestBuy() throws Exception {
        BigMoney buyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.0));
        buyOrder.setItemPrice(buyPrice);

        ExecuteBuyOrderCommand command =
                new ExecuteBuyOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        buyPrice,
                        placeDate.toDate());

        fixture.given(
                new OrderBookCreatedEvent(
                        orderBookId,
                        new CurrencyPair("BTC", "AUD")),
                new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        highestBuyOrderId.toString(),
                        highestBuyPrice),
                new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        lowestSellOrderId.toString(),
                        lowestSellPrice))
                .when(command)
                .expectEvents();
    }

    @Test
    public void testExecuteBuyOrderPriceLowerThanSellPrices() throws Exception {
        BigMoney buyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.02));
        buyOrder.setItemPrice(buyPrice);

        ExecuteBuyOrderCommand command =
                new ExecuteBuyOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        buyPrice,
                        placeDate.toDate());

        fixture.given(
                new OrderBookCreatedEvent(
                        orderBookId,
                        new CurrencyPair("BTC", "AUD")),
                new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        highestBuyOrderId.toString(),
                        highestBuyPrice),
                new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        lowestSellOrderId.toString(),
                        lowestSellPrice))
                .when(command)
                .expectEvents(new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        buyPrice));
    }

    @Test
    public void testExecuteBuyOrderWithStrangeRepoReturns() throws Exception {
        BigMoney buyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.04));
        buyOrder.setItemPrice(buyPrice);

        when(sellOrderRepository.findAscPendingOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100)))
                .thenReturn(createSellOrders(buyPrice.plus(BigDecimal.valueOf(0.01)), buyPrice.plus(BigDecimal.valueOf(1))));

        ExecuteBuyOrderCommand command =
                new ExecuteBuyOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        buyPrice,
                        placeDate.toDate());

        fixture.given(
                new OrderBookCreatedEvent(
                        orderBookId,
                        new CurrencyPair("BTC", "AUD")),
                new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        highestBuyOrderId.toString(),
                        highestBuyPrice),
                new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        lowestSellOrderId.toString(),
                        lowestSellPrice))
                .when(command)
                .expectEvents(new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        buyPrice));


    }

    private List<SellOrder> createSellOrders(BigMoney... prices) {
        ArrayList<SellOrder> list = Lists.newArrayList();

        for (int i = 0; i < prices.length; i++) {
            SellOrder sellOrder = new SellOrder();
            sellOrder.setPrimaryKey(new OrderId().toString());
            sellOrder.setItemPrice(prices[i]);
            list.add(sellOrder);
        }
        return list;
    }

    @Test
    public void testExecuteBuyOrderWithSellOrders() throws Exception {
        BigMoney buyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.04));
        BigMoney itemRemaining = BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.valueOf(100));
        buyOrder.setItemPrice(buyPrice);
        buyOrder.setItemRemaining(itemRemaining);
        when(buyOrderRepository.findOne(eq(orderId.toString()))).thenReturn(buyOrder);

        BigMoney highestBuyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23)).minus(1);
        BuyOrder highestBuy = new BuyOrder();
        highestBuy.setItemPrice(highestBuyPrice);
        highestBuy.setPrimaryKey(new OrderId().toString());
        when(buyOrderRepository.findHighestPricePendingOrder(eq(orderBookId))).thenReturn(highestBuy);

        SellOrder sellOrder1 = createSellOrder(highestBuyPrice, itemRemaining.minus(BigDecimal.TEN));
        SellOrder sellOrder2 = createSellOrder(buyPrice, itemRemaining.plus(BigDecimal.valueOf(100)));
        when(sellOrderRepository.findAscPendingOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(Arrays.asList(sellOrder1, sellOrder2));

        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));
        SellOrder lowestSell = new SellOrder();
        lowestSell.setItemPrice(lowestSellPrice);
        lowestSell.setPrimaryKey(new OrderId().toString());
        when(sellOrderRepository.findLowestPricePendingOrder(eq(orderBookId))).thenReturn(lowestSell);

        ExecuteBuyOrderCommand command =
                new ExecuteBuyOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        buyPrice,
                        placeDate.toDate());

        fixture.given(
                new OrderBookCreatedEvent(
                        orderBookId,
                        new CurrencyPair("BTC", "AUD")),
                new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        highestBuyOrderId.toString(),
                        highestBuyPrice),
                new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        lowestSellOrderId.toString(),
                        lowestSellPrice))
                .when(command)
                .expectEvents(new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        buyPrice),
                        new TradeExecutedEvent(orderBookId,
                                itemRemaining.minus(BigDecimal.TEN),
                                highestBuyPrice,
                                orderId.toString(),
                                sellOrder1.getPrimaryKey(),
                                buyOrder.getTransactionId(),
                                sellOrder1.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.BUY),
                        new TradeExecutedEvent(orderBookId,
                                BigMoney.of(CurrencyUnit.of("BTC") , BigDecimal.TEN),
                                buyPrice,
                                orderId.toString(),
                                sellOrder2.getPrimaryKey(),
                                buyOrder.getTransactionId(),
                                sellOrder2.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.BUY),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                lowestSell.getPrimaryKey(),
                                lowestSellPrice),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                highestBuy.getPrimaryKey(),
                                highestBuyPrice));

        //assertion
        assertThat(buyOrder.getCompleteDate(), notNullValue());
        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.DONE));

        assertThat(sellOrder1.getCompleteDate(), notNullValue());
        assertThat(sellOrder1.getCompleteDate(), equalTo(sellOrder1.getLastTradedTime()));
        assertThat(sellOrder1.getCompleteDate(), equalTo(placeDate.toDate()));
        assertThat(sellOrder1.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(sellOrder1.getItemRemaining(), equalTo(BigMoney.zero(CurrencyUnit.of("BTC"))));

        assertThat(sellOrder2.getCompleteDate(), nullValue());
        assertThat(sellOrder2.getLastTradedTime(), equalTo(placeDate.toDate()));
        assertThat(sellOrder2.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(sellOrder2.getItemRemaining().isEqual(itemRemaining.plus(BigDecimal.valueOf(100)).minus(10)), is(true));

        //verify
        verify(sellOrderRepository).findAscPendingOrdersByPriceTime(eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100));

        verify(buyOrderRepository, times(2)).save(eq(buyOrder));
        verify(sellOrderRepository, times(2)).save(any(SellOrder.class));

        verify(sellOrderRepository).findLowestPricePendingOrder(eq(orderBookId));
        verify(buyOrderRepository).findHighestPricePendingOrder(eq(orderBookId));
    }

    @Test
    public void testExecuteBuyOrderWithOneExactSellOrder() throws Exception {
        BigMoney buyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.05));
        BigMoney itemRemaining = tradeAmount;
        buyOrder.setItemPrice(buyPrice);
        buyOrder.setItemRemaining(itemRemaining);
        when(buyOrderRepository.findOne(eq(orderId.toString()))).thenReturn(buyOrder);

        BigMoney highestBuyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23)).minus(1);
        BuyOrder highestBuy = new BuyOrder();
        highestBuy.setItemPrice(highestBuyPrice);
        highestBuy.setPrimaryKey(new OrderId().toString());
        when(buyOrderRepository.findHighestPricePendingOrder(eq(orderBookId))).thenReturn(highestBuy);

        SellOrder sellOrder1 = createSellOrder(highestBuyPrice, itemRemaining);
        SellOrder sellOrder2 = createSellOrder(buyPrice, itemRemaining.plus(BigDecimal.valueOf(100)));
        when(sellOrderRepository.findAscPendingOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(Arrays.asList(sellOrder1, sellOrder2));

        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));
        SellOrder lowestSell = new SellOrder();
        lowestSell.setItemPrice(lowestSellPrice);
        lowestSell.setPrimaryKey(new OrderId().toString());
        when(sellOrderRepository.findLowestPricePendingOrder(eq(orderBookId))).thenReturn(lowestSell);

        ExecuteBuyOrderCommand command =
                new ExecuteBuyOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        buyPrice,
                        placeDate.toDate());

        fixture.given(
                new OrderBookCreatedEvent(
                        orderBookId,
                        new CurrencyPair("BTC", "AUD")),
                new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        highestBuyOrderId.toString(),
                        highestBuyPrice),
                new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        lowestSellOrderId.toString(),
                        lowestSellPrice))
                .when(command)
                .expectEvents(new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        buyPrice),
                        new TradeExecutedEvent(orderBookId,
                                itemRemaining,
                                highestBuyPrice,
                                orderId.toString(),
                                sellOrder1.getPrimaryKey(),
                                buyOrder.getTransactionId(),
                                sellOrder1.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.BUY),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                lowestSell.getPrimaryKey(),
                                lowestSellPrice),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                highestBuy.getPrimaryKey(),
                                highestBuyPrice));

        //assertion
        assertThat(buyOrder.getCompleteDate(), notNullValue());
        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.DONE));

        assertThat(sellOrder1.getCompleteDate(), notNullValue());
        assertThat(sellOrder1.getCompleteDate(), equalTo(sellOrder1.getLastTradedTime()));
        assertThat(sellOrder1.getCompleteDate(), equalTo(placeDate.toDate()));
        assertThat(sellOrder1.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(sellOrder1.getItemRemaining().isEqual(BigMoney.zero(CurrencyUnit.of("BTC"))), is(true));

        //verify
        verify(sellOrderRepository).findAscPendingOrdersByPriceTime(eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100));

        verify(buyOrderRepository).save(eq(buyOrder));
        verify(sellOrderRepository).save(any(SellOrder.class));

        verify(sellOrderRepository).findLowestPricePendingOrder(eq(orderBookId));
        verify(buyOrderRepository).findHighestPricePendingOrder(eq(orderBookId));
    }

    @Test
    public void testExecuteBuyOrderWithOneGreaterRemainingSellOrder() throws Exception {
        BigMoney buyPrice = lowestSellPrice;
        BigMoney itemRemaining = tradeAmount.plus(1);
        buyOrder.setItemPrice(buyPrice);
        buyOrder.setItemRemaining(itemRemaining);
        when(buyOrderRepository.findOne(eq(orderId.toString()))).thenReturn(buyOrder);

        SellOrder sellOrder1 = createSellOrder(buyPrice, itemRemaining.plus(1));
        SellOrder sellOrder2 = createSellOrder(buyPrice, itemRemaining.plus(BigDecimal.valueOf(100)));
        when(sellOrderRepository.findAscPendingOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(Arrays.asList(sellOrder1, sellOrder2));

        ExecuteBuyOrderCommand command =
                new ExecuteBuyOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        buyPrice,
                        placeDate.toDate());

        fixture.given(
                new OrderBookCreatedEvent(
                        orderBookId,
                        new CurrencyPair("BTC", "AUD")),
                new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        highestBuyOrderId.toString(),
                        highestBuyPrice),
                new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        lowestSellOrderId.toString(),
                        lowestSellPrice))
                .when(command)
                .expectEvents(new RefreshedHighestBuyPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        buyPrice),
                        new TradeExecutedEvent(orderBookId,
                                itemRemaining,
                                lowestSellPrice,
                                orderId.toString(),
                                sellOrder1.getPrimaryKey(),
                                buyOrder.getTransactionId(),
                                sellOrder1.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.BUY));

        //assertion
        assertThat(buyOrder.getCompleteDate(), notNullValue());
        assertThat(buyOrder.getOrderStatus(), equalTo(OrderStatus.DONE));

        assertThat(sellOrder1.getCompleteDate(), nullValue());
        assertThat(sellOrder1.getLastTradedTime(), equalTo(sellOrder1.getLastTradedTime()));
        assertThat(sellOrder1.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(sellOrder1.getItemRemaining().isEqual(BigMoney.of(CurrencyUnit.of("BTC"), 1)), is(true));

        //verify
        verify(sellOrderRepository).findAscPendingOrdersByPriceTime(eq(placeDate.toDate()),
                eq(buyPrice),
                eq(orderBookId),
                eq(100));

        verify(buyOrderRepository).save(eq(buyOrder));
        verify(sellOrderRepository).save(any(SellOrder.class));
    }

    private SellOrder createSellOrder(BigMoney price, BigMoney itemRemaining) {
        SellOrder sellOrder = new SellOrder();
        sellOrder.setItemPrice(price);
        sellOrder.setItemRemaining(itemRemaining);
        sellOrder.setTransactionId(new TransactionId());
        sellOrder.setPrimaryKey(new OrderId().toString());

        return sellOrder;
    }
}