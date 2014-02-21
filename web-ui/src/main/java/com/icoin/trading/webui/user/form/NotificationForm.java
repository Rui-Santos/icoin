package com.icoin.trading.webui.user.form;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-15
 * Time: PM4:08
 * To change this template use File | Settings | File Templates.
 */
public class NotificationForm {
    private boolean logonAlert;
    private boolean changePasswordAlert;
    private boolean withdrawPasswordAlert;
    private boolean withdrawMoneyAlert;

    public boolean isLogonAlert() {
        return logonAlert;
    }

    public void setLogonAlert(boolean logonAlert) {
        this.logonAlert = logonAlert;
    }

    public boolean isChangePasswordAlert() {
        return changePasswordAlert;
    }

    public void setChangePasswordAlert(boolean changePasswordAlert) {
        this.changePasswordAlert = changePasswordAlert;
    }

    public boolean isWithdrawPasswordAlert() {
        return withdrawPasswordAlert;
    }

    public void setWithdrawPasswordAlert(boolean withdrawPasswordAlert) {
        this.withdrawPasswordAlert = withdrawPasswordAlert;
    }

    public boolean isWithdrawMoneyAlert() {
        return withdrawMoneyAlert;
    }

    public void setWithdrawMoneyAlert(boolean withdrawMoneyAlert) {
        this.withdrawMoneyAlert = withdrawMoneyAlert;
    }
}
