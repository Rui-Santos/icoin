package com.icoin.trading.fee.cash.interceptor;

import com.icoin.trading.fee.cash.Invocation;
import com.icoin.trading.fee.cash.InvocationContext;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.domain.cash.CashAdmin;
import com.icoin.trading.fee.domain.cash.CashAdminRepository;
import com.icoin.trading.users.domain.model.user.UserAccount;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM11:36
 * To change this template use File | Settings | File Templates.
 */
public class UserStatusInterceptorTest {
    @Test
    public void testAllowed() throws Exception {
        final CashAdminRepository repository = mock(CashAdminRepository.class);
        final CashAdmin cashAdmin = new CashAdmin();
        cashAdmin.enableWithdrawingCoin();
        when(repository.findAll()).thenReturn(null, Collections.EMPTY_LIST, Arrays.asList(cashAdmin));

        final UserStatusInterceptor interceptor = new UserStatusInterceptor();

        final InvocationContext context = mock(InvocationContext.class);
        final UserAccount userAccount = mock(UserAccount.class);
        when(userAccount.isLocked(any(Date.class))).thenReturn(false);

        when(context.getUser()).thenReturn(userAccount);

        final Invocation invocation = mock(Invocation.class);
        when(invocation.getInvocationContext()).thenReturn(context);

        final ValidationCode validationCode = interceptor.intercept(invocation);

        assertThat(validationCode, anyOf(nullValue(), is(ValidationCode.SUCCESSFUL)));
    }

    @Test
    public void testDisallowed() throws Exception {
        final CashAdminRepository repository = mock(CashAdminRepository.class);
        final CashAdmin cashAdmin = new CashAdmin();
        cashAdmin.enableWithdrawingCoin();
        when(repository.findAll()).thenReturn(null, Collections.EMPTY_LIST, Arrays.asList(cashAdmin));

        final UserStatusInterceptor interceptor = new UserStatusInterceptor();

        final InvocationContext context = mock(InvocationContext.class);
        final UserAccount userAccount = mock(UserAccount.class);
        when(userAccount.isLocked(any(Date.class))).thenReturn(true);

        when(context.getUser()).thenReturn(null, userAccount);

        final Invocation invocation = mock(Invocation.class);
        when(invocation.getInvocationContext()).thenReturn(context);

        final ValidationCode validationCode1 = interceptor.intercept(invocation);
        final ValidationCode validationCode2 = interceptor.intercept(invocation);

        assertThat(validationCode1, is(ValidationCode.USER_NOT_FOUND));
        assertThat(validationCode2, is(ValidationCode.USER_LOCKED));

    }
}
