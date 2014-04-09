package com.icoin.trading.webui.user.form;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

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