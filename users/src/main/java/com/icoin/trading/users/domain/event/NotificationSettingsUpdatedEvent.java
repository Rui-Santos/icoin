package com.icoin.trading.users.domain.event;
import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.users.domain.model.user.UserId;

/**
 * Command to create a new user.
 *
 * @author Jettro Coenradie
 */
public class NotificationSettingsUpdatedEvent extends EventSupport<NotificationSettingsUpdatedEvent> {
    private UserId userId;

    private String username;

    private boolean logonAlert;
    private boolean withdrawMoneyAlert;
    private boolean withdrawItemAlert;
    private boolean executedAlert;

    public NotificationSettingsUpdatedEvent(UserId userId,
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
        return "NotificationSettingsUpdatedEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", logonAlert=" + logonAlert +
                ", withdrawMoneyAlert=" + withdrawMoneyAlert +
                ", withdrawItemAlert=" + withdrawItemAlert +
                ", executedAlert=" + executedAlert +
                '}';
    }
}