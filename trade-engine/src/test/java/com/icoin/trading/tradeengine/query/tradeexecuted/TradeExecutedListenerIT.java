package com.icoin.trading.tradeengine.query.tradeexecuted;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.api.tradeengine.events.coin.CoinCreatedEvent;
import com.icoin.trading.api.tradeengine.events.coin.OrderBookAddedToCoinEvent;
import com.icoin.trading.api.tradeengine.events.trade.TradeExecutedEvent;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.Currencies;
import com.icoin.trading.api.coin.domain.CurrencyPair;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.OrderId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.CoinListener;
import com.icoin.trading.tradeengine.query.coin.repositories.CoinQueryRepository;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.OrderBookListener;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.repositories.TradeExecutedQueryRepository;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-5
 * Time: AM1:19
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration({"classpath:com/icoin/trading/tradeengine/infrastructure/persistence/mongo/tradeengine-persistence-mongo.xml"})
@SuppressWarnings("SpringJavaAutowiringInspection")
public class TradeExecutedListenerIT {

    private TradeExecutedListener tradeExecutedListener;

    @Autowired
    private TradeExecutedQueryRepository tradeExecutedRepository;

    @Autowired
    private OrderBookQueryRepository orderBookRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CoinQueryRepository coinRepository;

    private CoinId coinId = new CoinId("XPM");
    private OrderId orderId = new OrderId();
    private PortfolioId sellPortfolioId = new PortfolioId();
    private PortfolioId buyPortfolioId = new PortfolioId();
    private TransactionId transactionId = new TransactionId();
    private OrderBookId orderBookId = new OrderBookId();

    @Before
    public void setUp() throws Exception {
        mongoTemplate.dropCollection(OrderBookEntry.class);
        mongoTemplate.dropCollection(CoinEntry.class);
        mongoTemplate.dropCollection(TradeExecutedEntry.class);


        CoinListener coinListener = new CoinListener();
        coinListener.setCoinRepository(coinRepository);
        coinListener.handleCoinCreatedEvent(
                new CoinCreatedEvent(
                        coinId,
                        "Test Coin",
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(100)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(100))));

        tradeExecutedListener = new TradeExecutedListener();
        tradeExecutedListener.setTradeExecutedRepository(tradeExecutedRepository);

        OrderBookListener orderBookListener = new OrderBookListener();
        orderBookListener.setCoinRepository(coinRepository);
        orderBookListener.setOrderBookRepository(orderBookRepository);

        orderBookListener.handleOrderBookAddedToCoinEvent(new OrderBookAddedToCoinEvent(coinId, orderBookId, CurrencyPair.XPM_CNY));
    }

    @Test
    public void testHandleTradeExecuted() throws Exception {
        OrderId sellOrderId = new OrderId();
        OrderId buyOrderId = new OrderId();
        TransactionId sellTransactionId = new TransactionId();
        TransactionId buyTransactionId = new TransactionId();
        final Date tradeTime = currentTime();

        TradeExecutedEvent event =
                new TradeExecutedEvent(
                        orderBookId,
                        coinId,
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(300)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(125)),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(37500)),
                        buyOrderId.toString(),
                        sellOrderId.toString(),
                        BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, BigDecimal.valueOf(125)),
                        BigMoney.of(CurrencyUnit.of(Currencies.BTC), BigDecimal.valueOf(300)),
                        buyTransactionId,
                        sellTransactionId,
                        buyPortfolioId,
                        sellPortfolioId,
                        tradeTime,
                        TradeType.BUY);
        tradeExecutedListener.handleTradeExecuted(event);

        Iterable<TradeExecutedEntry> tradeExecutedEntries = tradeExecutedRepository.findAll();
        assertTrue(tradeExecutedEntries.iterator().hasNext());

        TradeExecutedEntry tradeExecutedEntry = tradeExecutedEntries.iterator().next();

        assertThat(tradeExecutedEntry.getOrderBookIdentifier(), equalTo(orderBookId.toString()));
        assertThat(tradeExecutedEntry.getCoinId(), equalTo("XPM"));
        assertThat(tradeExecutedEntry.getTradeTime(), equalTo(tradeTime));
        assertThat(tradeExecutedEntry.getBuyPortfolioId(), equalTo(buyPortfolioId.toString()));
        assertThat(tradeExecutedEntry.getSellPortfolioId(), equalTo(sellPortfolioId.toString()));
        assertThat(tradeExecutedEntry.getTradeType(), equalTo(com.icoin.trading.tradeengine.query.tradeexecuted.TradeType.BUY));


        assertThat(tradeExecutedEntry
                .getTradedAmount()
                .isEqual(BigMoney.of(
                        CurrencyUnit.of(Currencies.BTC),
                        BigDecimal.valueOf(300))),
                is(true));
        assertThat(tradeExecutedEntry
                .getTradedPrice()
                .isEqual(BigMoney.of(
                        CurrencyUnit.of(Currencies.CNY),
                        BigDecimal.valueOf(125))),
                is(true));
    }
}
