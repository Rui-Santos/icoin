package com.icoin.trading.users.application.command;

import com.icoin.trading.users.domain.model.user.UserId;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/10/14
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChangeWithdrawPasswordCommand {
    private UserId userId;
    @NotNull
    @Size(min = 6, message = "The provided username cannot be null", max = 16)
    private String username;

    @Size(min = 6, max = 16)
    private String previousWithdrawPassword;

    @Size(min = 6, max = 16)
    private String withdrawPassword;

    @Size(min = 6, max = 16)
    private String confirmedWithdrawPassword;

    private String operatingIp;

    public ChangeWithdrawPasswordCommand(UserId userId, String username, String previousPassword, String withdrawPassword, String confirmedWithdrawPassword,String operatingIp) {
        notNull(userId, "The provided userId cannot be null");

        isTrue(withdrawPassword.equals(confirmedWithdrawPassword), "The withdraw password and confirmed withdraw password should be the same.");

        this.userId = userId;
        this.username = username;
        this.previousWithdrawPassword = previousPassword;
        this.withdrawPassword = withdrawPassword;
        this.confirmedWithdrawPassword = confirmedWithdrawPassword;
        this.operatingIp = operatingIp;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPreviousWithdrawPassword() {
        return previousWithdrawPassword;
    }

    public String getWithdrawPassword() {
        return withdrawPassword;
    }

    public String getConfirmedWithdrawPassword() {
        return confirmedWithdrawPassword;
    }

    public String getOperatingIp() {
        return operatingIp;
    }
}
