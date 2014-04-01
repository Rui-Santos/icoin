package com.icoin.trading.fee.cash.interceptor;

import com.icoin.trading.fee.cash.Invocation;
import com.icoin.trading.fee.cash.InvocationContext;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.domain.cash.CashAdmin;
import com.icoin.trading.fee.domain.cash.CashAdminRepository;
import org.joda.money.BigMoney;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-31
 * Time: PM10:43
 * To change this template use File | Settings | File Templates.
 */
public class AmountInterceptorTest {
    @Test
    public void testAllowed() throws Exception {
        final AmountInterceptor interceptor = new AmountInterceptor();

        final InvocationContext context = mock(InvocationContext.class);
        when(context.getAmount()).thenReturn(BigMoney.parse("BTC 0.01"),
                                                BigMoney.parse("BTC 15"),
                                                BigMoney.parse("BTC 0.01"),
                                                BigMoney.parse("BTC 0.005"));

        final Invocation invocation = mock(Invocation.class);
        when(invocation.getInvocationContext()).thenReturn(context);

        final ValidationCode validationCode1 = interceptor.intercept(invocation);
        interceptor.setMaxAmount(BigMoney.parse("BTC 15"));
        final ValidationCode validationCode2 = interceptor.intercept(invocation);
        final ValidationCode validationCode3= interceptor.intercept(invocation);
        interceptor.setMinAmount(BigMoney.parse("BTC 0.001"));
        final ValidationCode validationCode4 = interceptor.intercept(invocation);

        assertThat(validationCode1, anyOf(nullValue(), is(ValidationCode.SUCCESSFUL)));
        assertThat(validationCode2, anyOf(nullValue(), is(ValidationCode.SUCCESSFUL)));
        assertThat(validationCode3, anyOf(nullValue(), is(ValidationCode.SUCCESSFUL)));
        assertThat(validationCode4, anyOf(nullValue(), is(ValidationCode.SUCCESSFUL)));

        verify(context, times(4)).getAmount();
        verify(invocation, times(4)).invoke();
    }

    @Test
    public void testDisallowed() throws Exception {
        final AmountInterceptor interceptor = new AmountInterceptor();

        final InvocationContext context = mock(InvocationContext.class);
        when(context.getAmount()).thenReturn(BigMoney.parse("BTC 10.1"),
                BigMoney.parse("BTC 15.000002"),
                BigMoney.parse("BTC 0.001"),
                BigMoney.parse("BTC 0.0009999"));

        final Invocation invocation = mock(Invocation.class);
        when(invocation.getInvocationContext()).thenReturn(context);

        final ValidationCode validationCode1 = interceptor.intercept(invocation);
        interceptor.setMaxAmount(BigMoney.parse("BTC 15"));
        final ValidationCode validationCode2 = interceptor.intercept(invocation);
        final ValidationCode validationCode3= interceptor.intercept(invocation);
        interceptor.setMinAmount(BigMoney.parse("BTC 0.001"));
        final ValidationCode validationCode4 = interceptor.intercept(invocation);

        assertThat(validationCode1, anyOf(nullValue(), is(ValidationCode.EXCEEDED_MAX_AMOUNT_PER_TIME)));
        assertThat(validationCode2, anyOf(nullValue(), is(ValidationCode.EXCEEDED_MAX_AMOUNT_PER_TIME)));
        assertThat(validationCode3, anyOf(nullValue(), is(ValidationCode.EXCEEDED_MIN_AMOUNT_PER_TIME)));
        assertThat(validationCode4, anyOf(nullValue(), is(ValidationCode.EXCEEDED_MIN_AMOUNT_PER_TIME)));

        verify(context, times(4)).getAmount();
        verify(invocation, never()).invoke();
    }
}
