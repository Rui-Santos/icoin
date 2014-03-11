package com.icoin.trading.users.application.command;

import com.google.common.collect.ImmutableList;
import com.homhon.core.exception.IZookeyException;
import com.icoin.trading.users.domain.PasswordResetTokenGenerator;
import com.icoin.trading.users.domain.event.NotificationSettingsUpdatedEvent;
import com.icoin.trading.users.domain.event.PasswordChangedEvent;
import com.icoin.trading.users.domain.event.UserAdminInfoChangedEvent;
import com.icoin.trading.users.domain.event.UserAuthenticatedEvent;
import com.icoin.trading.users.domain.event.UserCreatedEvent;
import com.icoin.trading.users.domain.event.UserInfoChangedEvent;
import com.icoin.trading.users.domain.event.WithdrawPasswordChangedEvent;
import com.icoin.trading.users.domain.event.WithdrawPasswordCreatedEvent;
import com.icoin.trading.users.domain.model.function.TooManyResetsException;
import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.users.domain.model.function.UserPasswordResetRepository;
import com.icoin.trading.users.domain.model.user.Identifier;
import com.icoin.trading.users.domain.model.user.User;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.axonframework.test.matchers.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;

import static com.homhon.util.TimeUtils.currentTime;
import static com.homhon.util.TimeUtils.futureMinute;
import static org.axonframework.test.matchers.Matchers.sequenceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jettro Coenradie
 */

public class UserCommandHandlerTest {

    private FixtureConfiguration fixture;
    private UserCommandHandler handler = new UserCommandHandler();

    private UserQueryRepository userQueryRepository = mock(UserQueryRepository.class);

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UserPasswordResetRepository passwordResetRepository = mock(UserPasswordResetRepository.class);
    private Date current = currentTime();

    @Before
    public void setUp() {
        userQueryRepository = Mockito.mock(UserQueryRepository.class);
        fixture = Fixtures.newGivenWhenThenFixture(User.class);

        handler.setRepository(fixture.getRepository());
        handler.setUserRepository(userQueryRepository);
        handler.setPasswordEncoder(passwordEncoder);
        handler.setUserPasswordResetRepository(passwordResetRepository);
        fixture.registerAnnotatedCommandHandler(handler);
    }


    @Test
    public void testHandleCreateUser() throws Exception {
        UserId aggregateIdentifier = new UserId();
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");

        final Date time = currentTime();
        fixture.given()
                .when(new CreateUserCommand(aggregateIdentifier,
                        "buyer1",
                        "Mr",
                        "Buyer One",
                        identifier,
                        "buyer1@163.com",
                        "buyer1",
                        "buyer1",
                        UserCommandHandler.DEFAULT_ROLES,
                        time))
                .expectEventsMatching(
                        Matchers.payloadsMatching(
                                sequenceOf(new UserCreatedEventMatcher(new UserCreatedEvent(aggregateIdentifier,
                                        "buyer1",
                                        "Mr",
                                        "Buyer One",
                                        identifier,
                                        "buyer1@163.com",
                                        passwordEncoder.encode("buyer1"),
                                        UserCommandHandler.DEFAULT_ROLES,
                                        time),
                                        passwordEncoder,
                                        "buyer1"
                                ))));
    }

    @Test
    public void testHandleAuthenticateUser() throws Exception {
        UserId aggregateIdentifier = new UserId();
        final Date time = currentTime();
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");

        UserEntry userEntry = new UserEntry();
        userEntry.setUsername("buyer1");
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        when(userQueryRepository.findByUsername("buyer1")).thenReturn(userEntry);

        fixture.given(
                new UserCreatedEvent(
                        aggregateIdentifier,
                        "buyer1",
                        "Mr",
                        "Buyer One",
                        identifier,
                        "buyer1@163.com",
                        passwordEncoder.encode("buyer1"),
                        UserCommandHandler.DEFAULT_ROLES,
                        time))
                .when(new AuthenticateUserCommand("buyer1", "buyer1", "localhost", current))
                .expectEvents(new UserAuthenticatedEvent(aggregateIdentifier, "buyer1", "buyer1@163.com", "localhost", current));
    }

    @Test
    public void testHandleForgetPasswordWithUserNotFound() throws Exception {
        final Date currentTime = new Date();
        final String operatingIp = "123.12.3.1";
        final Date startDate = new Date(currentTime.getTime() - 24L * 60 * 60 * 1000L);
        final String email = "buyer1@167.hk";

        when(userQueryRepository.findByEmail(eq(email))).thenReturn(null);

        UserCommandHandler handler = new UserCommandHandler();
        handler.setUserPasswordResetRepository(passwordResetRepository);
        handler.setUserRepository(userQueryRepository);

        ForgetPasswordCommand command = new ForgetPasswordCommand(email, operatingIp, currentTime);
        String token = handler.handleForgetPassword(command);

        assertThat(token, isEmptyString());

        verify(userQueryRepository).findByEmail(eq(email));
        verify(passwordResetRepository, never()).findNotExpiredByEmail(eq(command.getEmail()), eq(startDate), eq(currentTime));
    }

    @Test
    public void testHandleForgetPasswordWith3TimesSameIp() throws Exception {
        final Date currentTime = new Date();
        final String operatingIp = "123.12.3.1";
        final String username = "adfdsa";
        final Date startDate = new Date(currentTime.getTime() - 24L * 60 * 60 * 1000L);
        final String email = "buyer1@167.hk";
        UserId aggregateIdentifier = new UserId();
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");


        UserEntry userEntry = new UserEntry();
        userEntry.setUsername("buyer1");
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        userEntry.setEmail(email);

        when(userQueryRepository.findByEmail(eq(email))).thenReturn(userEntry);
        when(passwordResetRepository.findNotExpiredByEmail(eq(email), eq(operatingIp), eq(startDate), eq(currentTime)))
                .thenReturn(ImmutableList.of(new UserPasswordReset(), new UserPasswordReset(), new UserPasswordReset()));


        UserCommandHandler handler = new UserCommandHandler();
        handler.setUserPasswordResetRepository(passwordResetRepository);
        handler.setUserRepository(userQueryRepository);

        ForgetPasswordCommand command = new ForgetPasswordCommand(email, operatingIp, currentTime);

        String token = null;
        boolean hasException = false;
        try {
            token = handler.handleForgetPassword(command);
        } catch (TooManyResetsException e) {
            hasException = true;
        }

        assertThat(token, nullValue());
        assertThat(hasException, is(true));

        verify(userQueryRepository).findByEmail(eq(email));
        verify(passwordResetRepository).findNotExpiredByEmail(eq(command.getEmail()), eq(operatingIp), eq(startDate), eq(currentTime));
    }

    @Test
    public void testHandleForgetPasswordWith6TimesDifferentIps() throws Exception {
        final Date currentTime = new Date();
        final String operatingIp = "123.12.3.1";
        final String username = "adfdsa";
        final Date startDate = new Date(currentTime.getTime() - 24L * 60 * 60 * 1000L);
        final String email = "buyer1@167.hk";
        UserId aggregateIdentifier = new UserId();
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");


        UserEntry userEntry = new UserEntry();
        userEntry.setUsername("buyer1");
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        userEntry.setEmail(email);

        when(userQueryRepository.findByEmail(eq(email))).thenReturn(userEntry);
        when(passwordResetRepository.findNotExpiredByEmail(eq(email), eq(operatingIp), eq(startDate), eq(currentTime)))
                .thenReturn(ImmutableList.of(new UserPasswordReset(), new UserPasswordReset()));

        when(passwordResetRepository.findNotExpiredByEmail(eq(email), eq(startDate), eq(currentTime)))
                .thenReturn(ImmutableList.of(new UserPasswordReset(), new UserPasswordReset(), new UserPasswordReset(), new UserPasswordReset(), new UserPasswordReset(), new UserPasswordReset()));


        UserCommandHandler handler = new UserCommandHandler();
        handler.setUserPasswordResetRepository(passwordResetRepository);
        handler.setUserRepository(userQueryRepository);

        ForgetPasswordCommand command = new ForgetPasswordCommand(email, operatingIp, currentTime);

        String token = null;
        boolean hasException = false;
        try {
            token = handler.handleForgetPassword(command);
        } catch (TooManyResetsException e) {
            hasException = true;
        }

        assertThat(token, nullValue());
        assertThat(hasException, is(true));

        verify(userQueryRepository).findByEmail(eq(email));
        verify(passwordResetRepository).findNotExpiredByEmail(eq(command.getEmail()), eq(operatingIp), eq(startDate), eq(currentTime));
        verify(passwordResetRepository).findNotExpiredByEmail(eq(command.getEmail()), eq(startDate), eq(currentTime));
    }

    @Test
    public void testHandleForgetPasswordWithTooManyFailedGeneration() throws Exception {
        final Date currentTime = new Date();
        final String operatingIp = "123.12.3.1";
        final String username = "adfdsa";
        final Date startDate = new Date(currentTime.getTime() - 24L * 60 * 60 * 1000L);
        final String email = "buyer1@167.hk";
        UserId aggregateIdentifier = new UserId();
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");
        String generatedToken = "avsdfsad";


        UserEntry userEntry = new UserEntry();
        userEntry.setUsername(username);
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        userEntry.setEmail(email);

        when(userQueryRepository.findByEmail(eq(email))).thenReturn(userEntry);
        when(passwordResetRepository.findNotExpiredByEmail(eq(email), eq(operatingIp), eq(startDate), eq(currentTime)))
                .thenReturn(null);

        when(passwordResetRepository.findNotExpiredByEmail(eq(email), eq(startDate), eq(currentTime)))
                .thenReturn(null);
        when(passwordResetRepository.findByToken(generatedToken)).thenReturn(new UserPasswordReset());

        PasswordResetTokenGenerator tokenGenerator = mock(PasswordResetTokenGenerator.class);
        when(tokenGenerator.generate(eq(username), eq(operatingIp), eq(currentTime)))
                .thenReturn(generatedToken);

        ForgetPasswordCommand command = new ForgetPasswordCommand(email, operatingIp, currentTime);
        handler.setPasswordResetTokenGenerator(tokenGenerator);

        String token = null;
        boolean hasException = false;
        try {
            token = handler.handleForgetPassword(command);
        } catch (IZookeyException e) {
            hasException = true;
        }

        assertThat(token, nullValue());
        assertThat(hasException, is(true));

        verify(passwordResetRepository).findNotExpiredByEmail(eq(command.getEmail()), eq(operatingIp), eq(startDate), eq(currentTime));
        verify(passwordResetRepository).findNotExpiredByEmail(eq(command.getEmail()), eq(startDate), eq(currentTime));
        verify(tokenGenerator, times(5)).generate(eq(username), eq(operatingIp), eq(currentTime));
    }

    @Test
    public void testHandleForgetPassword() throws Exception {
        final Date currentTime = new Date();
        final String operatingIp = "123.12.3.1";
        final String username = "ewrwe";
        final Date startDate = new Date(currentTime.getTime() - 24L * 60 * 60 * 1000L);
        final String email = "buyer1@167.hk";
        UserId aggregateIdentifier = new UserId();
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");
        String generatedToken = "avsdfsad";


        UserEntry userEntry = new UserEntry();
        userEntry.setUsername(username);
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        userEntry.setEmail(email);

        when(userQueryRepository.findByEmail(eq(email))).thenReturn(userEntry);
        when(passwordResetRepository.findNotExpiredByEmail(eq(email), eq(operatingIp), eq(startDate), eq(currentTime)))
                .thenReturn(ImmutableList.of(new UserPasswordReset(), new UserPasswordReset()));

        when(passwordResetRepository.findNotExpiredByEmail(eq(email), eq(startDate), eq(currentTime)))
                .thenReturn(ImmutableList.of(new UserPasswordReset(), new UserPasswordReset(), new UserPasswordReset(), new UserPasswordReset()));
        when(passwordResetRepository.findByToken(eq(generatedToken))).thenReturn(new UserPasswordReset(), null);

        PasswordResetTokenGenerator tokenGenerator = mock(PasswordResetTokenGenerator.class);
        when(tokenGenerator.generate(eq(username), eq(operatingIp), eq(currentTime)))
                .thenReturn(generatedToken);

        ForgetPasswordCommand command = new ForgetPasswordCommand(email, operatingIp, currentTime);
        handler.setPasswordResetTokenGenerator(tokenGenerator);

        String token = handler.handleForgetPassword(command);

        assertThat(token, equalTo(generatedToken));

        ArgumentCaptor<UserPasswordReset> captor = ArgumentCaptor.forClass(UserPasswordReset.class);
        verify(passwordResetRepository).save(captor.capture());

        UserPasswordReset reset = captor.getValue();
        assertThat(reset, notNullValue());

        assertThat(reset.getToken(), equalTo(generatedToken));
        assertThat(reset.getUsername(), equalTo(username));
        assertThat(reset.getUserId(), equalTo(aggregateIdentifier.toString()));
        assertThat(reset.getEmail(), equalTo(email));
        assertThat(reset.getIp(), equalTo(operatingIp));
        assertThat(reset.getExpirationDate(), equalTo(futureMinute(currentTime, 30)));

        verify(passwordResetRepository).findNotExpiredByEmail(eq(command.getEmail()), eq(operatingIp), eq(startDate), eq(currentTime));
        verify(passwordResetRepository).findNotExpiredByEmail(eq(command.getEmail()), eq(startDate), eq(currentTime));
        verify(tokenGenerator, times(2)).generate(eq(username), eq(operatingIp), eq(currentTime));
    }


    @Test
    public void testHandleChangePassword() throws Exception {
        UserId aggregateIdentifier = new UserId();
        final Date time = currentTime();
        final String operatingIp = "223.124.18.123";
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");
        Date changedTime = currentTime();

        UserEntry userEntry = new UserEntry();
        userEntry.setUsername("buyer1");
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        when(userQueryRepository.findByUsername("buyer1")).thenReturn(userEntry);

        String newPassword = "newPassword";
        ChangePasswordCommand command = new ChangePasswordCommand(aggregateIdentifier,
                "buyer1",
                "buyer1",
                newPassword,
                newPassword,
                operatingIp,
                changedTime);

        fixture.given(new UserCreatedEvent(aggregateIdentifier,
                "buyer1",
                "Mr",
                "Buyer One",
                identifier,
                "buyer1@163.com",
                passwordEncoder.encode("buyer1"),
                UserCommandHandler.DEFAULT_ROLES,
                time))
                .when(command)
                .expectEventsMatching(
                        Matchers.payloadsMatching(
                                sequenceOf(new PasswordChangedEventMatcher<PasswordChangedEvent>(
                                        new PasswordChangedEvent(aggregateIdentifier,
                                                "buyer1",
                                                "buyer1@163.com",
                                                passwordEncoder.encode("buyer1"),
                                                passwordEncoder.encode("buyer1"),
                                                operatingIp,
                                                changedTime),
                                        passwordEncoder,
                                        "buyer1"))));
    }

    @Test
    public void testHandlePasswordResetWithNotFoundToken() throws Exception {
        UserId aggregateIdentifier = new UserId();
        final Date time = currentTime();
        final String operatingIp = "223.124.18.123";
        final String email = "buyer1@163.com";
        final String username = "buyer1";
        final String generatedToken = "sdfoasop932723l3i47895df";
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");
        Date changedTime = currentTime();

        UserEntry userEntry = new UserEntry();
        userEntry.setUsername("buyer1");
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        when(userQueryRepository.findByUsername("buyer1")).thenReturn(userEntry);

        UserPasswordReset userPasswordReset = new UserPasswordReset();
        userPasswordReset.setUsername(generatedToken);
        userPasswordReset.setUserId(identifier.toString());

        List<UserPasswordReset> listToDelete = ImmutableList.of(new UserPasswordReset(), new UserPasswordReset());
        when(passwordResetRepository.findByUsername(eq(username))).thenReturn(listToDelete);

        String newPassword = passwordEncoder.encode("newPassword");
        ResetPasswordCommand command = new ResetPasswordCommand(generatedToken,
                newPassword,
                newPassword,
                operatingIp,
                changedTime);


        fixture.given(new UserCreatedEvent(aggregateIdentifier,
                username,
                "Mr",
                "Buyer One",
                identifier,
                email,
                passwordEncoder.encode("buyer1"),
                UserCommandHandler.DEFAULT_ROLES,
                time))
                .when(command)
                .expectEvents();

        verify(passwordResetRepository).findByToken(eq(generatedToken));
        verify(passwordResetRepository, never()).findByUsername(eq(username));
        verify(passwordResetRepository, never()).delete(eq(listToDelete));
    }

    @Test
    public void testHandlePasswordReset() throws Exception {
        final UserId aggregateIdentifier = new UserId();
        final Date time = currentTime();
        final String operatingIp = "223.124.18.123";
        final String email = "buyer1@163.com";
        final String username = "buyer1";
        final String generatedToken = "sdfoasop932723l3i47895df";
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");
        final Date changedTime = currentTime();

        UserEntry userEntry = new UserEntry();
        userEntry.setUsername("buyer1");
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        when(userQueryRepository.findByUsername("buyer1")).thenReturn(userEntry);

        UserPasswordReset userPasswordReset = new UserPasswordReset();
        userPasswordReset.setUsername(username);
        userPasswordReset.setUserId(aggregateIdentifier.toString());
        userPasswordReset.setExpirationDate(futureMinute(currentTime(), 10));

        when(passwordResetRepository.findByToken(eq(generatedToken))).thenReturn(userPasswordReset);
        List<UserPasswordReset> listToDelete = ImmutableList.of(new UserPasswordReset(), new UserPasswordReset());
        when(passwordResetRepository.findByUsername(eq(username))).thenReturn(listToDelete);

        String newPassword = passwordEncoder.encode("newPassword");
        ResetPasswordCommand command = new ResetPasswordCommand(generatedToken,
                newPassword,
                newPassword,
                operatingIp,
                changedTime);

        fixture.given(new UserCreatedEvent(aggregateIdentifier,
                username,
                "Mr",
                "Buyer One",
                identifier,
                email,
                passwordEncoder.encode("buyer1"),
                UserCommandHandler.DEFAULT_ROLES,
                time))
                .when(command)
                .expectEventsMatching(
                        Matchers.payloadsMatching(
                                sequenceOf(new PasswordChangedEventMatcher<PasswordChangedEvent>(
                                        new PasswordChangedEvent(aggregateIdentifier,
                                                username,
                                                "buyer1@163.com",
                                                passwordEncoder.encode("buyer1"),
                                                passwordEncoder.encode("buyer1"),
                                                operatingIp,
                                                changedTime),
                                        passwordEncoder,
                                        "buyer1"))));

        verify(passwordResetRepository).findByToken(eq(generatedToken));
        verify(passwordResetRepository).findByUsername(eq(username));
        verify(passwordResetRepository).delete(eq(listToDelete));
    }

    @Test
    public void testHandleCreateWithdrawPasswordCommand() throws Exception {
        UserId aggregateIdentifier = new UserId();
        final Date time = currentTime();
        final String operatingIp = "223.124.18.123";
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");
        Date changedTime = currentTime();

        UserEntry userEntry = new UserEntry();
        userEntry.setUsername("buyer1");
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        when(userQueryRepository.findByUsername("buyer1")).thenReturn(userEntry);

        String newPassword = passwordEncoder.encode("newPassword");
        CreateWithdrawPasswordCommand command = new CreateWithdrawPasswordCommand(aggregateIdentifier,
                "buyer1",
                newPassword,
                newPassword,
                operatingIp,
                changedTime);

        fixture.given(new UserCreatedEvent(aggregateIdentifier,
                "buyer1",
                "Mr",
                "Buyer One",
                identifier,
                "buyer1@163.com",
                passwordEncoder.encode("buyer1"),
                UserCommandHandler.DEFAULT_ROLES,
                time))
                .when(command)
                .expectEventsMatching(
                        Matchers.payloadsMatching(
                                sequenceOf(new PasswordChangedEventMatcher<WithdrawPasswordCreatedEvent>(
                                        new WithdrawPasswordCreatedEvent(aggregateIdentifier,
                                                "buyer1",
                                                "buyer1@163.com",
                                                passwordEncoder.encode("buyer1"),
                                                passwordEncoder.encode("buyer1"),
                                                operatingIp,
                                                changedTime),
                                        passwordEncoder,
                                        "buyer1"))));
    }

    @Test
    public void testHandleChangeWithdrawPasswordCommand() throws Exception {
        UserId aggregateIdentifier = new UserId();
        final Date time = currentTime();
        final String operatingIp = "223.124.18.123";
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");
        Date changedTime = currentTime();

        UserEntry userEntry = new UserEntry();
        userEntry.setUsername("buyer1");
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        when(userQueryRepository.findByUsername("buyer1")).thenReturn(userEntry);

        String newPassword = passwordEncoder.encode("newPassword");
        ChangeWithdrawPasswordCommand command = new ChangeWithdrawPasswordCommand(aggregateIdentifier,
                "buyer1",
                passwordEncoder.encode("buyer1"),
                newPassword,
                newPassword,
                operatingIp,
                changedTime);

        fixture.given(new UserCreatedEvent(aggregateIdentifier,
                "buyer1",
                "Mr",
                "Buyer One",
                identifier,
                "buyer1@163.com",
                passwordEncoder.encode("buyer1"),
                UserCommandHandler.DEFAULT_ROLES,
                time))
                .when(command)
                .expectEventsMatching(
                        Matchers.payloadsMatching(
                                sequenceOf(new PasswordChangedEventMatcher<WithdrawPasswordChangedEvent>(
                                        new WithdrawPasswordChangedEvent(aggregateIdentifier,
                                                "buyer1",
                                                "buyer1@163.com",
                                                passwordEncoder.encode("buyer1"),
                                                passwordEncoder.encode("buyer1"),
                                                operatingIp,
                                                changedTime),
                                        passwordEncoder,
                                        "buyer1"))));
    }

    @Test
    public void testHandleUpdateNotificationCommand() {
        UserId aggregateIdentifier = new UserId();
        final Date time = currentTime();
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");

        UserEntry userEntry = new UserEntry();
        userEntry.setUsername("buyer1");
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        when(userQueryRepository.findByUsername("buyer1")).thenReturn(userEntry);

        UpdateNotificationSettingsCommand command = new UpdateNotificationSettingsCommand(aggregateIdentifier,
                "buyer1",
                true,
                false,
                true,
                false);

        fixture.given(new UserCreatedEvent(aggregateIdentifier,
                "buyer1",
                "Mr",
                "Buyer One",
                identifier,
                "buyer1@163.com",
                passwordEncoder.encode("buyer1"),
                UserCommandHandler.DEFAULT_ROLES,
                time))
                .when(command)
                .expectEvents(
                        new NotificationSettingsUpdatedEvent(
                                aggregateIdentifier,
                                "buyer1",
                                true,
                                false,
                                true,
                                false));
    }

    @Test
    public void testHandleChangeInfoCommand() {
        final UserId aggregateIdentifier = new UserId();
        final Date time = currentTime();
        final String email = "buyer1@163.com";
        final String username = "buyer1";
        final String phone = "13232321";
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");

        UserEntry userEntry = new UserEntry();
        userEntry.setUsername(username);
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        when(userQueryRepository.findByUsername("buyer1")).thenReturn(userEntry);

        ChangeInfoCommand command = new ChangeInfoCommand(aggregateIdentifier,
                username,
                email,
                phone,
                "Mr",
                "Buyer One");

        fixture.given(new UserCreatedEvent(aggregateIdentifier,
                "buyer1",
                "Mr",
                "Buyer One",
                identifier,
                "buyer1@163.com",
                passwordEncoder.encode("buyer1"),
                UserCommandHandler.DEFAULT_ROLES,
                time))
                .when(command)
                .expectEvents(
                        new UserInfoChangedEvent(
                                aggregateIdentifier,
                                username,
                                email,
                                phone,
                                "Mr",
                                "Buyer One"));
    }

    @Test
    public void testHandleChangeAdminInfoCommand() {
        final UserId aggregateIdentifier = new UserId();
        final Date time = currentTime();
        final String email = "buyer1@163.com";
        final String username = "buyer1";
        final String phone = "13232321";
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");
        final List<String> roles = ImmutableList.of("user", "admin");

        UserEntry userEntry = new UserEntry();
        userEntry.setUsername(username);
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        when(userQueryRepository.findByUsername("buyer1")).thenReturn(userEntry);

        ChangeAdminInfoCommand command = new ChangeAdminInfoCommand(aggregateIdentifier,
                username,
                email,
                identifier,
                phone,
                "Mr",
                "Buyer One",
                roles);

        fixture.given(new UserCreatedEvent(aggregateIdentifier,
                "buyer1",
                "Mr",
                "Buyer One",
                identifier,
                email,
                passwordEncoder.encode("buyer1"),
                UserCommandHandler.DEFAULT_ROLES,
                time))
                .when(command)
                .expectEvents(
                        new UserAdminInfoChangedEvent(
                                aggregateIdentifier,
                                username,
                                email,
                                identifier,
                                phone,
                                "Mr",
                                "Buyer One",
                                roles));
    }
}
