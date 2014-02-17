package com.icoin.trading.webui.user.form;

import com.homhon.util.Strings;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/10/14
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChangePasswordForm {
    @NotEmpty
    @Size(min = 6, max = 16, message = "Previous password length error.")
    private String previousPassword;
    @NotEmpty
    @Size(min = 6, max = 16, message = "Password length must be between 6 and 16")
    private String newPassword;
    @NotEmpty
    @Size(min = 6, max = 16, message = "Confirmed new password length must be between 6 and 16")
    private String confirmedNewPassword;

    public String getPreviousPassword() {
        return previousPassword;
    }

    public void setPreviousPassword(String previousPassword) {
        this.previousPassword = previousPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmedNewPassword() {
        return confirmedNewPassword;
    }

    public void setConfirmedNewPassword(String confirmedNewPassword) {
        this.confirmedNewPassword = confirmedNewPassword;
    }

    public boolean isValid() {
        return Strings.hasText(newPassword) && Strings.hasText(previousPassword) &&
                !previousPassword.equals(newPassword)
                && newPassword.equals(confirmedNewPassword);
    }
}