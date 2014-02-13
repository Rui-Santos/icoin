package com.icoin.trading.webui.user.form;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Command to create a new user.
 *
 * @author Jettro Coenradie
 */
public class ForgetPasswordForm {
    @NotNull
    @Email
    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}