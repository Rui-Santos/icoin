package com.icoin.trading.webui.user.form;

import com.icoin.trading.users.query.UserEntry;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


public class ChangeInfoForm {

    @Size(min = 6, message = "must be at least 6 characters")
    private String username;

    private String mobile;

    @Email
    private String email;

    @Pattern(regexp = "(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)", message = "must be valid 15 or 18 characters")
    private String identifier;

    @NotEmpty
    @Size(min = 1, message = "must be at least 1 characters")
    private String firstName;

    @NotEmpty
    @Size(min = 1, message = "must be at least 1 characters")
    private String lastName;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static ChangeInfoForm toChangeInfoForm(UserEntry user) {
        ChangeInfoForm form = new ChangeInfoForm();
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setMobile(user.getCellPhoneNumber());
        form.setIdentifier(user.getIdentifier() == null ? "" : user.getIdentifier().getNumber());
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());
        return form;
    }
} 