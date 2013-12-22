package com.icoin.trading.tradeengine.query.order.repositories;

import com.google.common.collect.Lists;
import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderStatus;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.OrderType;
import com.icoin.trading.tradeengine.query.order.PriceAggregate;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-10
 * Time: PM11:48
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:com/icoin/trading/tradeengine/infrastructure/persistence/mongo/tradeengine-persistence-mongo.xml"})
public class OrderAggregateQueryRepositoryIT {
    private OrderBookId orderBookId = new OrderBookId();

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private OrderQueryRepository repository;

    private LocalDate date = LocalDate.now();


    @Before
    public void setUp() throws Exception {
        repository.deleteAll();

        final OrderEntry sellOrder1 = createSellOrder(
                Constants.CURRENCY_UNIT_BTC,
                1027654.4998777,
                1027654.4998777,
                1034.56767,
                date.toDate(),
                OrderStatus.PENDING
        );

        final OrderEntry sellOrder2 = createSellOrder(
                Constants.CURRENCY_UNIT_BTC,
                10.5,
                0,
                1034.56767,
                date.toDate(),
                OrderStatus.DONE
        );


        final OrderEntry sellOrder3 = createSellOrder(
                Constants.CURRENCY_UNIT_BTC,
                10.5,
                1,
                1034.56767,
                date.toDate(),
                OrderStatus.DONE
        );


        final OrderEntry sellOrder4 = createSellOrder(
                Constants.CURRENCY_UNIT_BTC,
                10.5,
                1,
                1000,
                date.toDate(),
                OrderStatus.PENDING
        );

        final OrderEntry sellOrder5 = createSellOrder(
                Constants.CURRENCY_UNIT_BTC,
                15.5,
                1,
                1000,
                date.toDate(),
                OrderStatus.PENDING
        );

        repository.save(Lists.newArrayList(sellOrder1, sellOrder2, sellOrder3, sellOrder4, sellOrder5));

    }

    private OrderEntry createSellOrder(
            CurrencyUnit currencyUnit,
            double total,
            double remaining,
            double price,
            Date placeDae,
            OrderStatus orderStatus) {
        return fillOrder(currencyUnit, total, remaining, price, placeDae, orderStatus, OrderType.SELL, new OrderEntry());
    }

    private OrderEntry createBuyOrder(
            CurrencyUnit currencyUnit,
            double total,
            double remaining,
            double price,
            Date placeDae,
            OrderStatus orderStatus) {
        return fillOrder(currencyUnit, total, remaining, price, placeDae, orderStatus, OrderType.BUY, new OrderEntry());
    }

    private OrderEntry fillOrder(CurrencyUnit currencyUnit,
                                 double totoal,
                                 double remaining,
                                 double price,
                                 Date placeDae,
                                 OrderStatus orderStatus,
                                 OrderType type,
                                 OrderEntry order) {
        order.setOrderBookIdentifier(orderBookId.toString());
        order.setPlacedDate(placeDae);
        order.setType(type);
        order.setOrderStatus(orderStatus);
        order.setTradeAmount(BigMoney.of(currencyUnit, totoal));
        order.setItemRemaining(BigMoney.of(currencyUnit, remaining));
        order.setItemPrice(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, price));

        return order;
    }

    @Test
    public void testFindOrderAggregatedPrice() throws Exception {

        final List<PriceAggregate> aggregateList =
                repository.findOrderAggregatedPrice(
                        orderBookId.toString(),
                        OrderType.SELL,
                        date.toDate(),
                        10);

        assertThat(aggregateList, hasSize(2));
        final PriceAggregate priceAggregate1 = aggregateList.get(0);
        final PriceAggregate priceAggregate2 = aggregateList.get(1);

        assertThat(priceAggregate1.getPrice().isEqual(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 1034.568)), is(true));
        assertThat(priceAggregate1.getSumUpAmountPerPrice().isEqual(BigMoney.of(Constants.CURRENCY_UNIT_BTC, 1027654.4998777)), is(true));
        assertThat(priceAggregate1.getTotal().isEqual(Money.of(Constants.DEFAULT_CURRENCY_UNIT, 1027654.4998777D * 1034.568D, RoundingMode.HALF_EVEN)), is(true));

        assertThat(priceAggregate2.getPrice().isEqual(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 1000)), is(true));
        assertThat(priceAggregate2.getSumUpAmountPerPrice().isEqual(BigMoney.of(Constants.CURRENCY_UNIT_BTC, 2)), is(true));
        assertThat(priceAggregate2.getTotal().isEqual(Money.of(Constants.DEFAULT_CURRENCY_UNIT, 2000, RoundingMode.HALF_EVEN)), is(true));
    }
}
