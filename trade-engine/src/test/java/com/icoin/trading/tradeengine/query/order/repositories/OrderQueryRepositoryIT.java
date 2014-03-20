package com.icoin.trading.tradeengine.query.order.repositories;

import com.google.common.collect.Lists;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static com.homhon.util.TimeUtils.currentTime;
import static com.homhon.util.TimeUtils.futureDate;
import static com.homhon.util.TimeUtils.futureMinute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-11
 * Time: AM12:44
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:com/icoin/trading/tradeengine/infrastructure/persistence/mongo/tradeengine-persistence-mongo.xml"})
public class OrderQueryRepositoryIT {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private OrderQueryRepository repository;

    private OrderEntry buyOrder1;
    private OrderEntry buyOrder2;
    private OrderEntry sellOrder1;
    private OrderEntry sellOrder2;
    private OrderBookId orderBookId = new OrderBookId();

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();

        final Date placeDate = currentTime();
        buyOrder1 = createOrderEntry("buyer1", OrderType.BUY, BigMoney.of(CurrencyUnit.EUR, 100), placeDate);
        buyOrder2 = createOrderEntry("buyer1", OrderType.BUY, BigMoney.of(CurrencyUnit.EUR, 10), futureMinute(placeDate, -1));
        sellOrder1 = createOrderEntry("buyer1", OrderType.SELL, BigMoney.of(CurrencyUnit.EUR, 10.8), futureDate(placeDate, 1));
        sellOrder2 = createOrderEntry("seller1", OrderType.SELL, BigMoney.of(CurrencyUnit.EUR, 109), futureDate(placeDate, -2));

        repository.save(Lists.newArrayList(buyOrder1, buyOrder2, sellOrder1, sellOrder2));
    }

    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
    }

    @Test
    public void testFindOrderAggregatedPrice() throws Exception {
//        final List<PriceAggregate> orderAggregatedPrice =
//                repository.findOrderAggregatedPrice("0ce4f7bb-592c-4671-8e87-8e691f345bc5", OrderType.SELL);
//
//        System.out.println(orderAggregatedPrice);
    }
    
    @Test
    public void testFindUserActiveOrders() throws Exception {
        List<OrderEntry> list = repository.findUserActiveOrders("buyer1", orderBookId.toString());

        assertThat(list, hasSize(3));
        assertThat(list, contains(sellOrder1, buyOrder1, buyOrder2));

    }

    @Test
    public void testFindUserAllOrders() {
        final List<OrderEntry> allForBuyer1 = repository.findAllUserOrders("buyer1", 0, 10);
        final List<OrderEntry> twoForBuyer1 = repository.findAllUserOrders("buyer1", 1, 10);

        assertThat(allForBuyer1, hasSize(3));
        assertThat(allForBuyer1, contains(sellOrder1, buyOrder1, buyOrder2));

        assertThat(twoForBuyer1, hasSize(2));
        assertThat(twoForBuyer1, contains(buyOrder1, buyOrder2));
    }


//    @Test
//    public void testFindActiveHintSellOrders() {
//        final List<OrderEntry> allSells = repository.findActiveHintSellOrders(orderBookId.toString(), 0, 10);
//        final List<OrderEntry> oneSell = repository.findActiveHintSellOrders(orderBookId.toString(), 0, 1);
//
//        assertThat(allSells, hasSize(2));
//        assertThat(allSells, contains(sellOrder1, sellOrder2));
//
//        assertThat(oneSell, hasSize(1));
//        assertThat(oneSell, contains(sellOrder1));
//    }

//    @Test
//    public void testFindActiveHintBuyOrders() {
//        final List<OrderEntry> allBuys = repository.findActiveHintBuyOrders(orderBookId.toString(), 0, 10);
//        final List<OrderEntry> oneSell = repository.findActiveHintBuyOrders(orderBookId.toString(), 1, 2);
//
//        assertThat(allBuys, hasSize(2));
//        assertThat(allBuys, contains(buyOrder1, buyOrder2));
//
//        assertThat(oneSell, hasSize(1));
//        assertThat(oneSell, contains(buyOrder2));
//    }

    private OrderEntry createOrderEntry(String userId, OrderType type, BigMoney price, Date placeDate) {
        final OrderEntry orderEntry = new OrderEntry();
        orderEntry.setPortfolioId(userId);
        orderEntry.setOrderBookIdentifier(orderBookId.toString());
        orderEntry.setType(type);
        orderEntry.setItemPrice(price);
        orderEntry.setPlacedDate(placeDate);

        return orderEntry;
    }
}
