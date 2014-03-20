package com.icoin.trading.users.infrastructure.listener;

import com.google.common.collect.ImmutableMap;
import com.icoin.trading.infrastructure.mail.VelocityEmailSender;
import com.icoin.trading.users.application.command.UserCommandHandler;
import com.icoin.trading.api.users.event.PasswordChangedEvent;
import com.icoin.trading.api.users.event.UserAuthenticatedEvent;
import com.icoin.trading.api.users.event.UserCreatedEvent;
import com.icoin.trading.api.users.event.WithdrawPasswordChangedEvent;
import com.icoin.trading.api.users.event.WithdrawPasswordCreatedEvent;
import com.icoin.trading.api.users.domain.Identifier;
import com.icoin.trading.api.users.domain.UserId;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

import static com.homhon.util.TimeUtils.currentTime;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-19
 * Time: PM9:36
 * To change this template use File | Settings | File Templates.
 */
public class UserActivityListenerTest {
    @Test
    public void testHandleUserCreated() throws Exception {
        final UserId userId = new UserId();
        final String subject = "Successful registered";
        final String from = "admin@icoin.com";
        final String template = "listener/user-created.vm";
        final String encode = "utf-8";
        final String firstName = "buyer";
        final String lastName = "abc";
        final String username = "buyerAbc";
        final String email = "buyerAbc@163.com";
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");
        final String password = "sjfsudm9ei8r899e9e87745jkdkjfhd";
        final Date time = currentTime();

        final Map<String, Object> model =
                ImmutableMap.of("username", (Object) username);

        final VelocityEmailSender sender = mock(VelocityEmailSender.class);
        doNothing().when(sender).sendEmail(eq(subject),
                eq(email),
                eq(from),
                eq(template),
                eq(encode),
                eq(model),
                eq(true));

        final UserCreatedEvent event = new UserCreatedEvent(userId, username, firstName, lastName, identifier, email, password, UserCommandHandler.DEFAULT_ROLES, time);
        final UserActivityListener listener = new UserActivityListener();
        listener.setSender(sender);

        listener.handle(event);

        verify(sender).sendEmail(eq(subject),
                eq(email),
                eq(from),
                eq(template),
                eq(encode),
                eq(model),
                eq(true));
    }

    @Test
    public void testHandleUserAuthed() throws Exception {
        final UserId userId = new UserId();
        final String subject = "Successful authenticated";
        final String from = "admin@icoin.com";
        final String template = "listener/logged-on.vm";
        final String encode = "utf-8";
        final String operatingIp = "207.45.15.2";
        final String username = "buyerAbc";
        final String email = "buyerAbc@163.com";
        final Date time = currentTime();

        final Map<String, Object> model =
                ImmutableMap.of("username", (Object) username,
                        "ip", operatingIp,
                        "time", time);

        final VelocityEmailSender sender = mock(VelocityEmailSender.class);
        doNothing().when(sender).sendEmail(eq(subject),
                eq(email),
                eq(from),
                eq(template),
                eq(encode),
                eq(model),
                eq(true));

        final UserQueryRepository repository = mock(UserQueryRepository.class);
        UserEntry user = new UserEntry();
        user.setLogonAlert(false);
        user.setEmail(email);
        user.setUsername(username);
        when(repository.findOne(eq(userId.toString()))).thenReturn(null, user, user);

        final UserAuthenticatedEvent event = new UserAuthenticatedEvent(userId,
                username,
                email,
                operatingIp,
                time);

        final UserActivityListener listener = new UserActivityListener();
        listener.setUserRepository(repository);
        listener.setSender(sender);

        listener.handle(event);
        listener.handle(event);
        user.setLogonAlert(true);
        listener.handle(event);

        verify(repository, times(3)).findOne(eq(userId.toString()));
        verify(sender).sendEmail(eq(subject),
                eq(email),
                eq(from),
                eq(template),
                eq(encode),
                eq(model),
                eq(true));
    }

    @Test
    public void testHandleUserPasswordChanged() throws Exception {
        final UserId userId = new UserId();
        final String subject = "Your password has been changed";
        final String from = "admin@icoin.com";
        final String template = "listener/password-changed.vm";
        final String encode = "utf-8";
        final String operatingIp = "207.45.15.2";
        final String password = "new password";
        final String username = "buyerAbc";
        final String email = "buyerAbc@163.com";
        final Date time = currentTime();

        final Map<String, Object> model =
                ImmutableMap.of("username", (Object) username,
                        "ip", operatingIp,
                        "time", time);

        final VelocityEmailSender sender = mock(VelocityEmailSender.class);
        doNothing().when(sender).sendEmail(eq(subject),
                eq(email),
                eq(from),
                eq(template),
                eq(encode),
                eq(model),
                eq(true));

        final UserQueryRepository repository = mock(UserQueryRepository.class);
        UserEntry user = new UserEntry();
        user.setChangePasswordAlert(false);
        user.setEmail(email);
        user.setUsername(username);
        when(repository.findOne(eq(userId.toString()))).thenReturn(null, user, user);

        final PasswordChangedEvent event = new PasswordChangedEvent(userId,
                username,
                email,
                password,
                password,
                operatingIp,
                time);

        final UserActivityListener listener = new UserActivityListener();
        listener.setUserRepository(repository);
        listener.setSender(sender);

        listener.handle(event);
        listener.handle(event);
        user.setChangePasswordAlert(true);
        listener.handle(event);

        verify(repository, times(3)).findOne(eq(userId.toString()));
        verify(sender).sendEmail(eq(subject),
                eq(email),
                eq(from),
                eq(template),
                eq(encode),
                eq(model),
                eq(true));
    }

    @Test
    public void testHandleUserWithdrawPasswordCreated() throws Exception {
        final UserId userId = new UserId();
        final String subject = "Your withdraw password has been created";
        final String from = "admin@icoin.com";
        final String template = "listener/withdraw-password-created.vm";
        final String encode = "utf-8";
        final String operatingIp = "207.45.15.2";
        final String password = "new password";
        final String username = "buyerAbc";
        final String email = "buyerAbc@163.com";
        final Date time = currentTime();

        final Map<String, Object> model =
                ImmutableMap.of("username", (Object) username,
                        "ip", operatingIp,
                        "time", time);

        final VelocityEmailSender sender = mock(VelocityEmailSender.class);
        doNothing().when(sender).sendEmail(eq(subject),
                eq(email),
                eq(from),
                eq(template),
                eq(encode),
                eq(model),
                eq(true));

        final UserQueryRepository repository = mock(UserQueryRepository.class);
        UserEntry user = new UserEntry();
        user.setEmail(email);
        user.setUsername(username);
        when(repository.findOne(eq(userId.toString()))).thenReturn(null, user, user);

        final WithdrawPasswordCreatedEvent event = new WithdrawPasswordCreatedEvent(userId,
                username,
                email,
                password,
                password,
                operatingIp,
                time);

        final UserActivityListener listener = new UserActivityListener();
        listener.setUserRepository(repository);
        listener.setSender(sender);

        listener.handle(event);
        listener.handle(event);

        verify(repository, times(2)).findOne(eq(userId.toString()));
        verify(sender).sendEmail(eq(subject),
                eq(email),
                eq(from),
                eq(template),
                eq(encode),
                eq(model),
                eq(true));
    }

    @Test
    public void testHandleUserWithdrawPasswordChanged() throws Exception {
        final UserId userId = new UserId();
        final String subject = "Your withdraw password has been changed";
        final String from = "admin@icoin.com";
        final String template = "listener/withdraw-password-changed.vm";
        final String encode = "utf-8";
        final String operatingIp = "207.45.15.2";
        final String password = "new password";
        final String username = "buyerAbc";
        final String email = "buyerAbc@163.com";
        final Date time = currentTime();

        final Map<String, Object> model =
                ImmutableMap.of("username", (Object) username,
                        "ip", operatingIp,
                        "time", time);

        final VelocityEmailSender sender = mock(VelocityEmailSender.class);
        doNothing().when(sender).sendEmail(eq(subject),
                eq(email),
                eq(from),
                eq(template),
                eq(encode),
                eq(model),
                eq(true));

        final UserQueryRepository repository = mock(UserQueryRepository.class);
        UserEntry user = new UserEntry();
        user.setWithdrawPasswordAlert(false);
        user.setEmail(email);
        user.setUsername(username);
        when(repository.findOne(eq(userId.toString()))).thenReturn(null, user, user);

        final WithdrawPasswordChangedEvent event = new WithdrawPasswordChangedEvent(userId,
                username,
                email,
                password,
                password,
                operatingIp,
                time);

        final UserActivityListener listener = new UserActivityListener();
        listener.setUserRepository(repository);
        listener.setSender(sender);

        listener.handle(event);
        listener.handle(event);
        user.setWithdrawPasswordAlert(true);
        listener.handle(event);

        verify(repository, times(3)).findOne(eq(userId.toString()));
        verify(sender).sendEmail(eq(subject),
                eq(email),
                eq(from),
                eq(template),
                eq(encode),
                eq(model),
                eq(true));
    }
}
