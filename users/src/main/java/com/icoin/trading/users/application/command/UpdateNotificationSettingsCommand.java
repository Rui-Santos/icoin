package com.icoin.trading.users.application.command;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.users.domain.UserId;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Command to create a new user.
 *
 * @author Jettro Coenradie
 */
public class UpdateNotificationSettingsCommand extends CommandSupport<UpdateNotificationSettingsCommand> {
    @NotNull(message = "The provided userId cannot be null")
    private UserId userId;

    @NotNull
    @Size(min = 3, message = "The provided username cannot be null")
    private String username;

    private boolean logonAlert;
    private boolean withdrawMoneyAlert;
    private boolean withdrawItemAlert;
    private boolean executedAlert;

    public UpdateNotificationSettingsCommand(UserId userId,
                                             String username,
                                             boolean logonAlert,
                                             boolean withdrawMoneyAlert,
                                             boolean withdrawItemAlert,
                                             boolean executedAlert) {
        this.userId = userId;
        this.username = username;
        this.logonAlert = logonAlert;
        this.executedAlert = executedAlert;
        this.withdrawMoneyAlert = withdrawMoneyAlert;
        this.withdrawItemAlert = withdrawItemAlert;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public boolean isLogonAlert() {
        return logonAlert;
    }

    public boolean isWithdrawMoneyAlert() {
        return withdrawMoneyAlert;
    }

    public boolean isWithdrawItemAlert() {
        return withdrawItemAlert;
    }

    public boolean isExecutedAlert() {
        return executedAlert;
    }

    @Override
    public String toString() {
        return "UpdateNotificationSettingsCommand{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", logonAlert=" + logonAlert +
                ", withdrawMoneyAlert=" + withdrawMoneyAlert +
                ", withdrawItemAlert=" + withdrawItemAlert +
                ", executedAlert=" + executedAlert +
                '}';
    }
}
