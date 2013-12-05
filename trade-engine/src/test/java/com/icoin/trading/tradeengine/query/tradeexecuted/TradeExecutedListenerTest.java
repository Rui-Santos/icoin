package com.icoin.trading.tradeengine.query.tradeexecuted;

import com.icoin.trading.tradeengine.domain.events.coin.CoinCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.order.OrderId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.coin.CoinEntry;
import com.icoin.trading.tradeengine.query.coin.CoinListener;
import com.icoin.trading.tradeengine.query.order.OrderBookEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderBookQueryRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.repositories.TradeExecutedQueryRepository;
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

import static com.homhon.mongo.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
public class TradeExecutedListenerTest {

    private TradeExecutedListener tradeExecutedListener;

    @Autowired
    private TradeExecutedQueryRepository tradeExecutedRepository;

    @Autowired
    private OrderBookQueryRepository orderBookRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    OrderId orderId = new OrderId();
    PortfolioId portfolioId = new PortfolioId();
    TransactionId transactionId = new TransactionId();
    OrderBookId orderBookId = new OrderBookId();
    CoinId coinId = new CoinId();

    @Before
    public void setUp() throws Exception {
        mongoTemplate.dropCollection(OrderBookEntry.class);
        mongoTemplate.dropCollection(CoinEntry.class);
        mongoTemplate.dropCollection(TradeExecutedEntry.class);

        CoinListener coinListener = new CoinListener();
        coinListener.handleCoinCreatedEvent(
                new CoinCreatedEvent(coinId, "Test Coin", BigDecimal.valueOf(100), BigDecimal.valueOf(100)));

        tradeExecutedListener = new TradeExecutedListener();
        tradeExecutedListener.setOrderBookRepository(orderBookRepository);
    }

    @Test
    public void testHandleTradeExecuted() throws Exception {
        OrderId sellOrderId = new OrderId();
        OrderId buyOrderId = new OrderId();
        TransactionId sellTransactionId = new TransactionId();
        TransactionId buyTransactionId = new TransactionId();
        final Date tradeTime = currentTime();

        TradeExecutedEvent event = new TradeExecutedEvent(orderBookId,
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(125),
                buyOrderId.toString(),//todo change,
                sellOrderId.toString(),//todo change,
                buyTransactionId,
                sellTransactionId,
                tradeTime);
        tradeExecutedListener.handleTradeExecuted(event);

        Iterable<TradeExecutedEntry> tradeExecutedEntries = tradeExecutedRepository.findAll();
        assertTrue(tradeExecutedEntries.iterator().hasNext());

        TradeExecutedEntry tradeExecutedEntry = tradeExecutedEntries.iterator().next();

        assertThat(tradeExecutedEntry.getOrderBookIdentifier(), equalTo(orderBookId.toString()));
        assertThat(tradeExecutedEntry.getCoinName(), equalTo("Test Coin"));
        assertThat(tradeExecutedEntry.getTradeAmount(), equalTo(BigDecimal.valueOf(300)));
        assertThat(tradeExecutedEntry.getTradePrice(), equalTo(BigDecimal.valueOf(125)));
        assertThat(tradeExecutedEntry.getTradeTime(), equalTo(tradeTime));
        assertThat(tradeExecutedEntry.getTradePrice(), equalTo(BigDecimal.valueOf(125)));
    }
}
