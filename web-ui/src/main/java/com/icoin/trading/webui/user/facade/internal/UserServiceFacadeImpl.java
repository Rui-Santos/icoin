package com.icoin.trading.webui.user.facade.internal;

import com.homhon.base.domain.service.UserService;
import com.homhon.util.Strings;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.api.users.command.AuthenticateUserCommand;
import com.icoin.trading.api.users.command.ChangeAdminInfoCommand;
import com.icoin.trading.api.users.command.ChangeInfoCommand;
import com.icoin.trading.api.users.command.ChangePasswordCommand;
import com.icoin.trading.api.users.command.ChangeWithdrawPasswordCommand;
import com.icoin.trading.api.users.command.CreateWithdrawPasswordCommand;
import com.icoin.trading.api.users.command.ForgetPasswordCommand;
import com.icoin.trading.api.users.command.ResetPasswordCommand;
import com.icoin.trading.api.users.command.UpdateNotificationSettingsCommand;
import com.icoin.trading.users.domain.ForgetPasswordEmailSender;
import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.users.domain.model.function.UserPasswordResetRepository;
import com.icoin.trading.api.users.domain.Identifier;
import com.icoin.trading.users.domain.model.user.UserAccount;
import com.icoin.trading.api.users.domain.UserId;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import com.icoin.trading.webui.user.facade.UserServiceFacade;
import org.apache.commons.lang3.time.DateUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.Collections.isEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/17/13
 * Time: 6:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class UserServiceFacadeImpl implements UserServiceFacade {
    private static Logger logger = LoggerFactory.getLogger(UserServiceFacadeImpl.class);

    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private UserQueryRepository userRepository;
    private PortfolioQueryRepository portfolioQueryRepository;

    private CommandGateway commandGateway;

    private ForgetPasswordEmailSender emailSender;

    private UserPasswordResetRepository userPasswordResetRepository;

    @Override
    public UserAccount currentUser() {
        return (UserAccount) userService.getCurrentUser();
    }

    @Override
    public UserEntry currentDetailUser() {
        UserAccount userAccount = currentUser();

        if (userAccount == null) {
            logger.warn("user not logged on");
            return null;
        }

        UserEntry user = userRepository.findByUsername(userAccount.getUsername());
        if (user == null) {
            logger.warn("user {} cannot be found", userAccount.getUsername());
            return null;
        }
        return user;
    }

    @Override
    public boolean isWithdrawPasswordSet() {
        final UserEntry user = currentDetailUser();

        if (user != null && user.isWithdrawPasswordSet()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isWithdrawPasswordMatched(String withdrawPassword) {
        if (!Strings.hasText(withdrawPassword)) {
            return false;
        }

        final UserEntry user = currentDetailUser();

        if (user == null) {
            return false;
        }

        return passwordEncoder.matches(withdrawPassword, user.getWithdrawPassword());
    }

    @Override
    public boolean canAuthWithNewPassword(String username, String newPassword) {
        if (!Strings.hasText(username) || !Strings.hasText(newPassword)) {
            logger.warn("username/password empty: username {}, password {}", username, newPassword);
            return false;
        }

        UserEntry user = userRepository.findByUsername(username);
        if (user == null) {
            logger.error("user {} cannot be found", username);
            return false;
        }
        return passwordEncoder.matches(newPassword, user.getPassword());
    }

    @Override
    public UserPasswordReset getToken(String token) {
        if (!Strings.hasText(token)) {
            return null;
        }
        return userPasswordResetRepository.findByToken(token);
    }

    /**
     * For now we work with only one portfolio per user. This might change in the future.
     *
     * @return The found portfolio for the logged in user.
     */
    public PortfolioEntry obtainPortfolioForUser() {
        final UserAccount user = currentUser();
        if (user == null) {
            return null;
        }
        return portfolioQueryRepository.findByUserIdentifier(user.getPrimaryKey());
    }

    @Override
    public boolean matchPreviousPassword(String previousPassword) {
        UserEntry user = currentDetailUser();
        if (user == null) {
            logger.warn("user not logged on");
            return false;
        }

        if (!Strings.hasLength(user.getPassword())) {
            return false;
        }

        final boolean matches = passwordEncoder.matches(previousPassword, user.getPassword());
        if (!matches) {
            logger.warn("user {}, id {}, password not matched for previous to change.", user.getUsername(), user.getPrimaryKey());
            return false;
        }

        return true;
    }

    @Override
    public void editDetails(String username, String email, String cellPhoneNumber, String firstName, String lastName) {
        if (!Strings.hasText(username)) {
            logger.warn("username is null! cannot update {}, {}, {}, {}.", email, cellPhoneNumber, firstName, lastName);
            return;
        }
        UserAccount account = currentUser();

        if (account == null || !username.equalsIgnoreCase(account.getUsername())) {
            logger.warn("current account not found, or username not matched!, username {}, current one",
                    username, account == null ? "" : account.getUsername());
            return;
        }

        ChangeInfoCommand command = new ChangeInfoCommand(new UserId(account.getPrimaryKey()), username, email, cellPhoneNumber, firstName, lastName);
        commandGateway.sendAndWait(command);
        logger.info("user info changed {}", command);
    }

    @Override
    public void editAdminDetails(String username,
                                 String email,
                                 Identifier identifier,
                                 String cellPhoneNumber,
                                 String firstName,
                                 String lastName,
                                 List<String> roles) {
        if (!Strings.hasText(username)) {
            logger.warn("username is null! cannot update {}, {}, {}, {}, {}.", email, identifier, cellPhoneNumber, firstName, lastName);
            return;
        }
        UserAccount account = currentUser();

        if (account == null || !username.equalsIgnoreCase(account.getUsername())) {
            logger.warn("current account not found, or username not matched!, username {}, current one",
                    username, account == null ? "" : account.getUsername());
            return;
        }

        ChangeAdminInfoCommand command =
                new ChangeAdminInfoCommand(
                        new UserId(account.getPrimaryKey()),
                        username,
                        email,
                        identifier,
                        cellPhoneNumber,
                        firstName,
                        lastName,
                        roles);
        commandGateway.sendAndWait(command);
        logger.info("user admin info changed {}", command);
    }

    @Override
    public void updateNotificationSettings(boolean logonAlert, boolean withdrawMoneyAlert, boolean withdrawItemAlert, boolean executedAlert) {
        UserAccount account = currentUser();

        if (account == null || !Strings.hasText(account.getPrimaryKey()) || !Strings.hasText(account.getUsername())) {
            logger.warn("current user is null! cannot update {}, {}, {}, {}.", logonAlert, withdrawMoneyAlert, withdrawItemAlert, executedAlert);
            return;
        }

        UpdateNotificationSettingsCommand command =
                new UpdateNotificationSettingsCommand(
                        new UserId(account.getPrimaryKey()),
                        account.getUsername(),
                        logonAlert, withdrawMoneyAlert,
                        withdrawItemAlert,
                        executedAlert);
        commandGateway.sendAndWait(command);
        logger.info("user notification settings changed {}", command);
    }

    @Override
    public void changePassword(String previousPassword,
                               String newPassword,
                               String confirmedNewPassword,
                               String operatingIp,
                               Date changedTime) {
        UserAccount userAccount = currentUser();
        if (userAccount == null) {
            logger.warn("user not logged on");
            return;
        }

        boolean matched = matchPreviousPassword(previousPassword);
        if (!matched) {
            logger.warn("previous password not matched");
            return;
        }

        commandGateway.send(new ChangePasswordCommand(
                new UserId(userAccount.getPrimaryKey()),
                userAccount.getUsername(),
                previousPassword,
                newPassword,
                confirmedNewPassword,
                operatingIp,
                changedTime));
    }

    @Override
    public boolean createWithdrawPassword(String withdrawPassword, String confirmedWithdrawPassword, String operatingIp, Date changedTime) {
        UserEntry user = currentDetailUser();
        if (user == null) {
            logger.warn("user not logged on or not found");
            return false;
        }

        if (user.isWithdrawPasswordSet()) {
            logger.warn("user {}, id {} has already created withdraw password!", user.getUsername(), user.getPrimaryKey());
            return false;
        }

        commandGateway.send(new CreateWithdrawPasswordCommand(
                new UserId(user.getPrimaryKey()),
                user.getUsername(),
                withdrawPassword,
                confirmedWithdrawPassword,
                operatingIp,
                changedTime));
        return true;
    }

    @Override
    public boolean matchPreviousWithdrawPassword(String previousPassword) {
        UserEntry user = currentDetailUser();
        if (user == null) {
            logger.warn("user not logged on");
            return false;
        }

        if (!user.isWithdrawPasswordSet()) {
            return false;
        }

        final boolean matches = passwordEncoder.matches(previousPassword, user.getWithdrawPassword());
        if (!matches) {
            logger.warn("user {}, id {}, withdraw password not matched for previous to change.", user.getUsername(), user.getPrimaryKey());
            return false;
        }

        return true;
    }

    public boolean generateForgetPasswordToken(String email, String operatingIp, Date currentTime) {
        UserAccount userAccount = currentUser();
        if (userAccount != null) {
            logger.warn("user already logged on");
            return false;
        }

        ForgetPasswordCommand command = new ForgetPasswordCommand(email, operatingIp, currentTime);

        String token = commandGateway.sendAndWait(command, 5, TimeUnit.SECONDS);

        if (!Strings.hasLength(token)) {
            return false;
        }

        emailSender.sendEmail(token);

        return true;
    }


    @Override
    public UserAccount resetPasswordWithToken(String token,
                                              String password,
                                              String confirmedPassword,
                                              String operatingIp,
                                              Date resetTime) {
        UserAccount userAccount = currentUser();
        if (userAccount != null) {
            logger.warn("user has already logged on");
            return null;
        }
        ResetPasswordCommand command = new ResetPasswordCommand(token, password, confirmedPassword, operatingIp, resetTime);

        final String username = commandGateway.sendAndWait(command);

        if (!Strings.hasText(username)) {
            return null;
        }

        AuthenticateUserCommand authenticateUserCommand = new AuthenticateUserCommand(username, password, operatingIp, resetTime);
        return commandGateway.sendAndWait(authenticateUserCommand);
    }

    @Override
    public void changeWithdrawPassword(String previousPassword,
                                       String withdrawPassword,
                                       String confirmedWithdrawPassword,
                                       String operatingIp,
                                       Date changedTime) {
        UserAccount userAccount = currentUser();
        if (userAccount == null) {
            logger.warn("user not logged on");
            return;
        }

        boolean matched = matchPreviousWithdrawPassword(previousPassword);
        if (!matched) {
            logger.warn("previous withdraw password not matched");
            return;
        }

        commandGateway.send(new ChangeWithdrawPasswordCommand(
                new UserId(userAccount.getPrimaryKey()),
                userAccount.getUsername(),
                previousPassword,
                withdrawPassword,
                confirmedWithdrawPassword,
                operatingIp,
                changedTime));
    }

    @Override
    public UserEntry findByEmail(String email) {
        if (!Strings.hasText(email)) {
            return null;
        }
        UserEntry user = userRepository.findByEmail(email);

        if (user == null) {
            logger.warn("can not find user by email!", email);
            return null;
        }

        return user;
    }

    @Override
    public int findPasswordResetCount(String email, String ip, Date currentDate) {
        hasLength(email);
        hasLength(ip);
        notNull(currentDate);

        Date startDate = DateUtils.addDays(currentDate, -1);

        List<UserPasswordReset> resets = userPasswordResetRepository.findNotExpiredByEmail(email, ip, startDate, currentDate);

        if (isEmpty(resets)) {
            return 0;
        }
        return resets.size();
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserRepository(UserQueryRepository userRepository) {
        this.userRepository = userRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPortfolioQueryRepository(PortfolioQueryRepository portfolioQueryRepository) {
        this.portfolioQueryRepository = portfolioQueryRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserPasswordResetRepository(UserPasswordResetRepository userPasswordResetRepository) {
        this.userPasswordResetRepository = userPasswordResetRepository;
    }

    @Autowired
    public void setEmailSender(ForgetPasswordEmailSender emailSender) {
        this.emailSender = emailSender;
    }
}