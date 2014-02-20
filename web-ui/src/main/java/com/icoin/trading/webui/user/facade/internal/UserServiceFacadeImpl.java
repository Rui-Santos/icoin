package com.icoin.trading.webui.user.facade.internal;

import com.homhon.base.domain.service.UserService;
import com.homhon.util.Strings;
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

        if (user != null && Strings.hasLength(user.getWithdrawPassword())) {
            return true;
        }

        return false;
    }

    /**
     * For now we work with only one portfolio per user. This might change in the future.
     *
     * @return The found portfolio for the logged in user.
     */
    public PortfolioEntry obtainPortfolioForUser() {
        final UserEntry user = currentDetailUser();
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

        final boolean matches = passwordEncoder.matches(previousPassword, user.getPassword());
        if(!matches){
            logger.warn("user {}, id {}, password not matched for previous to change.", user.getUsername(), user.getPrimaryKey());
            return false;
        }

        return true;
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
        UserAccount userAccount = currentUser();
        if (userAccount == null) {
            logger.warn("user not logged on");
            return false;
        }

        commandGateway.send(new CreateWithdrawPasswordCommand(
                new UserId(userAccount.getPrimaryKey()),
                userAccount.getUsername(),
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

        final boolean matches = passwordEncoder.matches(previousPassword, user.getWithdrawPassword());
        if(!matches){
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
    public void resetPasswordWithToken(String token,
                                       String password,
                                       String confirmedPassword,
                                       String operatingIp,
                                       Date resetTime) {
        UserAccount userAccount = currentUser();
        if (userAccount != null) {
            logger.warn("user has already logged on");
            return;
        }
        ResetPasswordCommand command = new ResetPasswordCommand(token, password, confirmedPassword, operatingIp, resetTime);

        commandGateway.send(command);
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