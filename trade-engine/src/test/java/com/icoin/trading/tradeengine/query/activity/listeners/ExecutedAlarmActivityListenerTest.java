package com.icoin.trading.tradeengine.query.activity.listeners;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import com.icoin.trading.api.tradeengine.events.trade.TradeExecutedEvent;
import com.icoin.trading.tradeengine.query.activity.ExecutedAlarmActivity;
import com.icoin.trading.tradeengine.query.activity.ExecutedAlarmType;
import com.icoin.trading.tradeengine.query.activity.repositories.ExecutedAlarmActivityQueryRepository;
import com.icoin.trading.tradeengine.query.order.OrderEntry;
import com.icoin.trading.tradeengine.query.order.repositories.OrderQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.tradeengine.query.tradeexecuted.TradeType;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/10/14
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedAlarmActivityListenerTest {
    @Test
    public void testHandleTradeExecutedWithMoneyViolation() throws Exception {
        BigMoney tradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10000);
        BigMoney tradedPrice = BigMoney.zero(CurrencyUnit.CAD);
        BigMoney lowExecutedMoney = BigMoney.of(CurrencyUnit.CAD, 0.00001);
        BigMoney highExecutedMoney = BigMoney.of(CurrencyUnit.CAD, 10000);
        BigMoney buyCommission = BigMoney.zero(CurrencyUnit.CAD);
        BigMoney sellCommission = BigMoney.zero(CurrencyUnit.of("BTC"));
        PortfolioId buyPortfolioId = new PortfolioId();
        PortfolioId sellPortfolioId = new PortfolioId();

        Date tradeTime = currentTime();

        final TradeExecutedEvent event1 = new TradeExecutedEvent(
                new OrderBookId("orderbookId"),
                new CoinId("XPM"),
                tradeAmount,
                tradedPrice,
                lowExecutedMoney,
                "buyOrderId",
                "sellOrderId",
                buyCommission,
                sellCommission,
                new TransactionId("buyTransactionId"),
                new TransactionId("sellTransactionId"),
                buyPortfolioId,
                sellPortfolioId,
                tradeTime,
                com.icoin.trading.api.tradeengine.domain.TradeType.SELL
        );

        final TradeExecutedEvent event2 = new TradeExecutedEvent(
                new OrderBookId("orderbookId"),
                new CoinId("XPM"),
                tradeAmount,
                tradedPrice,
                highExecutedMoney,
                "buyOrderId",
                "sellOrderId",
                buyCommission,
                sellCommission,
                new TransactionId("buyTransactionId"),
                new TransactionId("sellTransactionId"),
                buyPortfolioId,
                sellPortfolioId,
                tradeTime,
                com.icoin.trading.api.tradeengine.domain.TradeType.SELL
        );

        ExecutedAlarmActivityQueryRepository repository = mock(ExecutedAlarmActivityQueryRepository.class);
        PortfolioQueryRepository portfolioRepository = mock(PortfolioQueryRepository.class);

        ExecutedAlarmActivityListener listener = new ExecutedAlarmActivityListener();
        listener.setHighestMoneyThreshold(BigDecimal.valueOf(10000));
        listener.setLowestMoneyThreshold(BigDecimal.valueOf(0.1));
        listener.setHighestAmountThreshold(BigDecimal.TEN);
        listener.setLowestAmountThreshold(BigDecimal.ONE);
        listener.setHighestPriceThreshold(BigDecimal.TEN);
        listener.setLowestPriceThreshold(BigDecimal.ONE);

        listener.setExecutedAlarmActivityRepository(repository);
        listener.setPortfolioRepository(portfolioRepository);

        listener.handleTradeExecuted(event1);
        listener.handleTradeExecuted(event2);

        ArgumentCaptor<ExecutedAlarmActivity> captor = ArgumentCaptor.forClass(ExecutedAlarmActivity.class);
        verify(repository, times(2)).save(captor.capture());

        List<ExecutedAlarmActivity> activities = captor.getAllValues();

        //assert exceeded low 
        ExecutedAlarmActivity low = activities.get(0);
        assertThat(low.getBuyOrderId(), equalTo("buyOrderId"));
        assertThat(low.getSellOrderId(), equalTo("sellOrderId"));
        assertThat(low.getOrderBookIdentifier(), equalTo("orderbookId"));
        assertThat(low.getCoinId(), equalTo("XPM"));
        assertThat(low.getBuyUsername(), nullValue());
        assertThat(low.getSellUsername(), nullValue());
        assertThat(low.getBuyTransactionId(), equalTo("buyTransactionId"));
        assertThat(low.getSellTransactionId(), equalTo("sellTransactionId"));
        assertThat(low.getBuyPortfolioId(), nullValue());
        assertThat(low.getSellPortfolioId(), nullValue());

        assertThat(low.getExecutedMoney().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(low.getExecutedMoney().getAmount(), closeTo(BigDecimal.valueOf(0.00001), BigDecimal.valueOf(0.000000001)));

        assertThat(low.getTradedPrice().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(low.getTradedPrice().getAmount(), closeTo(BigDecimal.ZERO, BigDecimal.valueOf(0.000000001)));

        assertThat(low.getTradedAmount().getCurrencyUnit(), equalTo(CurrencyUnit.of("BTC")));
        assertThat(low.getTradedAmount().getAmount(), closeTo(BigDecimal.valueOf(10000), BigDecimal.valueOf(0.000000001)));

        assertThat(low.isChecked(), is(false));
        assertThat(low.getType(), is(ExecutedAlarmType.MONEY));
        assertThat(low.getTradeType(), is(TradeType.SELL));

        //assert exceeded high 
        ExecutedAlarmActivity high = activities.get(1);
        assertThat(high.getBuyOrderId(), equalTo("buyOrderId"));
        assertThat(high.getSellOrderId(), equalTo("sellOrderId"));
        assertThat(high.getOrderBookIdentifier(), equalTo("orderbookId"));
        assertThat(high.getCoinId(), equalTo("XPM"));
        assertThat(high.getBuyUsername(), nullValue());
        assertThat(high.getSellUsername(), nullValue());
        assertThat(high.getBuyTransactionId(), equalTo("buyTransactionId"));
        assertThat(high.getSellTransactionId(), equalTo("sellTransactionId"));
        assertThat(high.getBuyPortfolioId(), nullValue());
        assertThat(high.getSellPortfolioId(), nullValue());

        assertThat(high.getExecutedMoney().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(high.getExecutedMoney().getAmount(), closeTo(BigDecimal.valueOf(10000), BigDecimal.valueOf(0.000000001)));

        assertThat(high.getTradedPrice().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(high.getTradedPrice().getAmount(), closeTo(BigDecimal.ZERO, BigDecimal.valueOf(0.000000001)));

        assertThat(high.getTradedAmount().getCurrencyUnit(), equalTo(CurrencyUnit.of("BTC")));
        assertThat(high.getTradedAmount().getAmount(), closeTo(BigDecimal.valueOf(10000), BigDecimal.valueOf(0.000000001)));

        assertThat(high.isChecked(), is(false));
        assertThat(high.getType(), is(ExecutedAlarmType.MONEY));
        assertThat(high.getTradeType(), is(TradeType.SELL));
    }

    @Test
    public void testHandleTradeExecutedWithAmountViolation() throws Exception {
        BigMoney lowTradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), 0.1);
        BigMoney highTradeAmount = BigMoney.of(CurrencyUnit.of("BTC"), 10000);
        BigMoney tradedPrice = BigMoney.zero(CurrencyUnit.CAD);
        BigMoney executedMoney = BigMoney.of(CurrencyUnit.CAD, 1000);
        BigMoney buyCommission = BigMoney.zero(CurrencyUnit.CAD);
        BigMoney sellCommission = BigMoney.zero(CurrencyUnit.CAD);
        PortfolioId buyPortfolioId = new PortfolioId();
        PortfolioId sellPortfolioId = new PortfolioId();

        Date tradeTime = currentTime();

        final TradeExecutedEvent event1 = new TradeExecutedEvent(
                new OrderBookId("orderbookId"),
                new CoinId("XPM"),
                lowTradeAmount,
                tradedPrice,
                executedMoney,
                "buyOrderId",
                "sellOrderId",
                buyCommission,
                sellCommission,
                new TransactionId("buyTransactionId"),
                new TransactionId("sellTransactionId"),
                buyPortfolioId,
                sellPortfolioId,
                tradeTime,
                com.icoin.trading.api.tradeengine.domain.TradeType.BUY
        );

        final TradeExecutedEvent event2 = new TradeExecutedEvent(
                new OrderBookId("orderbookId"),
                new CoinId("XPM"),
                highTradeAmount,
                tradedPrice,
                executedMoney,
                "buyOrderId",
                "sellOrderId",
                buyCommission,
                sellCommission,
                new TransactionId("buyTransactionId"),
                new TransactionId("sellTransactionId"),
                buyPortfolioId,
                sellPortfolioId,
                tradeTime,
                com.icoin.trading.api.tradeengine.domain.TradeType.BUY
        );

        ExecutedAlarmActivityQueryRepository repository = mock(ExecutedAlarmActivityQueryRepository.class);
        PortfolioQueryRepository portfolioRepository = mock(PortfolioQueryRepository.class);

        //order 
        OrderEntry buyOrder = new OrderEntry();
        buyOrder.setPrimaryKey("buyOrderId");
        buyOrder.setPortfolioId("buyPortfolioId");

        OrderEntry sellOrder = new OrderEntry();
        sellOrder.setPrimaryKey("sellOrderId");
        sellOrder.setPortfolioId("sellPortfolioId");

        //portfolio 
        PortfolioEntry buyPortfolio = new PortfolioEntry();
        buyPortfolio.setPrimaryKey("buyPortfolioId");
        buyPortfolio.setUsername("buyUsername");
        buyPortfolio.setUserIdentifier("buyUserId");
        PortfolioEntry sellPortfolio = new PortfolioEntry();
        sellPortfolio.setPrimaryKey("sellPortfolioId");
        sellPortfolio.setUsername("sellUsername");
        sellPortfolio.setUserIdentifier("sellUserId");
        when(portfolioRepository.findOne(eq("buyPortfolioId"))).thenReturn(buyPortfolio);
        when(portfolioRepository.findOne(eq("sellPortfolioId"))).thenReturn(sellPortfolio);

        ExecutedAlarmActivityListener listener = new ExecutedAlarmActivityListener();
        listener.setHighestMoneyThreshold(BigDecimal.valueOf(10000));
        listener.setLowestMoneyThreshold(BigDecimal.valueOf(0.1));
        listener.setHighestAmountThreshold(BigDecimal.valueOf(9000));
        listener.setLowestAmountThreshold(BigDecimal.ONE);
        listener.setHighestPriceThreshold(BigDecimal.TEN);
        listener.setLowestPriceThreshold(BigDecimal.ONE);
        listener.setExecutedAlarmActivityRepository(repository);
        listener.setPortfolioRepository(portfolioRepository);

        listener.handleTradeExecuted(event1);
        listener.handleTradeExecuted(event2);

        ArgumentCaptor<ExecutedAlarmActivity> captor = ArgumentCaptor.forClass(ExecutedAlarmActivity.class);
        verify(repository, times(2)).save(captor.capture());

        List<ExecutedAlarmActivity> activities = captor.getAllValues();

        //assert exceeded low 
        ExecutedAlarmActivity low = activities.get(0);
        assertThat(low.getBuyOrderId(), equalTo("buyOrderId"));
        assertThat(low.getSellOrderId(), equalTo("sellOrderId"));
        assertThat(low.getOrderBookIdentifier(), equalTo("orderbookId"));
        assertThat(low.getCoinId(), equalTo("XPM"));
        assertThat(low.getBuyUsername(), equalTo("buyUsername"));
        assertThat(low.getSellUsername(), equalTo("sellUsername"));
        assertThat(low.getBuyTransactionId(), equalTo("buyTransactionId"));
        assertThat(low.getSellTransactionId(), equalTo("sellTransactionId"));
        assertThat(low.getBuyPortfolioId(), equalTo("buyPortfolioId"));
        assertThat(low.getSellPortfolioId(), equalTo("sellPortfolioId"));

        assertThat(low.getExecutedMoney().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(low.getExecutedMoney().getAmount(), closeTo(BigDecimal.valueOf(1000), BigDecimal.valueOf(0.000000001)));

        assertThat(low.getTradedPrice().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(low.getTradedPrice().getAmount(), closeTo(BigDecimal.ZERO, BigDecimal.valueOf(0.000000001)));

        assertThat(low.getTradedAmount().getCurrencyUnit(), equalTo(CurrencyUnit.of("BTC")));
        assertThat(low.getTradedAmount().getAmount(), closeTo(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.000000001)));

        assertThat(low.isChecked(), is(false));
        assertThat(low.getType(), is(ExecutedAlarmType.AMOUNT));
        assertThat(low.getTradeType(), is(TradeType.BUY));

        //assert exceeded high 
        ExecutedAlarmActivity high = activities.get(1);
        assertThat(high.getBuyOrderId(), equalTo("buyOrderId"));
        assertThat(high.getSellOrderId(), equalTo("sellOrderId"));
        assertThat(high.getOrderBookIdentifier(), equalTo("orderbookId"));
        assertThat(high.getCoinId(), equalTo("XPM"));
        assertThat(high.getBuyUsername(), equalTo("buyUsername"));
        assertThat(high.getSellUsername(), equalTo("sellUsername"));
        assertThat(high.getBuyTransactionId(), equalTo("buyTransactionId"));
        assertThat(high.getSellTransactionId(), equalTo("sellTransactionId"));
        assertThat(high.getBuyPortfolioId(), equalTo("buyPortfolioId"));
        assertThat(high.getSellPortfolioId(), equalTo("sellPortfolioId"));

        assertThat(high.getExecutedMoney().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(high.getExecutedMoney().getAmount(), closeTo(BigDecimal.valueOf(1000), BigDecimal.valueOf(0.000000001)));

        assertThat(high.getTradedPrice().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(high.getTradedPrice().getAmount(), closeTo(BigDecimal.ZERO, BigDecimal.valueOf(0.000000001)));

        assertThat(high.getTradedAmount().getCurrencyUnit(), equalTo(CurrencyUnit.of("BTC")));
        assertThat(high.getTradedAmount().getAmount(), closeTo(BigDecimal.valueOf(10000), BigDecimal.valueOf(0.000000001)));

        assertThat(high.isChecked(), is(false));
        assertThat(high.getType(), is(ExecutedAlarmType.AMOUNT));
        assertThat(high.getTradeType(), is(TradeType.BUY));

        verify(portfolioRepository, times(2)).findOne(eq("buyPortfolioId"));
        verify(portfolioRepository, times(2)).findOne(eq("sellPortfolioId"));
    }

    @Test
    public void testHandleTradeExecutedWithPriceViolation() throws Exception {
        BigMoney tradeAmount = BigMoney.of(CurrencyUnit.CAD, 200);
        BigMoney tradedPrice = BigMoney.of(CurrencyUnit.CAD, 100);
        BigMoney lowTradedPrice = BigMoney.of(CurrencyUnit.CAD, 1);
        BigMoney highTradedPrice = BigMoney.of(CurrencyUnit.CAD, 232487);
        BigMoney executedMoney = BigMoney.of(CurrencyUnit.CAD, 100);
        BigMoney buyCommission = BigMoney.zero(CurrencyUnit.CAD);
        BigMoney sellCommission = BigMoney.zero(CurrencyUnit.CAD);
        PortfolioId buyPortfolioId = new PortfolioId();
        PortfolioId sellPortfolioId = new PortfolioId();

        Date tradeTime = currentTime();

        final TradeExecutedEvent event1 = new TradeExecutedEvent(
                new OrderBookId("orderbookId"),
                new CoinId("XPM"),
                tradeAmount,
                lowTradedPrice,
                executedMoney,
                "buyOrderId",
                "sellOrderId",
                buyCommission,
                sellCommission,
                new TransactionId("buyTransactionId"),
                new TransactionId("sellTransactionId"),
                buyPortfolioId,
                sellPortfolioId,
                tradeTime,
                com.icoin.trading.api.tradeengine.domain.TradeType.BUY
        );

        final TradeExecutedEvent event2 = new TradeExecutedEvent(
                new OrderBookId("orderbookId"),
                new CoinId("XPM"),
                tradeAmount,
                highTradedPrice,
                executedMoney,
                "buyOrderId",
                "sellOrderId",
                buyCommission,
                sellCommission,
                new TransactionId("buyTransactionId"),
                new TransactionId("sellTransactionId"),
                buyPortfolioId,
                sellPortfolioId,
                tradeTime,
                com.icoin.trading.api.tradeengine.domain.TradeType.BUY
        );

        final TradeExecutedEvent event3 = new TradeExecutedEvent(
                new OrderBookId("orderbookId"),
                new CoinId("XPM"),
                tradeAmount,
                tradedPrice,
                executedMoney,
                "buyOrderId",
                "sellOrderId",
                buyCommission,
                sellCommission,
                new TransactionId("buyTransactionId"),
                new TransactionId("sellTransactionId"),
                buyPortfolioId,
                sellPortfolioId,
                tradeTime,
                com.icoin.trading.api.tradeengine.domain.TradeType.BUY
        );

        ExecutedAlarmActivityQueryRepository repository = mock(ExecutedAlarmActivityQueryRepository.class);
        PortfolioQueryRepository portfolioRepository = mock(PortfolioQueryRepository.class);

        //order 
        OrderEntry buyOrder = new OrderEntry();
        buyOrder.setPrimaryKey("buyOrderId");
        buyOrder.setPortfolioId("buyPortfolioId");

        OrderEntry sellOrder = new OrderEntry();
        sellOrder.setPrimaryKey("sellOrderId");
        sellOrder.setPortfolioId("sellPortfolioId");

//        when(portfolioRepository.findOne(eq("sellPortfolioId"))).thenReturn();

        ExecutedAlarmActivityListener listener = new ExecutedAlarmActivityListener();
        listener.setHighestMoneyThreshold(BigDecimal.valueOf(10000));
        listener.setLowestMoneyThreshold(BigDecimal.valueOf(0.1));
        listener.setHighestAmountThreshold(BigDecimal.valueOf(90000));
        listener.setLowestAmountThreshold(BigDecimal.ONE);
        listener.setHighestPriceThreshold(BigDecimal.valueOf(10000));
        listener.setLowestPriceThreshold(BigDecimal.ONE);
        listener.setExecutedAlarmActivityRepository(repository);
        listener.setPortfolioRepository(portfolioRepository);

        listener.handleTradeExecuted(event1);
        listener.handleTradeExecuted(event2);
        listener.handleTradeExecuted(event3);

        ArgumentCaptor<ExecutedAlarmActivity> captor = ArgumentCaptor.forClass(ExecutedAlarmActivity.class);
        verify(repository, times(2)).save(captor.capture());

        List<ExecutedAlarmActivity> activities = captor.getAllValues();

        //assert exceeded low 
        ExecutedAlarmActivity low = activities.get(0);
        assertThat(low.getBuyOrderId(), equalTo("buyOrderId"));
        assertThat(low.getSellOrderId(), equalTo("sellOrderId"));
        assertThat(low.getOrderBookIdentifier(), equalTo("orderbookId"));
        assertThat(low.getCoinId(), equalTo("XPM"));
        assertThat(low.getBuyUsername(), nullValue());
        assertThat(low.getSellUsername(), nullValue());
        assertThat(low.getBuyTransactionId(), equalTo("buyTransactionId"));
        assertThat(low.getSellTransactionId(), equalTo("sellTransactionId"));
        assertThat(low.getBuyPortfolioId(), nullValue());
        assertThat(low.getSellPortfolioId(), nullValue());

        assertThat(low.getExecutedMoney().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(low.getExecutedMoney().getAmount(), closeTo(BigDecimal.valueOf(100), BigDecimal.valueOf(0.000000001)));

        assertThat(low.getTradedPrice().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(low.getTradedPrice().getAmount(), closeTo(BigDecimal.valueOf(1), BigDecimal.valueOf(0.000000001)));

        assertThat(low.getTradedAmount().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(low.getTradedAmount().getAmount(), closeTo(BigDecimal.valueOf(200), BigDecimal.valueOf(0.000000001)));

        assertThat(low.isChecked(), is(false));
        assertThat(low.getType(), is(ExecutedAlarmType.PRICE));
        assertThat(low.getTradeType(), is(TradeType.BUY));

        //assert exceeded high 
        ExecutedAlarmActivity high = activities.get(1);
        assertThat(high.getBuyOrderId(), equalTo("buyOrderId"));
        assertThat(high.getSellOrderId(), equalTo("sellOrderId"));
        assertThat(high.getOrderBookIdentifier(), equalTo("orderbookId"));
        assertThat(high.getCoinId(), equalTo("XPM"));
        assertThat(high.getBuyUsername(), nullValue());
        assertThat(high.getSellUsername(), nullValue());
        assertThat(high.getBuyTransactionId(), equalTo("buyTransactionId"));
        assertThat(high.getSellTransactionId(), equalTo("sellTransactionId"));
        assertThat(high.getBuyPortfolioId(), nullValue());
        assertThat(high.getSellPortfolioId(), nullValue());

        assertThat(high.getExecutedMoney().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(high.getExecutedMoney().getAmount(), closeTo(BigDecimal.valueOf(100), BigDecimal.valueOf(0.000000001)));

        assertThat(high.getTradedPrice().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(high.getTradedPrice().getAmount(), closeTo(BigDecimal.valueOf(232487), BigDecimal.valueOf(0.000000001)));

        assertThat(high.getTradedAmount().getCurrencyUnit(), equalTo(CurrencyUnit.CAD));
        assertThat(high.getTradedAmount().getAmount(), closeTo(BigDecimal.valueOf(200), BigDecimal.valueOf(0.000000001)));

        assertThat(high.isChecked(), is(false));
        assertThat(high.getType(), is(ExecutedAlarmType.PRICE));
        assertThat(high.getTradeType(), is(TradeType.BUY));

        verify(portfolioRepository, times(2)).findOne(eq("buyPortfolioId"));
        verify(portfolioRepository, times(2)).findOne(eq("sellPortfolioId"));
    }
} 