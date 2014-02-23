package com.icoin.trading.webui.user.form;

import com.icoin.trading.users.query.UserEntry;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-15
 * Time: PM4:08
 * To change this template use File | Settings | File Templates.
 */
public class NotificationForm {
    private boolean logonAlert;
    private boolean withdrawMoneyAlert;
    private boolean withdrawItemAlert;
    private boolean executedAlert;

    public boolean isLogonAlert() {
        return logonAlert;
    }

    public void setLogonAlert(boolean logonAlert) {
        this.logonAlert = logonAlert;
    }

    public boolean isWithdrawMoneyAlert() {
        return withdrawMoneyAlert;
    }

    public void setWithdrawMoneyAlert(boolean withdrawMoneyAlert) {
        this.withdrawMoneyAlert = withdrawMoneyAlert;
    }

    public boolean isWithdrawItemAlert() {
        return withdrawItemAlert;
    }

    public void setWithdrawItemAlert(boolean withdrawItemAlert) {
        this.withdrawItemAlert = withdrawItemAlert;
    }

    public boolean isExecutedAlert() {
        return executedAlert;
    }

    public void setExecutedAlert(boolean executedAlert) {
        this.executedAlert = executedAlert;
    }

    public static NotificationForm toNotificationForm(UserEntry user) {
        NotificationForm form = new NotificationForm();
        form.setLogonAlert(user.isLogonAlert());
        form.setWithdrawMoneyAlert(user.isWithdrawMoneyAlert());
        form.setWithdrawItemAlert(user.isWithdrawItemAlert());
        form.setWithdrawItemAlert(user.isWithdrawItemAlert());
        return form;
    }
}
