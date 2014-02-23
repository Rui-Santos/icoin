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

import com.google.common.collect.ImmutableList;
import com.homhon.core.exception.IZookeyException;
import com.icoin.trading.users.domain.PasswordResetTokenGenerator;
import com.icoin.trading.users.domain.model.function.TooManyResetsException;
import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.users.domain.model.function.UserPasswordResetRepository;
import com.icoin.trading.users.domain.model.user.InvalidIdentityException;
import com.icoin.trading.users.domain.model.user.User;
import com.icoin.trading.users.domain.model.user.UserAccount;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.domain.model.user.UsernameAlreadyInUseException;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.homhon.mongo.TimeUtils.currentTime;
import static com.homhon.mongo.TimeUtils.futureMinute;
import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.Collections.isEmpty;
import static com.homhon.util.Objects.nullSafe;

/**
 * @author Jettro Coenradie
 */
@Component
public class UserCommandHandler {
    private static Logger logger = LoggerFactory.getLogger(UserCommandHandler.class);
    public static List<String> DEFAULT_ROLES = ImmutableList.of("ROLE_USER");
    private Repository<User> repository;

    private UserQueryRepository userQueryRepository;

    private UserPasswordResetRepository userPasswordResetRepository;

    private PasswordResetTokenGenerator passwordResetTokenGenerator;

    private PasswordEncoder passwordEncoder;

    @CommandHandler
    public UserId handleCreateUser(CreateUserCommand command) {
        hasLength(command.getUsername());
        notNull(command.getIdentifier());
        isTrue(command.isValid());

        if (!command.getIdentifier().isValid()) {
            throw new InvalidIdentityException(command.getIdentifier());
        }

        UserAccount account = userQueryRepository.findByUsername(command.getUsername());

        if (account != null) {
            throw new UsernameAlreadyInUseException(command.getUsername());
        }
        UserId identifier = command.getUserId();
        User user = new User(identifier,
                command.getUsername(),
                command.getFirstName(),
                command.getLastName(),
                command.getIdentifier(),
                command.getEmail(),
                passwordEncoder.encode(command.getPassword()),
                DEFAULT_ROLES);
        repository.add(user);
        return identifier;
    }

    @CommandHandler
    public UserAccount handleAuthenticateUser(AuthenticateUserCommand command) {
        UserAccount account = userQueryRepository.findByUsername(command.getUserName());

        if (account == null) {
            return null;
        }
        final Date authTime = nullSafe(command.getAuthTime(), currentTime());
        boolean success = onUser(account.getPrimaryKey())
                .authenticate(passwordEncoder, command.getPassword(), command.getOperatingIp(), authTime);
        return success ? account : null;
    }

    private User onUser(String userId) {
        return repository.load(new UserId(userId), null);
    }

    @CommandHandler
    public String handleForgetPassword(ForgetPasswordCommand command) {
        hasLength(command.getEmail());
        notNull(command.getCurrentTime());

        UserEntry user = userQueryRepository.findByEmail(command.getEmail());

        if (user == null) {
            logger.warn("can not find user by email!", command.getEmail());
            return "";
        }

        Date date = command.getCurrentTime();

        Date startDate = DateUtils.addDays(date, -1);

        List<UserPasswordReset> resets = userPasswordResetRepository.findNotExpiredByEmail(command.getEmail(), command.getOperatingIp(), startDate, date);

        if (!isEmpty(resets) && resets.size() >= 3) {
            throw new TooManyResetsException(command.getOperatingIp());
        }

        List<UserPasswordReset> totalResets = userPasswordResetRepository.findNotExpiredByEmail(command.getEmail(), startDate, date);

        if (!isEmpty(totalResets) && totalResets.size() >= 5) {
            throw new TooManyResetsException(command.getOperatingIp());
        }
        UserPasswordReset userPasswordReset = createPasswordReset(command, user);
        userPasswordResetRepository.save(userPasswordReset);

        return userPasswordReset.getToken();
    }

    private UserPasswordReset createPasswordReset(ForgetPasswordCommand command, UserEntry user) {
        UserPasswordReset reset = new UserPasswordReset();

        reset.setToken(getToken(command, user.getUsername()));
        reset.setUsername(user.getUsername());
        reset.setUserId(user.getPrimaryKey());
        reset.setEmail(command.getEmail());
        reset.setIp(command.getOperatingIp());
        reset.setExpirationDate(futureMinute(command.getCurrentTime(), 30));

        return reset;
    }

    @CommandHandler
    public String handlePasswordReset(ResetPasswordCommand command) {
        hasLength(command.getToken());
        hasLength(command.getPassword());
        hasLength(command.getConfirmedPassword());
        isTrue(command.isValid(), "The password and confirmed password should be the same!");

        UserPasswordReset token = userPasswordResetRepository.findByToken(command.getToken());

        if (token == null || !token.isValid(command.getChangedTime())) {
            logger.warn("cannot find password token {} or token {} is invalid", command.getToken(), token);
            return "";
        }

        token.setUsed(true);
        userPasswordResetRepository.save(token);

        User user = repository.load(new UserId(token.getUserId()));

        final Date changedTime = nullSafe(command.getChangedTime(), currentTime());
        user.changePassword(passwordEncoder.encode(command.getPassword()), passwordEncoder.encode(command.getConfirmedPassword()), command.getOperatingIp(), changedTime);

        clearPasswordResetTokens(token.getUserId(), token.getUsername());

        return token.getUsername();
    }

    @CommandHandler
    public void handleChangePassword(ChangePasswordCommand command) {
        notNull(command.getUserId());
        hasLength(command.getUserId().toString(), "user Id cannot be empty");
        hasLength(command.getConfirmPassword());
        hasLength(command.getPassword());
        isTrue(command.isValid(), "The password and confirmed password should be the same, but password should be different from previous one!");

        User user = repository.load(command.getUserId());

        if (user == null) {
            logger.warn("cannot find user {}", command.getUserId());
            return;
        }

        final String password = user.getPassword();

        final boolean matches = passwordEncoder.matches(command.getPreviousPassword(), password);
        if (!matches) {
            logger.warn("user {}, id {}, password not matched for previous to change.", command.getUsername(), command.getUserId());
            return;
        }

        final Date changedTime = nullSafe(command.getChangedTime(), currentTime());

        user.changePassword(passwordEncoder.encode(command.getPassword()), passwordEncoder.encode(command.getConfirmPassword()), command.getOperatingIp(), changedTime);

        logger.info("userid {}, username {} has changed password!", command.getUserId(), command.getUsername());

        clearPasswordResetTokens(command.getUserId().toString(), command.getUsername());
    }

    @CommandHandler
    public void handleChangeWithdrawPasswordCommand(ChangeWithdrawPasswordCommand command) {
        notNull(command.getUserId());
        hasLength(command.getConfirmedWithdrawPassword());
        hasLength(command.getWithdrawPassword());
        isTrue(command.isValid(), "The password and confirmed password should be the same, but password should be different from previous one!");

        User user = repository.load(command.getUserId());

        if (user == null) {
            logger.warn("cannot find user {}", command.getUserId());
            return;
        }

        final String withdrawPassword = user.getWithdrawPassword();

        final boolean matches = passwordEncoder.matches(command.getPreviousWithdrawPassword(), withdrawPassword);
        if (matches) {
            logger.warn("user {}, id {}, withdraw password not matched for previous to change.", command.getUsername(), command.getUserId());
            return;
        }

        final Date changedTime = nullSafe(command.getChangedTime(), currentTime());
        user.changeWithdrawPassword(passwordEncoder.encode(command.getWithdrawPassword()),
                passwordEncoder.encode(command.getConfirmedWithdrawPassword()),
                command.getOperatingIp(),
                changedTime);

        logger.info("userid {}, username {} has changed withdraw password!", command.getUserId(), command.getUsername());
    }

    @CommandHandler
    public void handleCreateWithdrawPasswordCommand(CreateWithdrawPasswordCommand command) {
        notNull(command.getUserId());
        hasLength(command.getConfirmedWithdrawPassword());
        hasLength(command.getWithdrawPassword());
        isTrue(command.isValid(), "The password and confirmed password should be the same, but password should be different from previous one!");

        User user = repository.load(command.getUserId());

        if (user == null) {
            logger.warn("cannot find user {}", command.getUserId());
            return;
        }

        final Date changedTime = nullSafe(command.getCreatedTime(), currentTime());
        user.createWithdrawPassword(passwordEncoder.encode(command.getWithdrawPassword()),
                passwordEncoder.encode(command.getConfirmedWithdrawPassword()),
                command.getOperatingIp(),
                changedTime);

        logger.info("userid {}, username {} has changed withdraw password!", command.getUserId(), command.getUsername());
    }

    @CommandHandler
    public void handleUpdateNotificationCommand(UpdateNotificationSettingsCommand command) {
        notNull(command.getUserId(), "user Id cannot be null");
        hasLength(command.getUserId().toString(), "user Id cannot be empty");
        hasLength(command.getUsername(), "username cannot be null");

        User user = repository.load(command.getUserId());

        if (user == null) {
            logger.warn("cannot find user {}", command.getUserId());
            return;
        }

        user.updateNotificationSettings(
                command.isLogonAlert(),
                command.isWithdrawMoneyAlert(),
                command.isWithdrawItemAlert(),
                command.isExecutedAlert());

        logger.info("userid {}, username {} has updated notification info {}!", command.getUserId(), command.getUsername(), command);
    }

    private void clearPasswordResetTokens(String userId, String userName) {
        List<UserPasswordReset> resets = userPasswordResetRepository.findByUsername(userName);
        if (isEmpty(resets)) {
            logger.info("userid {}, username {}, has no password reset tokens!", userId, userName);
            return;
        }

        userPasswordResetRepository.delete(resets);
        logger.info("userid {}, username {} deleted the password reset token!", userId, userName);
    }

    private String getToken(ForgetPasswordCommand command, String username) {
        for (int i = 0; i < 5; i++) {
            String token = passwordResetTokenGenerator.generate(username, command.getOperatingIp(), command.getCurrentTime());
            UserPasswordReset byToken = userPasswordResetRepository.findByToken(token);

            if (byToken == null) {
                return token;
            }
        }
        throw new IZookeyException("Cannot generate password token!");
    }

    @CommandHandler
    public void handleChangeInfoCommand(ChangeInfoCommand command) {
        notNull(command.getUserId(), "userid should not be empty");
        hasLength(command.getUserId().toString(), "user Id cannot be empty");
        hasLength(command.getUsername(), "username should not be empty");

        User user = repository.load(command.getUserId());

        if (user == null) {
            logger.warn("cannot find user {}", command.getUserId());
            return;
        }

        user.editInfo(command.getEmail(), command.getMobile(), command.getFirstName(), command.getLastName());

        logger.info("userid {}, username {} has changed info {}!", command.getUserId(), command.getUsername(), command);
    }

    @CommandHandler
    public void handleChangeAdminInfoCommand(ChangeAdminInfoCommand command) {
        notNull(command.getUserId(), "userid should not be empty");
        hasLength(command.getUserId().toString(), "user Id cannot be empty");
        hasLength(command.getUsername(), "username should not be empty");
        isTrue(command.isValid(), String.format("command %s is not validate", command));

        User user = repository.load(command.getUserId());

        if (user == null) {
            logger.warn("cannot find user {}", command.getUserId());
            return;
        }

        user.editAdminInfo(
                command.getEmail(),
                command.getIdentifier(),
                command.getMobile(),
                command.getFirstName(),
                command.getLastName(),
                command.getRoles());

        logger.info("userid {}, username {} has changed admin info {}!", command.getUserId(), command.getUsername(), command);
    }


    @Autowired
    @Qualifier("userRepository")
    public void setRepository(Repository<User> userRepository) {
        this.repository = userRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserPasswordResetRepository(UserPasswordResetRepository userPasswordResetRepository) {
        this.userPasswordResetRepository = userPasswordResetRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setUserRepository(UserQueryRepository userRepository) {
        this.userQueryRepository = userRepository;
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setPasswordResetTokenGenerator(PasswordResetTokenGenerator passwordResetTokenGenerator) {
        this.passwordResetTokenGenerator = passwordResetTokenGenerator;
    }
}
