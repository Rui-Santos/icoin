package com.icoin.trading.tradeengine.query.tradeexecuted.repositories;

import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.tradeengine.Constants;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private MongoTemplate mongoTemplate;

    private final LocalDateTime date = LocalDateTime.parse("2012-12-12T12:12:12.12");
    final String orderBookIdentifier = new OrderBookId().toString();

    @Before
    public void setUp() throws Exception {
        final TradeExecutedEntry year1 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusYears(1).minusDays(12).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry year2 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusYears(2).minusDays(30).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry year3 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusYears(2).minusDays(20).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry month1 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusMonths(3).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry month2 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusMonths(5).minusDays(3).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry month3 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusMonths(5).minusDays(1).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry day1 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusDays(1).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry day2 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusDays(1).minusHours(2).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry day3 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusDays(2).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry day4 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusDays(2).minusHours(3).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry hour1 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusHours(1).minusMinutes(1).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry hour2 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusHours(1).minusMinutes(2).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry hour3 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusHours(2).minusMinutes(2).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

        final TradeExecutedEntry hour4 =
                createTradeExecutedEntry(orderBookIdentifier,
                        date.minusHours(2).minusMinutes(4).toDate(),
                        BigMoney.of(Constants.CURRENCY_UNIT_BTC, 10.5),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.5),
                        TradeType.BUY);

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

    //    @Ignore
    @Test
    public void testOhlc() throws Exception {

        System.err.println("-------------------------------");
        final List<OpenHighLowCloseVolume> all = repository.ohlc(orderBookIdentifier,
                date.minusYears(5).toDate(),
                date.minusSeconds(8).toDate(),
                new PageRequest(0, 100));


        System.out.println(all);
//        assertThat();
    }


//    @Test
//    public void testDate() throws Exception {
//        Data data = new Data();
//        data.dateValue = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSZ").parse("29.08.1983 12:34:56.789+0000");
//        mongoTemplate.insert(data);
//
//        TypedAggregation<Data> agg = newAggregation(Data.class, project() //
//                .andExpression("dayOfYear(dateValue)").as("dayOfYear") //
//                .andExpression("dayOfMonth(dateValue)").as("dayOfMonth") //
//                .andExpression("dayOfWeek(dateValue)").as("dayOfWeek") //
//                .andExpression("year(dateValue)").as("year") //
//                .andExpression("month(dateValue)").as("month") //
//                .andExpression("week(dateValue)").as("week") //
//                .andExpression("hour(dateValue)").as("hour") //
//                .andExpression("minute(dateValue)").as("minute") //
//                .andExpression("second(dateValue)").as("second") //
//                .andExpression("millisecond(dateValue)").as("millisecond") //
//                ,
//                group("dayOfYear")
//        );
//
//        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, DBObject.class);
//        DBObject dbo = results.getUniqueMappedResult();
//
//        assertThat(dbo, is(notNullValue()));
//        assertThat((Integer) dbo.get("dayOfYear"), is(241));
//        assertThat((Integer) dbo.get("dayOfMonth"), is(29));
//        assertThat((Integer) dbo.get("dayOfWeek"), is(2));
//        assertThat((Integer) dbo.get("year"), is(1983));
//        assertThat((Integer) dbo.get("month"), is(8));
//        assertThat((Integer) dbo.get("week"), is(35));
//        assertThat((Integer) dbo.get("hour"), is(12));
//        assertThat((Integer) dbo.get("minute"), is(34));
//        assertThat((Integer) dbo.get("second"), is(56));
//        assertThat((Integer) dbo.get("millisecond"), is(789));
//    }

    private static class Data {
        public long primitiveLongValue;
        public double primitiveDoubleValue;
        public Double doubleValue;
        public Date dateValue;
        public String stringValue;

        public DataItem item;
    }

    private static class DataItem {
        int primitiveIntValue;
    }
}
