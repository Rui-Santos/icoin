package com.icoin.trading.tradeengine.infrastructure.persistence.mongo;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.SellOrder;
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
import static com.homhon.util.TimeUtils.currentTime;
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
 * Date: 13-12-6
 * Time: PM10:44
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:com/icoin/trading/tradeengine/infrastructure/persistence/mongo/tradeengine-persistence-mongo.xml"})
@SuppressWarnings("SpringJavaAutowiringInspection")
public class SellOrderRepositoryMongoIT {
    private final LocalDateTime placeDate = currentLocalTime();
    private OrderBookId orderBookId = new OrderBookId(), anotherOrderBookId = new OrderBookId();
    private SellOrder sellOrder1, sellOrder2, sellOrder3, anotherOrderBookSellOrder;

    @Autowired
    private SellOrderRepositoryMongo sellOrderRepository;

    @Before
    public void setUp() throws Exception {
        sellOrderRepository.deleteAll();

        sellOrder1 = new SellOrder();
        sellOrder1.setOrderBookId(orderBookId);
        sellOrder1.setItemRemaining(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)));
        sellOrder1.setTradeAmount(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(90.9)));
        sellOrder1.setItemPrice(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(1)));
        sellOrder1.setPlaceDate(placeDate.toDate());

        sellOrder2 = new SellOrder();
        sellOrder2.setOrderBookId(orderBookId);
        sellOrder2.setItemRemaining(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)));
        sellOrder2.setTradeAmount(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)));
        sellOrder2.setItemPrice(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(10.01)));
        sellOrder2.setPlaceDate(placeDate.plusMillis(2).toDate());

        sellOrder3 = new SellOrder();
        sellOrder3.setOrderBookId(orderBookId);
        sellOrder3.setItemRemaining(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)));
        sellOrder3.setTradeAmount(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(10)));
        sellOrder3.setItemPrice(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(8)));
        sellOrder3.setPlaceDate(placeDate.plusDays(2).toDate());

        anotherOrderBookSellOrder = new SellOrder();
        anotherOrderBookSellOrder.setOrderBookId(anotherOrderBookId);
        anotherOrderBookSellOrder.setItemRemaining(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100)));
        anotherOrderBookSellOrder.setTradeAmount(BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(1000)));
        anotherOrderBookSellOrder.setItemPrice(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(5)));
        anotherOrderBookSellOrder.setPlaceDate(placeDate.toDate());

        assertThat("Another order book id should not be equal to order book id to prepare the data"
                , anotherOrderBookId, not(equalTo(orderBookId)));

        sellOrderRepository.save(ImmutableList.of(sellOrder1, sellOrder2, sellOrder3, anotherOrderBookSellOrder));
    }

    @Test
    public void testFindAscPendingOrdersByPriceTime() throws Exception {
        List<SellOrder> sellOrderList = sellOrderRepository.findAscPendingOrdersByPriceTime(
                placeDate.minusDays(1).toDate(),
                sellOrder1.getItemPrice(),
                orderBookId,
                1);

        assertThat(sellOrderList, anyOf(nullValue(), empty()));

        //
        sellOrderList = sellOrderRepository.findAscPendingOrdersByPriceTime(
                placeDate.toDate(),
                sellOrder1.getItemPrice(),
                orderBookId,
                10);

        assertThat(sellOrderList, hasSize(1));
        SellOrder sellOrder = sellOrderList.get(0);
        assertThat(sellOrder, equalTo(sellOrder1));

//        db.sellOrder.find({ "orderBookId" : "58bc676a-a99c-4d15-a323-7b0429b7808f", "itemPrice" : { "$lt" : 10.01000000010}, "placeDate" : { "$lte" : ISODate("2013-12-06T17:38:48.584Z")}, "orderStatus" :'PENDING', "orderType": 'SELL' }

        sellOrderList = sellOrderRepository.findAscPendingOrdersByPriceTime(
                placeDate.plusDays(1).toDate(),
                sellOrder2.getItemPrice(),
                orderBookId,
                10);

        assertThat(sellOrderList, hasSize(2));
        assertThat(sellOrderList, contains(sellOrder1, sellOrder2));

        sellOrderList = sellOrderRepository.findAscPendingOrdersByPriceTime(
                placeDate.plusDays(2).toDate(),
                sellOrder2.getItemPrice(),
                orderBookId,
                10);

        assertThat(sellOrderList, hasSize(3));
        assertThat(sellOrderList, contains(sellOrder1, sellOrder3, sellOrder2));

        sellOrderList = sellOrderRepository.findAscPendingOrdersByPriceTime(
                placeDate.plusDays(3).toDate(),
                sellOrder2.getItemPrice(),
                orderBookId,
                2);

        assertThat(sellOrderList, hasSize(2));
        assertThat(sellOrderList, contains(sellOrder1, sellOrder3));

        sellOrderList = sellOrderRepository.findAscPendingOrdersByPriceTime(
                placeDate.plusDays(3).toDate(),
                sellOrder1.getItemPrice().plus(BigDecimal.valueOf(0.01)),
                orderBookId,
                2);

        assertThat(sellOrderList, hasSize(1));
        assertThat(sellOrderList, contains(sellOrder1));

        sellOrderList = sellOrderRepository.findAscPendingOrdersByPriceTime(
                placeDate.minusDays(1).toDate(),
                sellOrder1.getItemPrice().plus(BigDecimal.valueOf(0.01)),
                orderBookId,
                2);

        assertThat(sellOrderList, anyOf(nullValue(), empty()));
    }

    @Test
    public void testFindPending() throws Exception {
        SellOrder foundPendingOne = sellOrderRepository.findPendingOrder(sellOrder2.getPrimaryKey());
        SellOrder foundOne = sellOrderRepository.findOne(sellOrder2.getPrimaryKey());

        assertThat(foundPendingOne, equalTo(foundOne));

        sellOrder2.recordTraded(sellOrder2.getItemRemaining(), currentTime());
        sellOrderRepository.save(sellOrder2);
        SellOrder notPending = sellOrderRepository.findPendingOrder(sellOrder2.getPrimaryKey());
        assertThat(notPending, nullValue());

        foundOne = sellOrderRepository.findOne(sellOrder2.getPrimaryKey());
        assertThat(foundOne, equalTo(sellOrder2));
    }

    @Test
    public void testFindLowest() throws Exception {
        SellOrder lowestOrder = sellOrderRepository.findLowestPricePendingOrder(orderBookId);

        assertThat(lowestOrder, equalTo(sellOrder1));
    }
}
