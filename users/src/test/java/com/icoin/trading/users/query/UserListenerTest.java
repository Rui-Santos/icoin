package com.icoin.trading.users.query;

import com.icoin.trading.users.application.command.UserCommandHandler;
import com.icoin.trading.api.users.event.NotificationSettingsUpdatedEvent;
import com.icoin.trading.api.users.event.PasswordChangedEvent;
import com.icoin.trading.api.users.event.UserCreatedEvent;
import com.icoin.trading.api.users.event.WithdrawPasswordChangedEvent;
import com.icoin.trading.api.users.event.WithdrawPasswordCreatedEvent;
import com.icoin.trading.api.users.domain.Identifier;
import com.icoin.trading.api.users.domain.UserId;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/19/14
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserListenerTest {
    @Test
    public void testHandleUserCreated() throws Exception {
        final UserId userId = new UserId();
        final String firstName = "buyer";
        final String lastName = "abc";
        final String username = "buyerAbc";
        final String email = "buyerAbc@163.com";
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");
        final String password = "sjfsudm9ei8r899e9e87745jkdkjfhd";
        final Date time = currentTime();

        final UserQueryRepository repository = mock(UserQueryRepository.class);

        final UserCreatedEvent event = new UserCreatedEvent(userId, username, firstName, lastName, identifier, email, password, UserCommandHandler.DEFAULT_ROLES, time);
        final UserListener listener = new UserListener();
        listener.setUserRepository(repository);

        listener.handleUserCreated(event);

        final ArgumentCaptor<UserEntry> captor = ArgumentCaptor.forClass(UserEntry.class);
        verify(repository).save(captor.capture());
        final UserEntry userEntry = captor.getValue();

        assertThat(userEntry, notNullValue());
        assertThat(userEntry.getPrimaryKey(), equalTo(userId.toString()));
        assertThat(userEntry.getName(), equalTo(username));
        assertThat(userEntry.getFullName(), equalTo(lastName + " " + firstName));
        assertThat(userEntry.getUsername(), equalTo(username));
        assertThat(userEntry.getPassword(), equalTo(password));
        assertThat(userEntry.getIdentifier(), equalTo(identifier));
        assertThat(userEntry.getId(), equalTo(username));
        assertThat(userEntry.getFirstName(), equalTo(firstName));
        assertThat(userEntry.getLastName(), equalTo(lastName));
        assertThat(userEntry.getEmail(), equalTo(email));
    }

    @Test
    public void testHandleUserPasswordChangedWithUserNotFound() throws Exception {
        final UserId userId = new UserId();
        final String operatingIp = "207.45.15.2";
        final String password = "new password";
        final String username = "buyerAbc";
        final String email = "buyerAbc@163.com";
        final Date time = currentTime();

        final UserQueryRepository repository = mock(UserQueryRepository.class);

        final PasswordChangedEvent event = new PasswordChangedEvent(userId,
                username,
                email,
                password,
                password,
                operatingIp,
                time);

        final UserListener listener = new UserListener();
        listener.setUserRepository(repository);

        listener.handleUserPasswordChanged(event);

        verify(repository).findOne(eq(userId.toString()));
        verify(repository, never()).save(any(UserEntry.class));
    }

    @Test
    public void testHandleUserPasswordChanged() throws Exception {
        final UserId userId = new UserId();
        final String operatingIp = "207.45.15.2";
        final String password = "new password";
        final String username = "buyerAbc";
        final String email = "buyerAbc@163.com";
        final Date time = currentTime();

        final UserQueryRepository repository = mock(UserQueryRepository.class);
        UserEntry user = new UserEntry();
        when(repository.findOne(eq(userId.toString()))).thenReturn(user);

        final PasswordChangedEvent event = new PasswordChangedEvent(userId,
                username,
                email,
                password,
                password,
                operatingIp,
                time);

        final UserListener listener = new UserListener();
        listener.setUserRepository(repository);

        listener.handleUserPasswordChanged(event);

        final ArgumentCaptor<UserEntry> captor = ArgumentCaptor.forClass(UserEntry.class);
        verify(repository).save(captor.capture());
        final UserEntry userEntry = captor.getValue();

        assertThat(userEntry, notNullValue());
        assertThat(userEntry.getPassword(), equalTo(password));

        verify(repository).findOne(eq(userId.toString()));
    }

    @Test
    public void testHandleUserWithdrawPasswordCreated() throws Exception {
        final UserId userId = new UserId();
        final String operatingIp = "207.45.15.2";
        final String password = "new withdraw password";
        final String username = "buyerAbc";
        final String email = "buyerAbc@163.com";
        final Date time = currentTime();

        final UserQueryRepository repository = mock(UserQueryRepository.class);
        UserEntry user = new UserEntry();
        when(repository.findOne(eq(userId.toString()))).thenReturn(user);

        final WithdrawPasswordCreatedEvent event = new WithdrawPasswordCreatedEvent(userId,
                username,
                email,
                password,
                password,
                operatingIp,
                time);

        final UserListener listener = new UserListener();
        listener.setUserRepository(repository);

        listener.handleUserWithdrawPasswordCreated(event);

        final ArgumentCaptor<UserEntry> captor = ArgumentCaptor.forClass(UserEntry.class);
        verify(repository).save(captor.capture());
        final UserEntry userEntry = captor.getValue();

        assertThat(userEntry, notNullValue());
        assertThat(userEntry.getWithdrawPassword(), equalTo(password));

        verify(repository).findOne(eq(userId.toString()));
    }

    @Test
    public void testHandleUserWithdrawPasswordChanged() throws Exception {
        final UserId userId = new UserId();
        final String operatingIp = "207.45.15.2";
        final String password = "new withdraw password";
        final String username = "buyerAbc";
        final String email = "buyerAbc@163.com";
        final Date time = currentTime();

        final UserQueryRepository repository = mock(UserQueryRepository.class);
        UserEntry user = new UserEntry();
        when(repository.findOne(eq(userId.toString()))).thenReturn(user);

        final WithdrawPasswordChangedEvent event = new WithdrawPasswordChangedEvent(userId,
                username,
                email,
                password,
                password,
                operatingIp,
                time);

        final UserListener listener = new UserListener();
        listener.setUserRepository(repository);

        listener.handleUserWithdrawPasswordChanged(event);

        final ArgumentCaptor<UserEntry> captor = ArgumentCaptor.forClass(UserEntry.class);
        verify(repository).save(captor.capture());
        final UserEntry userEntry = captor.getValue();

        assertThat(userEntry, notNullValue());
        assertThat(userEntry.getWithdrawPassword(), equalTo(password));

        verify(repository).findOne(eq(userId.toString()));
    }

    @Test
    public void testHandleUpdateNotification() throws Exception {
        final UserId userId = new UserId();
        final String username = "buyerAbc";

        final UserQueryRepository repository = mock(UserQueryRepository.class);
        UserEntry user = new UserEntry();
        when(repository.findOne(eq(userId.toString()))).thenReturn(user);

        final NotificationSettingsUpdatedEvent event = new NotificationSettingsUpdatedEvent(userId,
                username,
                false,
                true,
                true,
                false);

        final UserListener listener = new UserListener();
        listener.setUserRepository(repository);

        listener.handleUpdateNotification(event);

        final ArgumentCaptor<UserEntry> captor = ArgumentCaptor.forClass(UserEntry.class);
        verify(repository).save(captor.capture());
        final UserEntry userEntry = captor.getValue();

        assertThat(userEntry, notNullValue());
        assertThat(userEntry.isLogonAlert(), is(false));
        assertThat(userEntry.isWithdrawMoneyAlert(), is(true));
        assertThat(userEntry.isWithdrawItemAlert(), is(true));
        assertThat(userEntry.isExecutedAlert(), is(false));

        verify(repository).findOne(eq(userId.toString()));
    }
} 