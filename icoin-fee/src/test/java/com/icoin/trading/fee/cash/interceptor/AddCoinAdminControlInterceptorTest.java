package com.icoin.trading.fee.cash.interceptor;

import com.icoin.trading.fee.cash.Invocation;
import com.icoin.trading.fee.cash.InvocationContext;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.domain.cash.CashAdmin;
import com.icoin.trading.fee.domain.cash.CashAdminRepository;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-29
 * Time: AM9:34
 * To change this template use File | Settings | File Templates.
 */
public class AddCoinAdminControlInterceptorTest {
    @Test
    public void testAllowed() throws Exception {
        final CashAdminRepository repository = mock(CashAdminRepository.class);
        final CashAdmin cashAdmin = new CashAdmin();
        cashAdmin.enableAddingCoin();
        when(repository.findAll()).thenReturn(null, Collections.EMPTY_LIST, Arrays.asList(cashAdmin));

        final AddCoinAdminControlInterceptor interceptor = new AddCoinAdminControlInterceptor();
        interceptor.setRepository(repository);

        final Invocation invocation = mock(Invocation.class);
        final InvocationContext context = mock(InvocationContext.class);
        when(invocation.getInvocationContext()).thenReturn(context);

        final ValidationCode validationCode1 = interceptor.intercept(invocation);
        final ValidationCode validationCode2 = interceptor.intercept(invocation);
        final ValidationCode validationCode3 = interceptor.intercept(invocation);

        assertThat(validationCode1, anyOf(nullValue(), is(ValidationCode.SUCCESSFUL)));
        assertThat(validationCode2, anyOf(nullValue(), is(ValidationCode.SUCCESSFUL)));
        assertThat(validationCode3, anyOf(nullValue(), is(ValidationCode.SUCCESSFUL)));
    }

    @Test
    public void testDisallowed() throws Exception {
        final CashAdminRepository repository = mock(CashAdminRepository.class);
        final CashAdmin cashAdmin = new CashAdmin();
        cashAdmin.preventAddingCoin();
        when(repository.findAll()).thenReturn(Arrays.asList(cashAdmin));

        final AddCoinAdminControlInterceptor interceptor = new AddCoinAdminControlInterceptor();
        interceptor.setRepository(repository);

        final Invocation invocation = mock(Invocation.class);
        final InvocationContext context = mock(InvocationContext.class);
        when(invocation.getInvocationContext()).thenReturn(context);

        final ValidationCode validationCode = interceptor.intercept(invocation);

        assertThat(validationCode, anyOf(nullValue(), is(ValidationCode.SYSTEM_DISALLOWED)));

    }
}
