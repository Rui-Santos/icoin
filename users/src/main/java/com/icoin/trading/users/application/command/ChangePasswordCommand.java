package com.icoin.trading.users.application.command;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-12
 * Time: PM9:55
 * To change this template use File | Settings | File Templates.
 */

import com.homhon.base.command.CommandSupport;
import com.homhon.util.Strings;
import com.icoin.trading.users.domain.model.user.UserId;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Command to create a new user.
 *
 * @author Jettro Coenradie
 */
public class ChangePasswordCommand extends CommandSupport<ChangePasswordCommand> {
    private UserId userId;
    @NotNull
    @Size(min = 6, message = "The provided username cannot be null", max = 16)
    private String username;

    @Size(min = 6, max = 16)
    private String previousPassword;

    @Size(min = 6, max = 16)
    private String password;

    @Size(min = 6, max = 16)
    private String confirmPassword;

    private String operatingIp;

    public ChangePasswordCommand(UserId userId, String username, String previousPassword, String password, String confirmPassword, String operatingIp) {
        notNull(userId, "The provided userId cannot be null");

        isTrue(confirmPassword.equals(password), "The password and confirmed password should be the same.");

        this.userId = userId;
        this.username = username;
        this.previousPassword = previousPassword;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.operatingIp = operatingIp;
    }

    public boolean isValid() {
        return Strings.hasText(password) && Strings.hasText(previousPassword) &&
                !previousPassword.equals(password)
                && password.equals(confirmPassword);
    }

    public String getPreviousPassword() {
        return previousPassword;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getOperatingIp() {
        return operatingIp;
    }
}