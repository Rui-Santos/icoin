package com.icoin.trading.fee.cash;

import com.google.common.collect.ImmutableList;
import org.joda.money.BigMoney;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM9:04
 * To change this template use File | Settings | File Templates.
 */
public class DefaultInvocationTest {
    @Test
     public void testInvoke() throws Exception {
        SleepInterceptor interceptor1 = new SleepInterceptor(100);
        SleepInterceptor interceptor2 = new SleepInterceptor(50);
        DefaultInvocation invocation =
                new DefaultInvocation(new InvocationContext("userId", BigMoney.parse("GBP 1.23"), new Date()),
                        ImmutableList.of(interceptor1, interceptor2));

        ValidationCode validationCode = invocation.invoke();

        assertThat(interceptor1.isRan(), is(true));
        assertThat(interceptor2.isRan(), is(true));
        assertThat(invocation.isExecuted(), is(true));
        assertThat(validationCode, is(ValidationCode.SUCCESSFUL));
    }

    @Test
    public void testInvokeWithExceptionalInterceptor() throws Exception {
        SleepInterceptor interceptor1 = new SleepInterceptor(100);
        ExceptionalInterceptor interceptor2 = new ExceptionalInterceptor();
        SleepInterceptor interceptor3 = new SleepInterceptor(250);
        DefaultInvocation invocation =
                new DefaultInvocation(new InvocationContext("userId", BigMoney.parse("GBP 1.23"), new Date()),
                        ImmutableList.of(interceptor1, interceptor2, interceptor3));

        ValidationCode validationCode = invocation.invoke();

        assertThat(interceptor1.isRan(), is(true));
        assertThat(interceptor3.isRan(), is(false));
        assertThat(invocation.isExecuted(), is(true));
        assertThat(validationCode, is(ValidationCode.EXECUTION_ERROR));
    }

    @Test
    public void testInvokeWithErrorCodeInterceptor() throws Exception {
        SleepInterceptor interceptor1 = new SleepInterceptor(100);
        ErrorCodeInterceptor interceptor2 = new ErrorCodeInterceptor();
        SleepInterceptor interceptor3 = new SleepInterceptor(250);
        DefaultInvocation invocation =
                new DefaultInvocation(new InvocationContext("userId", BigMoney.parse("GBP 1.23"), new Date()),
                        ImmutableList.of(interceptor1, interceptor2, interceptor3));

        ValidationCode validationCode = invocation.invoke();

        assertThat(interceptor1.isRan(), is(true));
        assertThat(interceptor3.isRan(), is(false));
        assertThat(invocation.isExecuted(), is(true));
        assertThat(validationCode, is(ValidationCode.SYSTEM_DISALLOWED));
    }
}
