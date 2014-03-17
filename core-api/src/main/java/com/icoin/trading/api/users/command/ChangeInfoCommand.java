package com.icoin.trading.api.users.command;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.users.event.UserId;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class ChangeInfoCommand<T extends ChangeInfoCommand> extends CommandSupport<T> {
    @NotNull
    protected UserId userId;

    @Size(min = 6, message = "must be at least 6 characters")
    protected String username;

    @Email
    protected String email;

    protected String mobile;

    @NotEmpty
    @Size(min = 1, message = "must be at least 3 characters")
    protected String firstName;

    @NotEmpty
    @Size(min = 1, message = "must be at least 3 characters")
    protected String lastName;


    public ChangeInfoCommand(UserId userId,
                             String username,
                             String email,
                             String mobile,
                             String firstName,
                             String lastName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserId getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "ChangeInfoCommand{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}