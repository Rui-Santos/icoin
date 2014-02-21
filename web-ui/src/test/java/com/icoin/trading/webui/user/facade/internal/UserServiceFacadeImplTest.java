package com.icoin.trading.webui.user.facade.internal;

import com.homhon.base.domain.service.UserService;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.users.application.command.ChangePasswordCommand;
import com.icoin.trading.users.application.command.ChangeWithdrawPasswordCommand;
import com.icoin.trading.users.application.command.CreateWithdrawPasswordCommand;
import com.icoin.trading.users.application.command.ForgetPasswordCommand;
import com.icoin.trading.users.application.command.ResetPasswordCommand;
import com.icoin.trading.users.domain.ForgetPasswordEmailSender;
import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.users.domain.model.function.UserPasswordResetRepository;
import com.icoin.trading.users.domain.model.user.UserAccount;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.homhon.util.TimeUtils.currentTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/13/14
 * Time: 6:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserServiceFacadeImplTest {
    private String username = "test";
    private UserId userId;
    private UserEntry user;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void testCurrentUser() throws Exception {
        UserService userService = mockUserService();

        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserService(userService);

        UserAccount userAccount = service.currentUser();

        assertThat(userAccount, (Matcher) equalTo(user));

        verify(userService).getCurrentUser();
    }

    private UserService mockUserService() {
        UserService userService = mock(UserService.class);

        user = new UserEntry();
        userId = new UserId();
        user.setPrimaryKey(userId.toString());
        user.setUsername(username);
        when(userService.getCurrentUser()).thenReturn(user);

        return userService;
    }

    @Test
    public void testCurrentDetailUser() throws Exception {
        UserService userService = mockUserService();

        UserQueryRepository userRepository = mock(UserQueryRepository.class);
        when(userRepository.findByUsername(eq(username))).thenReturn(user);

        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserService(userService);
        service.setUserRepository(userRepository);

        service.currentDetailUser();

        verify(userRepository).findByUsername(eq(username));
        verify(userService).getCurrentUser();
    }

    @Test
    public void testIsWithdrawPasswordSet() throws Exception {
        UserService userService = mockUserService();

        UserQueryRepository userRepository = mock(UserQueryRepository.class);
        when(userRepository.findByUsername(eq(username))).thenReturn(user);

        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserService(userService);
        service.setUserRepository(userRepository);

        user.setWithdrawPassword(null);
        boolean set1 = service.isWithdrawPasswordSet();
        user.setWithdrawPassword("withdraw password");
        boolean set2 = service.isWithdrawPasswordSet();

        assertThat(set1, is(false));
        assertThat(set2, is(true));

        verify(userRepository, times(2)).findByUsername(eq(username));
        verify(userService, times(2)).getCurrentUser();
    }


    @Test
    public void testObtainPortfolioForUser() throws Exception {
        UserService userService = mockUserService();

        UserQueryRepository userRepository = mock(UserQueryRepository.class);
        when(userRepository.findByUsername(eq(username))).thenReturn(user);

        final PortfolioEntry portfolio = new PortfolioEntry();
        PortfolioQueryRepository portfolioQueryRepository = mock(PortfolioQueryRepository.class);
        when(portfolioQueryRepository.findByUserIdentifier(eq(userId.toString()))).thenReturn(portfolio);

        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserService(userService);
        service.setUserRepository(userRepository);
        service.setPortfolioQueryRepository(portfolioQueryRepository);

        service.obtainPortfolioForUser();

        verify(userRepository).findByUsername(eq(username));
        verify(portfolioQueryRepository).findByUserIdentifier(eq(userId.toString()));
        verify(userService).getCurrentUser();
    }

    @Test
    public void testMatchPreviousPassword() throws Exception {
        final String previousPassword = "previousPassword";

        UserService userService = mockUserService();

        UserQueryRepository userRepository = mock(UserQueryRepository.class);
        when(userRepository.findByUsername(eq(username))).thenReturn(user);

        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserService(userService);
        service.setUserRepository(userRepository);
        service.setPasswordEncoder(passwordEncoder);

        user.setPassword(passwordEncoder.encode(previousPassword));

        boolean matched = service.matchPreviousPassword(previousPassword);
        boolean unmatched = service.matchPreviousPassword(previousPassword+1);

        assertThat(matched, is(true));
        assertThat(unmatched, is(false));

        verify(userService, times(2)).getCurrentUser();
        verify(userRepository, times(2)).findByUsername(eq(username));
    }

    @Test
    public void testChangePassword() throws Exception {
        final Date date = currentTime();
        final String previousPassword = "previousPassword";
        final String newPassword = "newPassword";
        final String confirmedNewPassword = "newPassword";
        final String operatingIp = "operatingIp";

        UserService userService = mockUserService();
        final ChangePasswordCommand command =
                new ChangePasswordCommand(
                        userId,
                        username,
                        previousPassword,
                        newPassword,
                        confirmedNewPassword,
                        operatingIp,
                        date);

        CommandGateway gateway = mock(CommandGateway.class);
        doNothing().when(gateway).send(eq(command));

        UserQueryRepository userRepository = mock(UserQueryRepository.class);
        when(userRepository.findByUsername(eq(username))).thenReturn(user);

        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserService(userService);
        service.setCommandGateway(gateway);
        service.setUserRepository(userRepository);
        service.setPasswordEncoder(passwordEncoder);

        user.setPassword(passwordEncoder.encode(previousPassword));
        service.changePassword(previousPassword, newPassword, confirmedNewPassword, operatingIp, date);
        user.setPassword(passwordEncoder.encode(previousPassword + 1));
        service.changePassword(previousPassword, newPassword, confirmedNewPassword, operatingIp, date);


        verify(userService ,times(4)).getCurrentUser();
        verify(userRepository, times(2)).findByUsername(eq(username));
        verify(gateway).send(eq(command));
    }

    @Test
    public void testGenerateForgetPasswordToken() throws Exception {
        final String emptyToken = "";
        final String token = "sewfasaa12121casf13143dc";
        final String email = "svd@163.com";
        final String operatingIp = "ip";
        final Date currentTime = new Date();

        UserService userService = mockUserService();
        UserService userService2 = mock(UserService.class);

        CommandGateway gateway = mock(CommandGateway.class);
        ForgetPasswordCommand command = new ForgetPasswordCommand(email, operatingIp, currentTime);
        when(gateway.sendAndWait(eq(command), eq(5L), eq(TimeUnit.SECONDS))).thenReturn(emptyToken, token);

        ForgetPasswordEmailSender emailSender = mock(ForgetPasswordEmailSender.class);
        doNothing().when(emailSender).sendEmail(eq(token));

        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserService(userService);
        service.setCommandGateway(gateway);
        service.setEmailSender(emailSender);

        boolean generated = service.generateForgetPasswordToken(email, operatingIp, currentTime);
        assertThat(generated, is(false));

        service.setUserService(userService2);
        generated = service.generateForgetPasswordToken(email, operatingIp, currentTime);
        assertThat(generated, is(false));

        generated = service.generateForgetPasswordToken(email, operatingIp, currentTime);
        assertThat(generated, is(true));

        verify(userService).getCurrentUser();
        verify(userService2, times(2)).getCurrentUser();
        verify(gateway, times(2)).sendAndWait(eq(command), eq(5L), eq(TimeUnit.SECONDS));
        verify(emailSender).sendEmail(eq(token));
    }

    @Test
    public void testResetPasswordWithToken() throws Exception {
        final String token = "token";
        final String ip = "ip";
        final String password = "password";
        final String confirmedPassword = "password";
        final Date date = currentTime();

        UserService userService = mockUserService();
        UserService userService2 = mock(UserService.class);

        CommandGateway gateway = mock(CommandGateway.class);
        ResetPasswordCommand command = new ResetPasswordCommand(token, password, confirmedPassword, ip, date);
//        when(gateway).sendAndWait(eq(command));


        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserService(userService);
        service.setCommandGateway(gateway);

        service.resetPasswordWithToken(token, password, confirmedPassword, ip, date);

        service.setUserService(userService2);
        service.resetPasswordWithToken(token, password, confirmedPassword, ip, date);

        verify(userService).getCurrentUser();
        verify(userService2).getCurrentUser();
        verify(gateway).sendAndWait(eq(command));
    }

    @Test
    public void testMatchPreviousWithdrawPassword() throws Exception {
        final String previousPassword = "previousPassword";

        UserService userService = mockUserService();

        UserQueryRepository userRepository = mock(UserQueryRepository.class);
        when(userRepository.findByUsername(eq(username))).thenReturn(user);

        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserService(userService);
        service.setUserRepository(userRepository);
        service.setPasswordEncoder(passwordEncoder);

        user.setWithdrawPassword(passwordEncoder.encode(previousPassword));
        boolean matched = service.matchPreviousWithdrawPassword(previousPassword);
        user.setWithdrawPassword(passwordEncoder.encode(previousPassword+12));
        boolean unmatched = service.matchPreviousWithdrawPassword(previousPassword);

        assertThat(matched, is(true));
        assertThat(unmatched, is(false));

        verify(userService, times(2)).getCurrentUser();
        verify(userRepository, times(2)).findByUsername(eq(username));
    }

    @Test
    public void testCreateWithdrawPassword() throws Exception {
        final String previousWithdrawPassword = "previousWithdrawPassword";
        final String newWithdrawPassword = "newWithdrawPassword";
        final String confirmedNewWithdrawPassword = "newWithdrawPassword";
        final String operatingIp = "operatingIp";
        final Date date = currentTime();

        UserService userService = mockUserService();

        UserQueryRepository userRepository = mock(UserQueryRepository.class);
        when(userRepository.findByUsername(eq(username))).thenReturn(user);

        final CreateWithdrawPasswordCommand command =
                new CreateWithdrawPasswordCommand(
                        userId,
                        username,
                        newWithdrawPassword,
                        confirmedNewWithdrawPassword,
                        operatingIp,
                        date);

        CommandGateway gateway = mock(CommandGateway.class);
        doNothing().when(gateway).send(eq(command));

        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserService(userService);
        service.setUserRepository(userRepository);
        service.setCommandGateway(gateway);
        service.setPasswordEncoder(passwordEncoder);


        service.createWithdrawPassword(
                newWithdrawPassword,
                confirmedNewWithdrawPassword,
                operatingIp,
                date);

        user.setWithdrawPassword(passwordEncoder.encode(previousWithdrawPassword));
        service.createWithdrawPassword(
                newWithdrawPassword,
                confirmedNewWithdrawPassword,
                operatingIp,
                date);

        verify(userService, times(2)).getCurrentUser();
        verify(userRepository, times(2)).findByUsername(eq(username));
        verify(gateway).send(eq(command));
    }

    @Test
    public void testChangeWithdrawPassword() throws Exception {
        final String previousWithdrawPassword = "previousWithdrawPassword";
        final String newWithdrawPassword = "newWithdrawPassword";
        final String confirmedNewWithdrawPassword = "newWithdrawPassword";
        final String operatingIp = "operatingIp";
        final Date date = currentTime();

        UserService userService = mockUserService();

        UserQueryRepository userRepository = mock(UserQueryRepository.class);
        when(userRepository.findByUsername(eq(username))).thenReturn(user);

        final ChangeWithdrawPasswordCommand command =
                new ChangeWithdrawPasswordCommand(
                        userId,
                        username,
                        previousWithdrawPassword,
                        newWithdrawPassword,
                        confirmedNewWithdrawPassword,
                        operatingIp,
                        date);

        CommandGateway gateway = mock(CommandGateway.class);
        doNothing().when(gateway).send(eq(command));

        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserService(userService);
        service.setUserRepository(userRepository);
        service.setCommandGateway(gateway);
        service.setPasswordEncoder(passwordEncoder);


        service.changeWithdrawPassword(previousWithdrawPassword,
                newWithdrawPassword,
                confirmedNewWithdrawPassword,
                operatingIp,
                date);
        user.setWithdrawPassword(passwordEncoder.encode(previousWithdrawPassword));
        service.changeWithdrawPassword(previousWithdrawPassword + 1,
                newWithdrawPassword,
                confirmedNewWithdrawPassword,
                operatingIp,
                date);

        service.changeWithdrawPassword(previousWithdrawPassword,
                newWithdrawPassword,
                confirmedNewWithdrawPassword,
                operatingIp,
                date);

        verify(userService, times(6)).getCurrentUser();
        verify(userRepository, times(3)).findByUsername(eq(username));
        verify(gateway).send(eq(command));
    }

    @Test
    public void testFindByEmail() throws Exception {
        final String email = "dd@udufs.com";

        UserQueryRepository userRepository = mock(UserQueryRepository.class);
        when(userRepository.findByEmail(eq(email))).thenReturn(user);

        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserRepository(userRepository);

        final UserEntry user = service.findByEmail(email);
        assertThat(user, equalTo(this.user));

        verify(userRepository).findByEmail(eq(email));
    }

    @Test
    public void testFindPasswordResetCount() throws Exception {
        final String email = "svd@163.com";
        final String operatingIp = "ip";
        final Date currentTime = new Date();
        final Date startDate = new Date(currentTime.getTime() - 24L * 60 * 60 * 1000L);

        UserPasswordResetRepository userPasswordResetRepository = mock(UserPasswordResetRepository.class);
        when(userPasswordResetRepository
                .findNotExpiredByEmail(eq(email), eq(operatingIp), eq(startDate), eq(currentTime)))
                .thenReturn((List<UserPasswordReset>)null, Arrays.asList(new UserPasswordReset(), new UserPasswordReset()));

        UserServiceFacadeImpl service = new UserServiceFacadeImpl();
        service.setUserPasswordResetRepository(userPasswordResetRepository);

        int count = service.findPasswordResetCount(email, operatingIp, currentTime);
        assertThat(count, equalTo(0));

        count = service.findPasswordResetCount(email, operatingIp, currentTime);
        assertThat(count, equalTo(2));

        verify(userPasswordResetRepository, times(2)).findNotExpiredByEmail(eq(email), eq(operatingIp), eq(startDate), eq(currentTime));
    }
} 