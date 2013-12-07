package com.icoin.trading.tradeengine.infrastructure.persistence.mongo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderComparator;
import com.icoin.trading.tradeengine.domain.model.order.BuyOrder;
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
import java.util.TreeSet;

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
    private BuyOrder buyOrder1, buyOrder2, buyOrder3, anotherOrderBookBuyOrder;

    @Autowired
    private BuyOrderRepositoryMongo buyOrderRepository;

    @Before
    public void setUp() throws Exception {
        buyOrderRepository.deleteAll();

        buyOrder1 = new BuyOrder();
        buyOrder1.setOrderBookId(orderBookId);
        buyOrder1.setItemRemaining(BigDecimal.valueOf(100));
        buyOrder1.setTradeAmount(BigDecimal.valueOf(90.9));
        buyOrder1.setItemPrice(BigDecimal.valueOf(8.01));
        buyOrder1.setPlaceDate(placeDate.toDate());

        buyOrder2 = new BuyOrder();
        buyOrder2.setOrderBookId(orderBookId);
        buyOrder2.setItemRemaining(BigDecimal.valueOf(100));
        buyOrder2.setTradeAmount(BigDecimal.valueOf(100));
        buyOrder2.setItemPrice(BigDecimal.valueOf(10.1));
        buyOrder2.setPlaceDate(placeDate.plusMillis(2).toDate());

        buyOrder3 = new BuyOrder();
        buyOrder3.setOrderBookId(orderBookId);
        buyOrder3.setItemRemaining(BigDecimal.valueOf(100));
        buyOrder3.setTradeAmount(BigDecimal.valueOf(10));
        buyOrder3.setItemPrice(BigDecimal.valueOf(10.1));
        buyOrder3.setPlaceDate(placeDate.plusDays(2).toDate());

        anotherOrderBookBuyOrder = new BuyOrder();
        anotherOrderBookBuyOrder.setOrderBookId(anotherOrderBookId);
        anotherOrderBookBuyOrder.setItemRemaining(BigDecimal.valueOf(100));
        anotherOrderBookBuyOrder.setTradeAmount(BigDecimal.valueOf(1000));
        anotherOrderBookBuyOrder.setItemPrice(BigDecimal.valueOf(1));
        anotherOrderBookBuyOrder.setPlaceDate(placeDate.toDate());

        assertThat("Another order book id should not be equal to order book id to prepare the data"
                , anotherOrderBookId, not(equalTo(orderBookId)));

        buyOrderRepository.save(ImmutableList.of(buyOrder1, buyOrder2, buyOrder3, anotherOrderBookBuyOrder));
    }

    @Test
    public void testFindHighest() throws Exception {
        BuyOrder highestOrder = buyOrderRepository.findHighestPricePendingOrder(orderBookId);

        assertThat(highestOrder, equalTo(buyOrder2));
    }

    @Test
    public void testFindDescPendingOrdersByPriceTime() throws Exception {
        List<BuyOrder> buyOrderList = buyOrderRepository.findDescPendingOrdersByPriceTime(
                placeDate.minusDays(1).toDate(),
                buyOrder1.getItemPrice(),
                orderBookId,
                1);

        assertThat(buyOrderList, anyOf(nullValue(), empty()));

        buyOrderList = buyOrderRepository.findDescPendingOrdersByPriceTime(
                placeDate.toDate(),
                buyOrder1.getItemPrice(),
                orderBookId,
                10);

        assertThat(buyOrderList, hasSize(1));
        BuyOrder buyOrder = buyOrderList.get(0);
        assertThat(buyOrder, equalTo(buyOrder1));

        buyOrderList = buyOrderRepository.findDescPendingOrdersByPriceTime(
                placeDate.plusDays(1).toDate(),
                buyOrder1.getItemPrice(),
                orderBookId,
                10);

        assertThat(buyOrderList, hasSize(2));
        assertThat(buyOrderList, contains(buyOrder2, buyOrder1));

        buyOrderList = buyOrderRepository.findDescPendingOrdersByPriceTime(
                placeDate.plusDays(2).toDate(),
                buyOrder1.getItemPrice(),
                orderBookId,
                10);

        assertThat(buyOrderList, hasSize(3));
        assertThat(buyOrderList, contains(buyOrder2, buyOrder3, buyOrder1));

        buyOrderList = buyOrderRepository.findDescPendingOrdersByPriceTime(
                placeDate.plusDays(3).toDate(),
                buyOrder1.getItemPrice(),
                orderBookId,
                2);

        assertThat(buyOrderList, hasSize(2));
        assertThat(buyOrderList, contains(buyOrder2, buyOrder3));

        buyOrderList = buyOrderRepository.findDescPendingOrdersByPriceTime(
                placeDate.plusDays(1).toDate(),
                buyOrder1.getItemPrice().add(BigDecimal.valueOf(0.0000001)),
                orderBookId,
                2);

        assertThat(buyOrderList, hasSize(1));
        assertThat(buyOrderList, contains(buyOrder2));

        buyOrderList = buyOrderRepository.findDescPendingOrdersByPriceTime(
                placeDate.plusDays(1).toDate(),
                buyOrder3.getItemPrice().add(BigDecimal.valueOf(0.0000001)),
                orderBookId,
                2);

        assertThat(buyOrderList, anyOf(nullValue(), empty()));
    }
}
