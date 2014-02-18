/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icoin.trading.users.application.command;

import com.homhon.core.exception.IZookeyException;
import com.icoin.trading.users.domain.PasswordResetTokenGenerator;
import com.icoin.trading.users.domain.event.UserAuthenticatedEvent;
import com.icoin.trading.users.domain.event.UserCreatedEvent;
import com.icoin.trading.users.domain.model.function.TooManyResetsException;
import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.users.domain.model.function.UserPasswordResetRepository;
import com.icoin.trading.users.domain.model.user.Identifier;
import com.icoin.trading.users.domain.model.user.User;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import com.icoin.trading.users.util.DigestUtils;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.homhon.util.Collections.isEmpty;
import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jettro Coenradie
 */
@Ignore
public class UserCommandHandlerTest {

    private FixtureConfiguration fixture;

    private UserQueryRepository userQueryRepository = mock(UserQueryRepository.class);
    UserPasswordResetRepository passwordResetRepository = mock(UserPasswordResetRepository.class);

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private Date current = currentTime();

    @Before
    public void setUp() {
        userQueryRepository = Mockito.mock(UserQueryRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        fixture = Fixtures.newGivenWhenThenFixture(User.class);
        UserCommandHandler commandHandler = new UserCommandHandler();
        commandHandler.setRepository(fixture.getRepository());
        commandHandler.setUserRepository(userQueryRepository);
        commandHandler.setPasswordEncoder(passwordEncoder);
        fixture.registerAnnotatedCommandHandler(commandHandler);
    }


    @Test
    public void testHandleCreateUser() throws Exception {
        UserId aggregateIdentifier = new UserId();
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");

        fixture.given()
                .when(new CreateUserCommand(aggregateIdentifier,
                        "Buyer 1",
                        "Mr",
                        "Buyer One",
                        identifier,
                        "buyer1@163.com",
                        "buyer1",
                        "buyer1",
                        UserCommandHandler.DEFAULT_ROLES))
                .expectEvents(new UserCreatedEvent(aggregateIdentifier,
                        "Buyer 1",
                        "Mr",
                        "Buyer One",
                        identifier,
                        "buyer1@163.com",
                        DigestUtils.sha1("buyer1"),
                        UserCommandHandler.DEFAULT_ROLES));
    }

    @Test
    public void testHandleAuthenticateUser() throws Exception {
        UserId aggregateIdentifier = new UserId();
        final Identifier identifier = new Identifier(Identifier.Type.IDENTITY_CARD, "110101201101019252");

        UserEntry userEntry = new UserEntry();
        userEntry.setUsername("buyer1");
        userEntry.setPrimaryKey(aggregateIdentifier.toString());
        userEntry.setLastName("Buyer One");
        userEntry.setFirstName("Mr");
        userEntry.setIdentifier(identifier);
        Mockito.when(userQueryRepository.findByUsername("buyer1")).thenReturn(userEntry);

        fixture.given(new UserCreatedEvent(aggregateIdentifier,
                "Buyer 1",
                "Mr",
                "Buyer One",
                identifier,
                "buyer1@163.com",
                DigestUtils.sha1("buyer1"),
                UserCommandHandler.DEFAULT_ROLES))
                .when(new AuthenticateUserCommand("buyer1", "buyer1", "localhost", current))
                .expectEvents(new UserAuthenticatedEvent(aggregateIdentifier, "Buyer 1", "buyer1@163.com", "localhost", current));
    }

    @Test
    public void testHandleForgetPasswordWithUserNotFound() throws Exception {
        final Date currentTime = new Date();
        final String operatingIp = "123.12.3.1";
        final Date startDate = new Date(currentTime.getTime() - 24L * 60 * 60 * 1000L);
        final String email = "buyer1@167.hk";

        when(userQueryRepository.findByEmail(eq(email))).thenReturn(null);
        when(passwordResetRepository.findNotExpiredByEmail(eq(email), eq(startDate), eq(currentTime)));

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
    public void testHandleForgetPassword() throws Exception {
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
        when(passwordResetRepository.findNotExpiredByEmail(eq(email), eq(startDate), eq(currentTime)));


        UserCommandHandler handler = new UserCommandHandler();
        handler.setUserPasswordResetRepository(passwordResetRepository);
        handler.setUserRepository(userQueryRepository);

        ForgetPasswordCommand command = new ForgetPasswordCommand(email, operatingIp, currentTime);
        String token = handler.handleForgetPassword(command);

        assertThat(token, isEmptyString());

        verify(userQueryRepository).findByEmail(eq(email));
        verify(passwordResetRepository).findNotExpiredByEmail(eq(command.getEmail()), eq(startDate), eq(currentTime));
    }

    @Test
    public void testHandlePasswordReset() throws Exception {
        final Date currentTime = new Date();
        final String operatingIp = "123.12.3.1";
        final String email = "adfdsa@sdkj.com";
        final String username = "adfdsa";
        final Date startDate = new Date(currentTime.getTime() - 24L * 60 * 60 * 1000L);
        final UserEntry user = new UserEntry();
        user.setUsername(username);

        UserQueryRepository userRepository = mock(UserQueryRepository.class);
        when(userRepository.findByEmail(eq(email))).thenReturn(null, user);

        UserPasswordResetRepository resetRepository = mock(UserPasswordResetRepository.class);
        when(resetRepository.findNotExpiredByEmail(eq(email), eq(operatingIp), eq(startDate), eq(currentTime)))
                .thenReturn(null, Arrays.asList(new UserPasswordReset(), new UserPasswordReset()));

        PasswordResetTokenGenerator tokenGenerator = mock(PasswordResetTokenGenerator.class);
        when(tokenGenerator.generate(eq(username), eq(operatingIp), eq(currentTime)))
                .thenReturn(null, "");

        UserCommandHandler handler = new UserCommandHandler();

        handler.setUserPasswordResetRepository(resetRepository);
        handler.setUserRepository(userRepository);
        handler.setPasswordResetTokenGenerator(tokenGenerator);

        String token1 = handler.handleForgetPassword(new ForgetPasswordCommand(email, operatingIp, currentTime));
        String token2 = handler.handleForgetPassword(new ForgetPasswordCommand(email, operatingIp, currentTime));
        String token3 = handler.handleForgetPassword(new ForgetPasswordCommand(email, operatingIp, currentTime));

        assertThat(token1, isEmptyOrNullString());
        assertThat(token2, not(isEmptyOrNullString()));
        assertThat(token3, not(isEmptyOrNullString()));

        ArgumentCaptor<UserPasswordReset> captor = ArgumentCaptor.forClass(UserPasswordReset.class);
        verify(resetRepository).save(captor.capture());

        List<UserPasswordReset> resets = captor.getAllValues();

        assertThat(resets, hasSize(2));

    }

    @Test
    public void testHandlePasswordCannotGenerated() throws Exception {
        final Date currentTime = new Date();
        final String operatingIp = "123.12.3.1";
        final String email = "adfdsa@sdkj.com";
        final String username = "adfdsa";
        final String sameToken = "sameToken";

        final Date startDate = new Date(currentTime.getTime() - 24L * 60 * 60 * 1000L);

        final UserEntry user = new UserEntry();
        user.setUsername(username);

        UserQueryRepository userRepository = mock(UserQueryRepository.class);
        when(userRepository.findByEmail(eq(email))).thenReturn(user);

        UserPasswordResetRepository resetRepository = mock(UserPasswordResetRepository.class);
        when(resetRepository.findNotExpiredByEmail(eq(email), eq(operatingIp), eq(startDate), eq(currentTime)))
                .thenReturn(null, Arrays.asList(new UserPasswordReset(), new UserPasswordReset()));

        UserPasswordReset reset = new UserPasswordReset();
        when(resetRepository.findByToken(eq(sameToken))).thenReturn(reset);

        PasswordResetTokenGenerator tokenGenerator = mock(PasswordResetTokenGenerator.class);
        when(tokenGenerator.generate(eq(username), eq(operatingIp), eq(currentTime)))
                .thenReturn(sameToken, sameToken, sameToken, sameToken, sameToken);

        UserCommandHandler handler = new UserCommandHandler();

        handler.setUserPasswordResetRepository(resetRepository);
        handler.setUserRepository(userRepository);
        handler.setPasswordResetTokenGenerator(tokenGenerator);

        boolean hasExeption = false;
        try {
            handler.handleForgetPassword(new ForgetPasswordCommand(email, operatingIp, currentTime));
        } catch (IZookeyException e) {
            hasExeption = true;
        }

        verify(resetRepository, times(5)).findByToken(eq(sameToken));

        assertThat(hasExeption, is(true));
    }

    @Test(expected = TooManyResetsException.class)
    public void testHandlePasswordResetWithException() throws Exception {
        final Date currentTime = new Date();
        final String operatingIp = "123.12.3.1";
        final String email = "adfdsa@sdkj.com";

        final Date startDate = new Date(currentTime.getTime() - 24L * 60 * 60 * 1000L);

        final UserEntry user = new UserEntry();

        UserQueryRepository userRepository = mock(UserQueryRepository.class);
        when(userRepository.findByEmail(eq(email))).thenReturn(user);

        UserPasswordResetRepository resetRepository = mock(UserPasswordResetRepository.class);
        when(resetRepository.findNotExpiredByEmail(eq(email), eq(operatingIp), eq(startDate), eq(currentTime)))
                .thenReturn(Arrays.asList(new UserPasswordReset(), new UserPasswordReset(), new UserPasswordReset()));

        UserCommandHandler handler = new UserCommandHandler();
        handler.setUserPasswordResetRepository(resetRepository);
        handler.setUserRepository(userRepository);

        handler.handleForgetPassword(new ForgetPasswordCommand(email, operatingIp, currentTime));
    }

    @Test
    public void testHandleChangePassword() throws Exception {

    }

    @Test
    public void testHandleChangeWithdrawPasswordCommand() throws Exception {

    }
}
