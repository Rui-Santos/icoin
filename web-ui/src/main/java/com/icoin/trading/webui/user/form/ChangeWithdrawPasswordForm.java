package com.icoin.trading.webui.user.form;

import javax.validation.constraints.Size;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/10/14
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChangeWithdrawPasswordForm {
    @Size(min = 6, max = 16)
    private String previousWithdrawPassword;
    @Size(min = 6, max = 16)
    private String withdrawPassword;
    @Size(min = 6, max = 16)
    private String confirmedWithdrawPassword;

    public String getPreviousWithdrawPassword() {
        return previousWithdrawPassword;
    }

    public void setPreviousWithdrawPassword(String previousWithdrawPassword) {
        this.previousWithdrawPassword = previousWithdrawPassword;
    }

    public String getWithdrawPassword() {
        return withdrawPassword;
    }

    public void setWithdrawPassword(String withdrawPassword) {
        this.withdrawPassword = withdrawPassword;
    }

    public String getConfirmedWithdrawPassword() {
        return confirmedWithdrawPassword;
    }

    public void setConfirmedWithdrawPassword(String confirmedWithdrawPassword) {
        this.confirmedWithdrawPassword = confirmedWithdrawPassword;
    }
}