package com.icoin.trading.fee.cash;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.users.domain.model.user.UserAccount;
import org.joda.money.BigMoney;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM9:06
 * To change this template use File | Settings | File Templates.
 */
public class InvocationProxyTest {
    @Test
    public void test() throws Exception {
        InvocationContext context = mock(InvocationContext.class);

        Invocation invocation = mock(Invocation.class);
        when(invocation.getInvocationContext()).thenReturn(context);

        InvocationProxy proxy = new InvocationProxy(invocation);

        proxy.invoke();

        verify(invocation).invoke();
        verify(context).printProfiling();
    }

    @Test
    public void testWithException() throws Exception {
        InvocationContext context = mock(InvocationContext.class);

        Invocation invocation = mock(Invocation.class);
        when(invocation.invoke()).thenThrow(new RuntimeException());
        when(invocation.getInvocationContext()).thenReturn(context);

        InvocationProxy proxy = new InvocationProxy(invocation);

        boolean hasException = false;

        try {
            proxy.invoke();
        } catch (Exception e) {
            hasException = true;
        }

        assertThat(hasException, is(true));

        verify(invocation).invoke();
        verify(context).printProfiling();
    }

    @Test
    public void testIntegration() throws Exception {
        SleepInterceptor interceptor1 = new SleepInterceptor(100);
        SleepInterceptor interceptor2 = new SleepInterceptor(50);
        final UserAccount user = mock(UserAccount.class);
        DefaultInvocation invocation =
                new DefaultInvocation(new InvocationContext(user, BigMoney.parse("GBP 1.23"), new Date()),
                        ImmutableList.of(interceptor1, interceptor2));

        InvocationProxy proxy = new InvocationProxy(invocation);

        ValidationCode resultCode = proxy.invoke();

        assertThat(interceptor1.isRan(), is(true));
        assertThat(interceptor2.isRan(), is(true));
        assertThat(invocation.isExecuted(), is(true));
        assertThat(resultCode, is(ValidationCode.SUCCESSFUL));
    }
}