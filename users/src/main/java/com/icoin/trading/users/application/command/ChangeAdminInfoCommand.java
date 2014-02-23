package com.icoin.trading.users.application.command;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-22
 * Time: AM2:21
 * To change this template use File | Settings | File Templates.
 */

import com.icoin.trading.users.domain.model.user.Identifier;
import com.icoin.trading.users.domain.model.user.UserId;

import java.util.List;


public class ChangeAdminInfoCommand extends ChangeInfoCommand<ChangeAdminInfoCommand> {
    //    @Pattern(regexp = "(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)", message = "must be valid 15 or 18 characters")
    private Identifier identifier;
    private List<String> roles;

    public ChangeAdminInfoCommand(UserId userId,
                                  String username,
                                  String email,
                                  Identifier identifier,
                                  String mobile,
                                  String firstName,
                                  String lastName,
                                  List<String> roles) {
        super(userId, username, email, mobile, firstName, lastName);
        this.identifier = identifier;
        this.roles = roles;
    }

    public boolean isValid() {
        return identifier == null ? true : identifier.isValid();
    }

    public List<String> getRoles() {
        return roles;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "ChangeInfoCommand{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                "identifier='" + identifier + '\'' +
                ", mobile='" + mobile + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roles=" + roles +
                '}';
    }
}