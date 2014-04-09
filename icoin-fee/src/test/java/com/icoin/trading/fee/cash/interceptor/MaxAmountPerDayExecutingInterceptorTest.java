package com.icoin.trading.fee.cash.interceptor;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.fee.cash.Invocation;
import com.icoin.trading.fee.cash.InvocationContext;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.domain.DueDateService;
import com.icoin.trading.fee.domain.cash.CashRepository;
import com.icoin.trading.fee.domain.cash.CoinReceiveCash;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM11:44
 * To change this template use File | Settings | File Templates.
 */
public class MaxAmountPerDayExecutingInterceptorTest {
    @Test
    public void testAllowed() throws Exception {
        final InvocationContext context = mock(InvocationContext.class);

        final Invocation invocation = mock(Invocation.class);
        when(invocation.getInvocationContext()).thenReturn(context);

        CashRepository cashRepository = mock(CashRepository.class);
        final CoinReceiveCash cash1 = new CoinReceiveCash();
        cash1.confirm(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN), new Date());
        final CoinReceiveCash cash2 = new CoinReceiveCash();
        cash2.confirm(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.ONE), new Date());
        when(cashRepository.findByUserId(anyString(), any(Date.class))).
                thenReturn(ImmutableList.of(cash1), ImmutableList.of(cash2));

        DueDateService dateService = mock(DueDateService.class);

        final MaxAmountPerDayExecutingInterceptor interceptor
                = new MaxAmountPerDayExecutingInterceptor("BTC", BigDecimal.TEN);
        interceptor.setCashRepository(cashRepository);
        interceptor.setService(dateService);


        final ValidationCode validationCode1 = interceptor.intercept(invocation);
        final ValidationCode validationCode2 = interceptor.intercept(invocation);

        assertThat(validationCode1, anyOf(nullValue(), is(ValidationCode.SUCCESSFUL)));
        assertThat(validationCode2, anyOf(nullValue(), is(ValidationCode.SUCCESSFUL)));

        verify(invocation, times(2)).invoke();
        verify(dateService, times(2)).computeDueDate(any(Date.class));
        verify(invocation, times(2)).invoke();
    }

    @Test
    public void testDisallowed() throws Exception {
        final InvocationContext context = mock(InvocationContext.class);

        final Invocation invocation = mock(Invocation.class);
        when(invocation.getInvocationContext()).thenReturn(context);

        CashRepository cashRepository = mock(CashRepository.class);
        final CoinReceiveCash cash1 = new CoinReceiveCash();
        cash1.confirm(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.TEN), new Date());
        final CoinReceiveCash cash2 = new CoinReceiveCash();
        cash2.confirm(BigMoney.of(CurrencyUnit.of("BTC"), BigDecimal.ONE), new Date());
        when(cashRepository.findByUserId(anyString(), any(Date.class))).
                thenReturn(ImmutableList.of(cash1, cash2));

        DueDateService dateService = mock(DueDateService.class);

        final MaxAmountPerDayExecutingInterceptor interceptor
                = new MaxAmountPerDayExecutingInterceptor("BTC", BigDecimal.TEN);
        interceptor.setCashRepository(cashRepository);
        interceptor.setService(dateService);


        final ValidationCode validationCode = interceptor.intercept(invocation);

        assertThat(validationCode, is(ValidationCode.EXCEEDED_MAX_AMOUNT_PER_DAY));

        verify(dateService).computeDueDate(any(Date.class));
        verify(invocation, never()).invoke();
    }
}
