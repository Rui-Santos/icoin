package com.icoin.trading.tradeengine.query.activity.listeners;

import com.icoin.trading.api.tradeengine.events.portfolio.cash.CashDepositedEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.cash.CashWithdrawnEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.coin.ItemAddedToPortfolioEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.coin.ItemWithdrawnEvent;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.tradeengine.query.activity.PortfolioAlarmActivity;
import com.icoin.trading.tradeengine.query.activity.PortfolioAlarmType;
import com.icoin.trading.tradeengine.query.activity.repositories.PortfolioAlarmQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/10/14
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class PortfolioAlarmListenerTest {
    @Test
    public void testHandleAddCoin() throws Exception {
        Date time = currentTime();

        PortfolioAlarmQueryRepository repository = mock(PortfolioAlarmQueryRepository.class);
        PortfolioQueryRepository portfolioRepository = mock(PortfolioQueryRepository.class);

        PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setPrimaryKey("portfolioId");
        portfolio.setUsername("username");
        portfolio.setUserIdentifier("userId");
        when(portfolioRepository.findOne(eq("portfolioId"))).thenReturn(portfolio);


        PortfolioAlarmListener listener = new PortfolioAlarmListener();
        listener.setPortfolioAlarmRepository(repository);
        listener.setPortfolioRepository(portfolioRepository);
        listener.setHighestAmountThreshold(BigDecimal.valueOf(4568));

        ItemAddedToPortfolioEvent event1 = new ItemAddedToPortfolioEvent(new PortfolioId("portfolioId"), new CoinId("BTC"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        ItemAddedToPortfolioEvent event2 = new ItemAddedToPortfolioEvent(new PortfolioId("portfolioId"), new CoinId("BTC"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        listener.handleEvent(event1);
        listener.handleEvent(event2);

        //assertion 
        ArgumentCaptor<PortfolioAlarmActivity> captor = ArgumentCaptor.forClass(PortfolioAlarmActivity.class);
        verify(repository).save(captor.capture());

        PortfolioAlarmActivity activity = captor.getValue();

        assertThat(activity.getPortfolioId(), equalTo("portfolioId"));
        assertThat(activity.getUserId(), equalTo("userId"));
        assertThat(activity.getUsername(), equalTo("username"));
        assertThat(activity.getTime(), equalTo(time));
        assertThat(activity.getType(), equalTo(PortfolioAlarmType.ADD_LARGE_AMOUNT_OF_COIN));
    }

    @Test
    public void testHandleWithdrawCoin() throws Exception {
        Date time = currentTime();

        PortfolioAlarmQueryRepository repository = mock(PortfolioAlarmQueryRepository.class);
        PortfolioQueryRepository portfolioRepository = mock(PortfolioQueryRepository.class);

        PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setPrimaryKey("portfolioId");
        portfolio.setUsername("username");
        portfolio.setUserIdentifier("userId");
        when(portfolioRepository.findOne(eq("portfolioId"))).thenReturn(portfolio);


        PortfolioAlarmListener listener = new PortfolioAlarmListener();
        listener.setPortfolioAlarmRepository(repository);
        listener.setPortfolioRepository(portfolioRepository);
        listener.setHighestAmountThreshold(BigDecimal.valueOf(9876));

        ItemWithdrawnEvent event1 = new ItemWithdrawnEvent(new PortfolioId("portfolioId"), new CoinId("BTC"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        ItemWithdrawnEvent event2 = new ItemWithdrawnEvent(new PortfolioId("portfolioId"), new CoinId("BTC"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        listener.handleEvent(event1);
        listener.handleEvent(event2);

        //assertion 
        ArgumentCaptor<PortfolioAlarmActivity> captor = ArgumentCaptor.forClass(PortfolioAlarmActivity.class);
        verify(repository).save(captor.capture());

        PortfolioAlarmActivity activity = captor.getValue();

        assertThat(activity.getPortfolioId(), equalTo("portfolioId"));
        assertThat(activity.getUserId(), equalTo("userId"));
        assertThat(activity.getUsername(), equalTo("username"));
        assertThat(activity.getTime(), equalTo(time));
        assertThat(activity.getType(), equalTo(PortfolioAlarmType.WITHDRAW_LARGE_AMOUNT_OF_COIN));
    }

    @Test
    public void testHandleAddMoney() throws Exception {
        Date time = currentTime();

        PortfolioAlarmQueryRepository repository = mock(PortfolioAlarmQueryRepository.class);
        PortfolioQueryRepository portfolioRepository = mock(PortfolioQueryRepository.class);

        PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setPrimaryKey("portfolioId");
        portfolio.setUsername("username");
        portfolio.setUserIdentifier("userId");
        when(portfolioRepository.findOne(eq("portfolioId"))).thenReturn(portfolio);


        PortfolioAlarmListener listener = new PortfolioAlarmListener();
        listener.setPortfolioAlarmRepository(repository);
        listener.setPortfolioRepository(portfolioRepository);
        listener.setHighestMoneyThreshold(BigDecimal.valueOf(1231));

        CashDepositedEvent event1 = new CashDepositedEvent(new PortfolioId("portfolioId"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        CashDepositedEvent event2 = new CashDepositedEvent(new PortfolioId("portfolioId"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);

        listener.handleEvent(event1);
        listener.handleEvent(event2);

        //assertion 
        ArgumentCaptor<PortfolioAlarmActivity> captor = ArgumentCaptor.forClass(PortfolioAlarmActivity.class);
        verify(repository).save(captor.capture());

        PortfolioAlarmActivity activity = captor.getValue();

        assertThat(activity.getPortfolioId(), equalTo("portfolioId"));
        assertThat(activity.getUserId(), equalTo("userId"));
        assertThat(activity.getUsername(), equalTo("username"));
        assertThat(activity.getTime(), equalTo(time));
        assertThat(activity.getType(), equalTo(PortfolioAlarmType.ADD_LARGE_AMOUNT_OF_MONEY));
    }

    @Test
    public void testHandleWithdrawMoney() throws Exception {
        Date time = currentTime();

        PortfolioAlarmQueryRepository repository = mock(PortfolioAlarmQueryRepository.class);
        PortfolioQueryRepository portfolioRepository = mock(PortfolioQueryRepository.class);

        PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setPrimaryKey("portfolioId");
        portfolio.setUsername("username");
        portfolio.setUserIdentifier("userId");
        when(portfolioRepository.findOne(eq("portfolioId"))).thenReturn(portfolio);


        PortfolioAlarmListener listener = new PortfolioAlarmListener();
        listener.setPortfolioAlarmRepository(repository);
        listener.setPortfolioRepository(portfolioRepository);
        listener.setHighestMoneyThreshold(BigDecimal.valueOf(3423));

        CashWithdrawnEvent event1 = new CashWithdrawnEvent(new PortfolioId("portfolioId"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        CashWithdrawnEvent event2 = new CashWithdrawnEvent(new PortfolioId("portfolioId"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        listener.handleEvent(event1);
        listener.handleEvent(event2);

        //assertion 
        ArgumentCaptor<PortfolioAlarmActivity> captor = ArgumentCaptor.forClass(PortfolioAlarmActivity.class);
        verify(repository).save(captor.capture());

        PortfolioAlarmActivity activity = captor.getValue();

        assertThat(activity.getPortfolioId(), equalTo("portfolioId"));
        assertThat(activity.getUserId(), equalTo("userId"));
        assertThat(activity.getUsername(), equalTo("username"));
        assertThat(activity.getTime(), equalTo(time));
        assertThat(activity.getType(), equalTo(PortfolioAlarmType.WITHDRAW_LARGE_AMOUNT_OF_MONEY));
    }
} 