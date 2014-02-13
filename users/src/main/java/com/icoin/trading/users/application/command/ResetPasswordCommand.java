package com.icoin.trading.users.application.command;

import com.homhon.base.command.CommandSupport;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.homhon.util.Strings.hasText;

/**
 * Command to create a new user.
 *
 * @author Jettro Coenradie
 */
public class ResetPasswordCommand extends CommandSupport<ResetPasswordCommand> {
    @NotNull(message = "The token cannot be null")
    private String token;
    @Size(min = 6, message = "The Password has at least 6 characters.")
    private String password;
    @Size(min = 6, message = "The confirmed Password has at least 6 characters.")
    private String confirmedPassword;

    private String operatingIp;

    public ResetPasswordCommand(String token, String password, String confirmedPassword, String operatingIp) {
        this.token = token;
        this.password = password;
        this.confirmedPassword = confirmedPassword;
        this.operatingIp = operatingIp;
    }

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public boolean isValid() {
        return hasText(password) && password.equals(confirmedPassword);
    }

    public String getOperatingIp() {
        return operatingIp;
    }
}
