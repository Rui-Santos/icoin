package com.icoin.trading.tradeengine.infrastructure.persistence.mongo;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderType;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

import static com.homhon.util.TimeUtils.currentLocalTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-7
 * Time: AM12:45
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:com/icoin/trading/tradeengine/infrastructure/persistence/mongo/tradeengine-persistence-mongo.xml"})
@SuppressWarnings("SpringJavaAutowiringInspection")
public class BuyOrderRepositoryMongoIT {
    private final LocalDateTime placeDate = currentLocalTime();
    private OrderBookId orderBookId = new OrderBookId(), anotherOrderBookId = new OrderBookId();
    private Order buyOrder1, buyOrder2, buyOrder3, anotherOrderBookBuyOrder;

    @Autowired
    private OrderRepositoryMongo orderRepository;

    @Before
    public void setUp() throws Exception {
        orderRepository.deleteAll();

        buyOrder1 = new Order(OrderType.BUY);
        buyOrder1.setOrderBookId(orderBookId);
        buyOrder1.setItemRemaining(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)));
        buyOrder1.setTradeAmount(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(90.9)));
        buyOrder1.setItemPrice(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(8.01)));
        buyOrder1.setPlaceDate(placeDate.toDate());

        buyOrder2 = new Order(OrderType.BUY);
        buyOrder2.setOrderBookId(orderBookId);
        buyOrder2.setItemRemaining(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)));
        buyOrder2.setTradeAmount(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)));
        buyOrder2.setItemPrice(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10.1)));
        buyOrder2.setPlaceDate(placeDate.plusMillis(2).toDate());

        buyOrder3 = new Order(OrderType.BUY);
        buyOrder3.setOrderBookId(orderBookId);
        buyOrder3.setItemRemaining(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)));
        buyOrder3.setTradeAmount(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)));
        buyOrder3.setItemPrice(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10.1)));
        buyOrder3.setPlaceDate(placeDate.plusDays(2).toDate());

        anotherOrderBookBuyOrder = new Order(OrderType.BUY);
        anotherOrderBookBuyOrder.setItemRemaining(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)));
        anotherOrderBookBuyOrder.setTradeAmount(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1000)));
        anotherOrderBookBuyOrder.setItemPrice(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(1)));
        anotherOrderBookBuyOrder.setPlaceDate(placeDate.toDate());

        assertThat("Another order book id should not be equal to order book id to prepare the data"
                , anotherOrderBookId, not(equalTo(orderBookId)));

        orderRepository.save(ImmutableList.of(buyOrder1, buyOrder2, buyOrder3, anotherOrderBookBuyOrder));
    }

    @Test
    public void testFindHighest() throws Exception {
        Order highestOrder = orderRepository.findHighestPricePendingBuyOrder(orderBookId);

        assertThat(highestOrder, equalTo(buyOrder2));
    }

    @Test
    public void testFindPendingOrdersByPriceTime() throws Exception {
        List<Order> buyOrderList = orderRepository.findPendingBuyOrdersByPriceTime(
                placeDate.minusDays(1).toDate(),
                buyOrder1.getItemPrice(),
                orderBookId,
                1);

        assertThat(buyOrderList, anyOf(nullValue(), empty()));

        buyOrderList = orderRepository.findPendingBuyOrdersByPriceTime(
                placeDate.toDate(),
                buyOrder1.getItemPrice(),
                orderBookId,
                10);

        assertThat(buyOrderList, hasSize(1));
        Order buyOrder = buyOrderList.get(0);
        assertThat(buyOrder, equalTo(buyOrder1));

        buyOrderList = orderRepository.findPendingBuyOrdersByPriceTime(
                placeDate.plusDays(1).toDate(),
                buyOrder1.getItemPrice(),
                orderBookId,
                10);

        assertThat(buyOrderList, hasSize(2));
        assertThat(buyOrderList, contains(buyOrder1, buyOrder2));

        buyOrderList = orderRepository.findPendingBuyOrdersByPriceTime(
                placeDate.plusDays(2).toDate(),
                buyOrder1.getItemPrice(),
                orderBookId,
                10);

        assertThat(buyOrderList, hasSize(3));
        assertThat(buyOrderList, contains(buyOrder1, buyOrder2, buyOrder3));

        buyOrderList = orderRepository.findPendingBuyOrdersByPriceTime(
                placeDate.plusDays(3).toDate(),
                buyOrder1.getItemPrice(),
                orderBookId,
                2);

        assertThat(buyOrderList, hasSize(2));
        assertThat(buyOrderList, contains(buyOrder1, buyOrder2));

        buyOrderList = orderRepository.findPendingBuyOrdersByPriceTime(
                placeDate.plusDays(1).toDate(),
                buyOrder1.getItemPrice().plus(BigDecimal.valueOf(0.01)),
                orderBookId,
                2);

        assertThat(buyOrderList, hasSize(1));
        assertThat(buyOrderList, contains(buyOrder2));

        buyOrderList = orderRepository.findPendingBuyOrdersByPriceTime(
                placeDate.plusDays(1).toDate(),
                buyOrder3.getItemPrice().plus(BigDecimal.valueOf(0.01)),
                orderBookId,
                2);

        assertThat(buyOrderList, anyOf(nullValue(), empty()));
    }
}
