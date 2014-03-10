package com.icoin.trading.tradeengine.query.portfolio;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.tradeengine.domain.events.portfolio.PortfolioCreatedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashDepositedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationCancelledEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservationConfirmedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashReservedEvent;
import com.icoin.trading.tradeengine.domain.events.portfolio.cash.CashWithdrawnEvent;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.users.domain.model.user.Identifier;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.joda.money.BigMoney;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-4
 * Time: PM2:03
 * To change this template use File | Settings | File Templates.
 */
public class PortfolioMoneyEventListenerTest {

    private final OrderBookId itemIdentifier = new OrderBookId();
    private final CoinId coinIdentifier = new CoinId();


    @Test
    public void testHandlePortfolioCreated() throws Exception {
        final String username = "test";
        final String firstName = "Test";
        final String lastName = "User";
        final UserId userIdentifier = new UserId();
        final PortfolioId portfolioIdentifier = new PortfolioId();
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");

        final UserEntry user = new UserEntry();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setIdentifier(identifier);

        PortfolioQueryRepository portfolioQueryRepository = mock(PortfolioQueryRepository.class);
        UserQueryRepository userQueryRepository = mock(UserQueryRepository.class);
        when(userQueryRepository.findOne(eq(userIdentifier.toString()))).thenReturn(user);

        final PortfolioMoneyEventListener listener = new PortfolioMoneyEventListener();
        listener.setPortfolioRepository(portfolioQueryRepository);
        listener.setUserQueryRepository(userQueryRepository);

        final PortfolioCreatedEvent event = new PortfolioCreatedEvent(portfolioIdentifier, userIdentifier);
        listener.handleEvent(event);

        ArgumentCaptor<PortfolioEntry> captor = ArgumentCaptor.forClass(PortfolioEntry.class);
        verify(portfolioQueryRepository).save(captor.capture());
        final PortfolioEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getPrimaryKey(), equalTo(portfolioIdentifier.toString()));
        assertThat(saved.getFullName(), equalTo(lastName + " " + firstName));
        assertThat(saved.getUsername(), equalTo(username));
        assertThat(saved.getUserIdentifier(), equalTo(userIdentifier.toString()));
        assertThat(saved.getAmountOfMoney(), equalTo(BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT)));
        assertThat(saved.getReservedAmountOfMoney(), equalTo(BigMoney.zero(Constants.DEFAULT_CURRENCY_UNIT)));
    }

    @Test
    public void testHandleCashDeposited() throws Exception {
        final PortfolioId portfolioIdentifier = new PortfolioId();
        final BigMoney availableMoney = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 11);
        final BigMoney moneyAdded = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.091);

        PortfolioQueryRepository portfolioQueryRepository = mock(PortfolioQueryRepository.class);
        final PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setAmountOfMoney(availableMoney);
        when(portfolioQueryRepository.findOne(eq(portfolioIdentifier.toString()))).
                thenReturn(portfolio);

        final PortfolioMoneyEventListener listener = new PortfolioMoneyEventListener();
        listener.setPortfolioRepository(portfolioQueryRepository);

        final CashDepositedEvent event = new CashDepositedEvent(portfolioIdentifier, moneyAdded);
        listener.handleEvent(event);

        ArgumentCaptor<PortfolioEntry> captor = ArgumentCaptor.forClass(PortfolioEntry.class);
        verify(portfolioQueryRepository).save(captor.capture());
        final PortfolioEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getAmountOfMoney(), equalTo(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 21.091)));
    }

    @Test
    public void testHandleCashWithdrawn() throws Exception {
        final Date current = currentTime();
        final PortfolioId portfolioIdentifier = new PortfolioId();
        final BigMoney availableMoney = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 11);
        final BigMoney moneyWithdrawn = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.091);

        PortfolioQueryRepository portfolioQueryRepository = mock(PortfolioQueryRepository.class);
        final PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setAmountOfMoney(availableMoney);
        when(portfolioQueryRepository.findOne(eq(portfolioIdentifier.toString()))).
                thenReturn(portfolio);

        final PortfolioMoneyEventListener listener = new PortfolioMoneyEventListener();
        listener.setPortfolioRepository(portfolioQueryRepository);

        final CashWithdrawnEvent event = new CashWithdrawnEvent(portfolioIdentifier, moneyWithdrawn, current);
        listener.handleEvent(event);

        ArgumentCaptor<PortfolioEntry> captor = ArgumentCaptor.forClass(PortfolioEntry.class);
        verify(portfolioQueryRepository).save(captor.capture());
        final PortfolioEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getAmountOfMoney(), equalTo(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 0.909)));
    }

    @Test
    public void testHandleCashReserved() throws Exception {
        final PortfolioId portfolioIdentifier = new PortfolioId();
        final BigMoney reserved = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 11);
        final BigMoney total = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.091);
        final BigMoney commission = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.091);
        final TransactionId transactionIdentifier = new TransactionId();

        PortfolioQueryRepository portfolioQueryRepository = mock(PortfolioQueryRepository.class);
        final PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setReservedAmountOfMoney(reserved);
        when(portfolioQueryRepository.findOne(eq(portfolioIdentifier.toString()))).
                thenReturn(portfolio);

        final PortfolioMoneyEventListener listener = new PortfolioMoneyEventListener();
        listener.setPortfolioRepository(portfolioQueryRepository);

        final CashReservedEvent event = new CashReservedEvent(portfolioIdentifier, transactionIdentifier, total, commission);
        listener.handleEvent(event);

        ArgumentCaptor<PortfolioEntry> captor = ArgumentCaptor.forClass(PortfolioEntry.class);
        verify(portfolioQueryRepository).save(captor.capture());
        final PortfolioEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getReservedAmountOfMoney(), equalTo(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 31.182)));
    }

    @Test
    public void testHandleCashReservationCancelled() throws Exception {
        final PortfolioId portfolioIdentifier = new PortfolioId();
        final BigMoney reserved = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 11);
        final BigMoney total = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.091);
        final BigMoney commission = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 0.9);
        final TransactionId transactionIdentifier = new TransactionId();

        PortfolioQueryRepository portfolioQueryRepository = mock(PortfolioQueryRepository.class);
        final PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setReservedAmountOfMoney(reserved);
        when(portfolioQueryRepository.findOne(eq(portfolioIdentifier.toString()))).
                thenReturn(portfolio);

        final PortfolioMoneyEventListener listener = new PortfolioMoneyEventListener();
        listener.setPortfolioRepository(portfolioQueryRepository);

        final CashReservationCancelledEvent event = new CashReservationCancelledEvent(portfolioIdentifier, transactionIdentifier, total, commission);
        listener.handleEvent(event);

        ArgumentCaptor<PortfolioEntry> captor = ArgumentCaptor.forClass(PortfolioEntry.class);
        verify(portfolioQueryRepository).save(captor.capture());
        final PortfolioEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getReservedAmountOfMoney(), equalTo(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 0.009)));
    }

    @Test
    public void testHandleCashReservationConfirmed() throws Exception {
        final PortfolioId portfolioIdentifier = new PortfolioId();
        final BigMoney reserved = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 11);
        final BigMoney available = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 11);
        final BigMoney total = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 10.091);
        final BigMoney commission = BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 0.9);
        final TransactionId transactionIdentifier = new TransactionId();

        PortfolioQueryRepository portfolioQueryRepository = mock(PortfolioQueryRepository.class);
        final PortfolioEntry portfolio = new PortfolioEntry();
        portfolio.setReservedAmountOfMoney(reserved);
        portfolio.setAmountOfMoney(available);

        when(portfolioQueryRepository.findOne(eq(portfolioIdentifier.toString()))).
                thenReturn(portfolio);

        final PortfolioMoneyEventListener listener = new PortfolioMoneyEventListener();
        listener.setPortfolioRepository(portfolioQueryRepository);

        final CashReservationConfirmedEvent event = new CashReservationConfirmedEvent(portfolioIdentifier, transactionIdentifier, total, commission);
        listener.handleEvent(event);

        ArgumentCaptor<PortfolioEntry> captor = ArgumentCaptor.forClass(PortfolioEntry.class);
        verify(portfolioQueryRepository).save(captor.capture());
        final PortfolioEntry saved = captor.getValue();

        assertThat(saved, notNullValue());
        assertThat(saved.getReservedAmountOfMoney(), equalTo(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 0.009)));
        assertThat(saved.getAmountOfMoney(), equalTo(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 0.009)));
        assertThat(saved.obtainMoneyToSpend(), equalTo(BigMoney.of(Constants.DEFAULT_CURRENCY_UNIT, 0).toMoney().toBigMoney()));
    }
}
