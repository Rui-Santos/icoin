package com.icoin.trading.tradeengine.application.executor;

import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.application.command.order.ExecuteSellOrderCommand;
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
import org.junit.Ignore;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
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
public class SellOrderExecutorIT {
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
    private SellOrder sellOrder;

    private FixtureConfiguration fixture;
    private SellOrderRepository sellOrderRepository = mock(SellOrderRepository.class);
    private BuyOrderRepository buyOrderRepository = mock(BuyOrderRepository.class);
    private OrderExecutorHelper helper = new OrderExecutorHelper();
    private SellOrderExecutor commandHandler;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(OrderBook.class);
        commandHandler = new SellOrderExecutor();
        fixture.registerAnnotatedCommandHandler(commandHandler);
        commandHandler.setOrderBookRepository(fixture.getRepository());

        OrderExecutorHelper orderExecutorHelper = new OrderExecutorHelper();
        orderExecutorHelper.setBuyOrderRepository(buyOrderRepository);
        orderExecutorHelper.setSellOrderRepository(sellOrderRepository);
        commandHandler.setOrderExecutorHelper(orderExecutorHelper);

        sellOrder = new SellOrder();
        sellOrder.setOrderBookId(orderBookId);
        sellOrder.setTransactionId(transactionId);
        sellOrder.setPrimaryKey(orderId.toString());
        sellOrder.setPortfolioId(portfolioId);
        sellOrder.setPlaceDate(placeDate.toDate());

        helper.setBuyOrderRepository(buyOrderRepository);
        helper.setSellOrderRepository(sellOrderRepository);

        commandHandler.setOrderExecutorHelper(helper);
    }

    @Test
    public void testExecuteSellOrderPriceGeLowestSell() throws Exception {
        BigMoney sellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.04));
        sellOrder.setItemPrice(sellPrice);

        ExecuteSellOrderCommand command =
                new ExecuteSellOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        sellPrice,
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
    public void testExecuteSellOrderPriceHigherThanBuyPrices() throws Exception {
        BigMoney sellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.02));
        sellOrder.setItemPrice(sellPrice);

        ExecuteSellOrderCommand command =
                new ExecuteSellOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        sellPrice,
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
                .expectEvents(new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        sellPrice));
    }

    @Test
    public void testExecuteSellOrderWithStrangeRepoReturns() throws Exception {
        BigMoney sellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.01));

        when(buyOrderRepository
                .findDescPendingOrdersByPriceTime(
                        eq(placeDate.toDate()),
                        eq(sellPrice),
                        eq(orderBookId),
                        eq(100)))
                .thenReturn(createBuyOrders(sellPrice.minus(BigDecimal.valueOf(0.01)), sellPrice.minus(BigDecimal.valueOf(1))));

        ExecuteSellOrderCommand command =
                new ExecuteSellOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        sellPrice,
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
                .expectEvents(new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        sellPrice));


    }

    private List<BuyOrder> createBuyOrders(BigMoney... prices) {
        ArrayList<BuyOrder> list = Lists.newArrayList();

        for (int i = 0; i < prices.length; i++) {
            BuyOrder buyOrder = new BuyOrder();
            buyOrder.setPrimaryKey(new OrderId().toString());
            buyOrder.setItemPrice(prices[i]);
            list.add(buyOrder);
        }
        return list;
    }

    @Test
    public void testExecuteSellOrderWithBuyOrders() throws Exception {
        BigMoney sellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.0));
        BigMoney itemRemaining = BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.valueOf(100));
        sellOrder.setItemPrice(sellPrice);
        sellOrder.setItemRemaining(itemRemaining);
        when(sellOrderRepository.findOne(eq(orderId.toString()))).thenReturn(sellOrder);

        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));
        SellOrder lowestSell = new SellOrder();
        lowestSell.setItemPrice(lowestSellPrice);
        lowestSell.setPrimaryKey(new OrderId().toString());
        when(sellOrderRepository.findLowestPricePendingOrder(eq(orderBookId))).thenReturn(lowestSell);

        BuyOrder buyOrder1 = createBuyOrder(highestBuyPrice, itemRemaining.minus(BigDecimal.TEN));
        BuyOrder buyOrder2 = createBuyOrder(sellPrice, itemRemaining.plus(BigDecimal.valueOf(100)));
        when(buyOrderRepository.findDescPendingOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(sellPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(Arrays.asList(buyOrder1, buyOrder2));

        BigMoney highestBuyPrice1 = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23)).minus(1);
        BuyOrder highestBuy = new BuyOrder();
        highestBuy.setItemPrice(highestBuyPrice1);
        highestBuy.setPrimaryKey(new OrderId().toString());
        when(buyOrderRepository.findHighestPricePendingOrder(eq(orderBookId))).thenReturn(highestBuy);

        ExecuteSellOrderCommand command =
                new ExecuteSellOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        sellPrice,
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
                .expectEvents(new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        sellPrice),
                        new TradeExecutedEvent(orderBookId,
                                itemRemaining.minus(BigDecimal.TEN),
                                highestBuyPrice,
                                buyOrder1.getPrimaryKey(),
                                orderId.toString(),
                                buyOrder1.getTransactionId(),
                                sellOrder.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.SELL),
                        new TradeExecutedEvent(orderBookId,
                                BigMoney.of(CurrencyUnit.of("BTC") , BigDecimal.TEN),
                                sellPrice,
                                buyOrder2.getPrimaryKey(),
                                orderId.toString(),
                                buyOrder2.getTransactionId(),
                                sellOrder.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.SELL),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                lowestSell.getPrimaryKey(),
                                lowestSellPrice),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                highestBuy.getPrimaryKey(),
                                highestBuyPrice1));

        //assertion
        assertThat(sellOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));

        assertThat(buyOrder1.getCompleteDate(), notNullValue());
        assertThat(buyOrder1.getCompleteDate(), equalTo(buyOrder1.getLastTradedTime()));
        assertThat(buyOrder1.getCompleteDate(), equalTo(placeDate.toDate()));
        assertThat(buyOrder1.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrder1.getItemRemaining(), equalTo(BigMoney.zero(CurrencyUnit.of("BTC"))));

        assertThat(buyOrder2.getCompleteDate(), nullValue());
        assertThat(buyOrder2.getLastTradedTime(), equalTo(placeDate.toDate()));
        assertThat(buyOrder2.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(buyOrder2.getItemRemaining().isEqual(itemRemaining.plus(BigDecimal.valueOf(100)).minus(10)), is(true));

        //verify
        verify(buyOrderRepository).findDescPendingOrdersByPriceTime(eq(placeDate.toDate()),
                eq(sellPrice),
                eq(orderBookId),
                eq(100));

        verify(sellOrderRepository, times(2)).save(eq(sellOrder));
        verify(buyOrderRepository, times(2)).save(any(BuyOrder.class));

        verify(sellOrderRepository).findLowestPricePendingOrder(eq(orderBookId));
        verify(buyOrderRepository).findHighestPricePendingOrder(eq(orderBookId));
    }

    @Test
    public void testExecuteSellOrderWithOneExactBuyOrder() throws Exception {
        BigMoney sellPrice = highestBuyPrice;
        BigMoney itemRemaining = tradeAmount;
        sellOrder.setItemPrice(sellPrice);
        sellOrder.setItemRemaining(itemRemaining);
        when(sellOrderRepository.findOne(eq(orderId.toString()))).thenReturn(sellOrder);

        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));
        SellOrder lowestSell = new SellOrder();
        lowestSell.setItemPrice(lowestSellPrice);
        lowestSell.setPrimaryKey(new OrderId().toString());
        when(sellOrderRepository.findLowestPricePendingOrder(eq(orderBookId))).thenReturn(lowestSell);

        BuyOrder buyOrder1 = createBuyOrder(highestBuyPrice, itemRemaining);
        BuyOrder buyOrder2 = createBuyOrder(sellPrice, itemRemaining.plus(BigDecimal.valueOf(100)));
        when(buyOrderRepository.findDescPendingOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(sellPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(Arrays.asList(buyOrder1, buyOrder2));

        BigMoney highestBuyPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23)).minus(1);
        BuyOrder highestBuy = new BuyOrder();
        highestBuy.setItemPrice(highestBuyPrice);
        highestBuy.setPrimaryKey(new OrderId().toString());
        when(buyOrderRepository.findHighestPricePendingOrder(eq(orderBookId))).thenReturn(highestBuy);

        ExecuteSellOrderCommand command =
                new ExecuteSellOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        sellPrice,
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
                .expectEvents(new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        sellPrice),
                        new TradeExecutedEvent(orderBookId,
                                itemRemaining,
                                sellPrice,
                                buyOrder1.getPrimaryKey(),
                                orderId.toString(),
                                buyOrder1.getTransactionId(),
                                sellOrder.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.SELL),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                lowestSell.getPrimaryKey(),
                                lowestSellPrice),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                highestBuy.getPrimaryKey(),
                                highestBuyPrice));

        //assertion
        assertThat(sellOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));

        assertThat(buyOrder1.getCompleteDate(), notNullValue());
        assertThat(buyOrder1.getCompleteDate(), equalTo(buyOrder1.getLastTradedTime()));
        assertThat(buyOrder1.getCompleteDate(), equalTo(placeDate.toDate()));
        assertThat(buyOrder1.getOrderStatus(), equalTo(OrderStatus.DONE));
        assertThat(buyOrder1.getItemRemaining().isEqual(BigMoney.zero(CurrencyUnit.of("BTC"))), is(true));

        //verify
        verify(buyOrderRepository).findDescPendingOrdersByPriceTime(eq(placeDate.toDate()),
                eq(sellPrice),
                eq(orderBookId),
                eq(100));

        verify(sellOrderRepository).save(eq(sellOrder));
        verify(buyOrderRepository).save(any(BuyOrder.class));
    }

    @Test
    public void testExecuteBuyOrderWithOneGreaterRemainingBuyOrder() throws Exception {
        BigMoney sellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(99.04));
        BigMoney itemRemaining = tradeAmount.plus(1);
        sellOrder.setItemPrice(sellPrice);
        sellOrder.setItemRemaining(tradeAmount);
        when(sellOrderRepository.findOne(eq(orderId.toString()))).thenReturn(sellOrder);

        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23));
        SellOrder lowestSell = new SellOrder();
        lowestSell.setItemPrice(lowestSellPrice);
        lowestSell.setPrimaryKey(new OrderId().toString());
        when(sellOrderRepository.findLowestPricePendingOrder(eq(orderBookId))).thenReturn(lowestSell);

        BuyOrder buyOrder1 = createBuyOrder(highestBuyPrice, itemRemaining);
        BuyOrder buyOrder2 = createBuyOrder(sellPrice, itemRemaining.plus(BigDecimal.valueOf(100)));
        when(buyOrderRepository.findDescPendingOrdersByPriceTime(
                eq(placeDate.toDate()),
                eq(sellPrice),
                eq(orderBookId),
                eq(100)
        )).thenReturn(Arrays.asList(buyOrder1, buyOrder2));

        BigMoney highestBuyPrice1 = BigMoney.of(CurrencyUnit.AUD, BigDecimal.valueOf(100.23)).minus(1);
        BuyOrder highestBuy = new BuyOrder();
        highestBuy.setItemPrice(highestBuyPrice1);
        highestBuy.setPrimaryKey(new OrderId().toString());
        when(buyOrderRepository.findHighestPricePendingOrder(eq(orderBookId))).thenReturn(highestBuy);

        ExecuteSellOrderCommand command =
                new ExecuteSellOrderCommand(
                        orderId,
                        portfolioId,
                        orderBookId,
                        transactionId,
                        tradeAmount,
                        sellPrice,
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
                .expectEvents(new RefreshedLowestSellPriceEvent(
                        orderBookId,
                        orderId.toString(),
                        sellPrice),
                        new TradeExecutedEvent(orderBookId,
                                tradeAmount,
                                highestBuyPrice,
                                buyOrder1.getPrimaryKey(),
                                orderId.toString(),
                                buyOrder1.getTransactionId(),
                                sellOrder.getTransactionId(),
                                placeDate.toDate(),
                                TradeType.SELL),
                        new RefreshedLowestSellPriceEvent(
                                orderBookId,
                                lowestSell.getPrimaryKey(),
                                lowestSellPrice),
                        new RefreshedHighestBuyPriceEvent(
                                orderBookId,
                                highestBuy.getPrimaryKey(),
                                highestBuyPrice1));

        //assertion
        assertThat(sellOrder.getCompleteDate(), notNullValue());
        assertThat(sellOrder.getOrderStatus(), equalTo(OrderStatus.DONE));

        assertThat(buyOrder1.getCompleteDate(), nullValue());
        assertThat(buyOrder1.getLastTradedTime(), equalTo(buyOrder1.getLastTradedTime()));
        assertThat(buyOrder1.getOrderStatus(), equalTo(OrderStatus.PENDING));
        assertThat(buyOrder1.getItemRemaining().isEqual(BigMoney.of(CurrencyUnit.of("BTC"), 1)), is(true));

        //verify
        verify(buyOrderRepository).findDescPendingOrdersByPriceTime(eq(placeDate.toDate()),
                eq(sellPrice),
                eq(orderBookId),
                eq(100));

        verify(sellOrderRepository).save(eq(sellOrder));
        verify(buyOrderRepository).save(any(BuyOrder.class));
    }

    private BuyOrder createBuyOrder(BigMoney price, BigMoney itemRemaining) {
        BuyOrder buyOrder = new BuyOrder();
        buyOrder.setItemPrice(price);
        buyOrder.setItemRemaining(itemRemaining);
        buyOrder.setTransactionId(new TransactionId());
        buyOrder.setPrimaryKey(new OrderId().toString());

        return buyOrder;
    }
}
