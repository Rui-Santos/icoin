package com.icoin.trading.api.users.command;

import com.homhon.base.command.CommandSupport;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Command to create a new user.
 *
 * @author Jettro Coenradie
 */
public class ForgetPasswordCommand extends CommandSupport<ForgetPasswordCommand> {
    @NotNull
    @Email
    private String email;

    private String operatingIp;

    @NotNull
    private Date currentTime;

    public ForgetPasswordCommand(String email, String operatingIp, Date currentTime) {
        this.email = email;
        this.operatingIp = operatingIp;
        this.currentTime = currentTime;
    }

    public String getEmail() {
        return email;
    }

    public String getOperatingIp() {
        return operatingIp;
    }

    public Date getCurrentTime() {
        return currentTime;
    }
}