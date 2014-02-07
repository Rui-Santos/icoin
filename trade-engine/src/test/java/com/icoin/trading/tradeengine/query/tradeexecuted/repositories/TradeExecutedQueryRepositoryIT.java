package com.icoin.trading.tradeengine.query.tradeexecuted.repositories;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.query.tradeexecuted.OpenHighLowCloseVolume;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeExecutedEntry;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeType;
import org.joda.money.BigMoney;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-5
 * Time: PM10:17
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:com/icoin/trading/tradeengine/infrastructure/persistence/mongo/tradeengine-persistence-mongo.xml"})
public class TradeExecutedQueryRepositoryIT {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private TradeExecutedQueryRepository repository;

    private final LocalDateTime date = LocalDateTime.parse("2012-12-12T12:12:12.12");
    final String orderBookIdentifier = new OrderBookId().toString();

    @Before
    public void setUp() throws Exception {
        final TradeExecutedEntry year1 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusYears(1).minusDays(12).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry year2 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusYears(2).minusDays(30).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry year3 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusYears(2).minusDays(20).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry month1 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusMonths(3).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry month2 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusMonths(5).minusDays(3).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry month3 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusMonths(5).minusDays(1).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry day1 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusDays(1).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry day2 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusDays(1).minusHours(2).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry day3 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusDays(2).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry day4 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusDays(2).minusHours(3).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry hour1 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusHours(1).minusMinutes(1).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry hour2 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusHours(1).minusMinutes(2).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry hour3 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusHours(2).minusMinutes(2).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        final TradeExecutedEntry hour4 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusHours(2).minusMinutes(4).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.Buy);

        repository.save(Arrays.asList(year1, year2, year3, month1, month2, month3,
                day1, day2, day3, day4, hour1, hour2, hour3, hour4));
    }

    private TradeExecutedEntry createTradeExecutedEntry(String orderBookIdentifier, Date tradeTime, BigMoney tradedAmount, BigMoney tradedPrice, TradeType tradeType) {
        final TradeExecutedEntry year1 = new TradeExecutedEntry();
        year1.setTradeType(tradeType);
        year1.setTradeTime(tradeTime);
        year1.setOrderBookIdentifier(orderBookIdentifier);
        year1.setTradedAmount(tradedAmount);
        year1.setTradedPrice(tradedPrice);
        return year1;
    }

    @Test
    public void testOhlc() throws Exception {
        final List<OpenHighLowCloseVolume> all = repository.ohlc(orderBookIdentifier,
                date.minusYears(5).toDate(),
                date.minusSeconds(8).toDate(),
                new PageRequest(0, 100));

//        assertThat();
    }
}
