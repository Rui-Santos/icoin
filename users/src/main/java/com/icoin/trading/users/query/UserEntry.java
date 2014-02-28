/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icoin.trading.users.query;

import com.homhon.mongo.domainsupport.modelsupport.entity.AuditAwareEntitySupport;
import com.homhon.util.Strings;
import com.icoin.trading.users.domain.model.user.Identifier;
import com.icoin.trading.users.domain.model.user.UserAccount;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;


/**
 * @author Jettro Coenradie
 */
public class UserEntry extends AuditAwareEntitySupport<UserEntry, String, Long> implements UserAccount<UserEntry> {

    @Indexed(unique = true)
    private String username;
    private String password;
    private String withdrawPassword;
    private String firstName;
    private String lastName;
    private String realName;
    private String cellPhoneNumber;
    @Indexed(unique = true)
    private String email;
    private Identifier identifier;

    private boolean logonAlert = true;
    private boolean changePasswordAlert = true;
    private boolean withdrawPasswordAlert = true;
    private boolean withdrawMoneyAlert = true;
    private boolean withdrawItemAlert = true;
    private boolean executedAlert;
    private List<String> roles;

    @Override
    public String getId() {
        return username;
    }

    @Override
    public String getName() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPrimaryKey() {
        return this.primaryKey;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Override
    public String getFullName() {
        return "" + lastName + " " + firstName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWithdrawPassword() {
        return withdrawPassword;
    }

    public void setWithdrawPassword(String withdrawPassword) {
        this.withdrawPassword = withdrawPassword;
    }

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

    public boolean isExecutedAlert() {
        return executedAlert;
    }

    public void setExecutedAlert(boolean executedAlert) {
        this.executedAlert = executedAlert;
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

    @Override
    public String toString() {
        return "UserEntry{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", realName='" + realName + '\'' +
                ", email='" + email + '\'' +
                ", identifier=" + identifier +
                ", logonAlert=" + logonAlert +
                ", changePasswordAlert=" + changePasswordAlert +
                ", withdrawPasswordAlert=" + withdrawPasswordAlert +
                ", withdrawMoneyAlert=" + withdrawMoneyAlert +
                ", withdrawItemAlert=" + withdrawItemAlert +
                ", executedAlert=" + executedAlert +
                ", roles=" + roles +
                '}';
    }

    @Override
    public boolean sameValueAs(UserEntry userEntry) {
        return super.sameIdentityAs(userEntry);
    }

    @Override
    public UserEntry copy() {
        final UserEntry userEntry = new UserEntry();

        userEntry.setUsername(username);
        userEntry.setPrimaryKey(primaryKey);
        userEntry.setLastName(lastName);
        userEntry.setFirstName(firstName);
//        userEntry.setIdentifier(identifier);
//        userEntry.setPassword(password);

        return userEntry;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public boolean isWithdrawPasswordSet() {
        return Strings.hasLength(withdrawPassword);
    }
}
