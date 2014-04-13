package com.icoin.trading.tradeengine.query.activity.listeners;

import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.events.portfolio.cash.CashDepositedEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.cash.CashWithdrawnEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.coin.ItemAddedToPortfolioEvent;
import com.icoin.trading.api.tradeengine.events.portfolio.coin.ItemWithdrawnEvent;
import com.icoin.trading.tradeengine.query.activity.Activity;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivity;
import com.icoin.trading.tradeengine.query.activity.PortfolioActivityType;
import com.icoin.trading.tradeengine.query.activity.repositories.PortfolioActivityQueryRepository;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/10/14
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class PortfolioActivityListenerTest {
    @Test
    public void testHandleAddCoin() throws Exception {
        Date time = currentTime();

        PortfolioActivityQueryRepository repository = mock(PortfolioActivityQueryRepository.class);
        PortfolioQueryRepository portfolioRepository = mock(PortfolioQueryRepository.class);

        PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setPrimaryKey("portfolioId");
        portfolio.setUsername("username");
        portfolio.setUserIdentifier("userId");
        when(portfolioRepository.findOne(eq("portfolioId"))).thenReturn(portfolio);


        PortfolioActivityListener listener = new PortfolioActivityListener();
        listener.setPortfolioActivityRepository(repository);
        listener.setPortfolioRepository(portfolioRepository);

        ItemAddedToPortfolioEvent event1 = new ItemAddedToPortfolioEvent(new PortfolioId("portfolioId"), new CoinId("BTC"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        ItemAddedToPortfolioEvent event2 = new ItemAddedToPortfolioEvent(new PortfolioId("portfolioId"), new CoinId("BTC"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        listener.handleEvent(event1);
        listener.handleEvent(event2);

        //assertion 
        ArgumentCaptor<PortfolioActivity> captor = ArgumentCaptor.forClass(PortfolioActivity.class);
        verify(repository).save(captor.capture());

        PortfolioActivity activity = captor.getValue();

        assertThat(activity.getPortfolioId(), equalTo("portfolioId"));
        assertThat(activity.getUserId(), equalTo("userId"));
        assertThat(activity.getUsername(), equalTo("username"));
        assertThat(activity.getType(), equalTo(PortfolioActivityType.ADD_COIN));
        Activity act = activity.getActivity();
        assertThat(act, notNullValue());
        assertThat(act.getActivityItems(), contains());
    }

    @Test
    public void testHandleWithdrawCoin() throws Exception {
        Date time = currentTime();

        PortfolioActivityQueryRepository repository = mock(PortfolioActivityQueryRepository.class);
        PortfolioQueryRepository portfolioRepository = mock(PortfolioQueryRepository.class);

        PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setPrimaryKey("portfolioId");
        portfolio.setUsername("username");
        portfolio.setUserIdentifier("userId");
        when(portfolioRepository.findOne(eq("portfolioId"))).thenReturn(portfolio);


        PortfolioActivityListener listener = new PortfolioActivityListener();
        listener.setPortfolioActivityRepository(repository);
        listener.setPortfolioRepository(portfolioRepository);

        ItemWithdrawnEvent event1 = new ItemWithdrawnEvent(new PortfolioId("portfolioId"), new CoinId("BTC"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        ItemWithdrawnEvent event2 = new ItemWithdrawnEvent(new PortfolioId("portfolioId"), new CoinId("BTC"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        listener.handleEvent(event1);
        listener.handleEvent(event2);

        //assertion 
        ArgumentCaptor<PortfolioActivity> captor = ArgumentCaptor.forClass(PortfolioActivity.class);
        verify(repository).save(captor.capture());

        PortfolioActivity activity = captor.getValue();

        assertThat(activity.getPortfolioId(), equalTo("portfolioId"));
        assertThat(activity.getUserId(), equalTo("userId"));
        assertThat(activity.getUsername(), equalTo("username"));
        assertThat(activity.getType(), equalTo(PortfolioActivityType.WITHDRAW_COIN));
        Activity act = activity.getActivity();
        assertThat(act, notNullValue());
        assertThat(act.getActivityItems(), contains());
    }

    @Test
    public void testHandleAddMoney() throws Exception {
        Date time = currentTime();

        PortfolioActivityQueryRepository repository = mock(PortfolioActivityQueryRepository.class);
        PortfolioQueryRepository portfolioRepository = mock(PortfolioQueryRepository.class);

        PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setPrimaryKey("portfolioId");
        portfolio.setUsername("username");
        portfolio.setUserIdentifier("userId");
        when(portfolioRepository.findOne(eq("portfolioId"))).thenReturn(portfolio);


        PortfolioActivityListener listener = new PortfolioActivityListener();
        listener.setPortfolioActivityRepository(repository);
        listener.setPortfolioRepository(portfolioRepository);

        CashDepositedEvent event1 = new CashDepositedEvent(new PortfolioId("portfolioId"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        CashDepositedEvent event2 = new CashDepositedEvent(new PortfolioId("portfolioId"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);

        listener.handleEvent(event1);
        listener.handleEvent(event2);

        //assertion 
        ArgumentCaptor<PortfolioActivity> captor = ArgumentCaptor.forClass(PortfolioActivity.class);
        verify(repository).save(captor.capture());

        PortfolioActivity activity = captor.getValue();

        assertThat(activity.getPortfolioId(), equalTo("portfolioId"));
        assertThat(activity.getUserId(), equalTo("userId"));
        assertThat(activity.getUsername(), equalTo("username"));
        assertThat(activity.getType(), equalTo(PortfolioActivityType.ADD_MONEY));
        Activity act = activity.getActivity();
        assertThat(act, notNullValue());
        assertThat(act.getActivityItems(), contains());
    }

    @Test
    public void testHandleWithdrawMoney() throws Exception {
        Date time = currentTime();

        PortfolioActivityQueryRepository repository = mock(PortfolioActivityQueryRepository.class);
        PortfolioQueryRepository portfolioRepository = mock(PortfolioQueryRepository.class);

        PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setPrimaryKey("portfolioId");
        portfolio.setUsername("username");
        portfolio.setUserIdentifier("userId");
        when(portfolioRepository.findOne(eq("portfolioId"))).thenReturn(portfolio);


        PortfolioActivityListener listener = new PortfolioActivityListener();
        listener.setPortfolioActivityRepository(repository);
        listener.setPortfolioRepository(portfolioRepository);

        CashWithdrawnEvent event1 = new CashWithdrawnEvent(new PortfolioId("portfolioId"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        CashWithdrawnEvent event2 = new CashWithdrawnEvent(new PortfolioId("portfolioId"), BigMoney.of(CurrencyUnit.of("BTC"), 10), time);
        listener.handleEvent(event1);
        listener.handleEvent(event2);

        //assertion 
        ArgumentCaptor<PortfolioActivity> captor = ArgumentCaptor.forClass(PortfolioActivity.class);
        verify(repository).save(captor.capture());

        PortfolioActivity activity = captor.getValue();

        assertThat(activity.getPortfolioId(), equalTo("portfolioId"));
        assertThat(activity.getUserId(), equalTo("userId"));
        assertThat(activity.getUsername(), equalTo("username"));
        assertThat(activity.getType(), equalTo(PortfolioActivityType.WITHDRAW_MONEY));
        Activity act = activity.getActivity();
        assertThat(act, notNullValue());
        assertThat(act.getActivityItems(), contains());
    }
} 
