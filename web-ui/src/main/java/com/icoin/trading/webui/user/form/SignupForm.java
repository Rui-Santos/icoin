/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icoin.trading.webui.user.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.social.connect.UserProfile;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SignupForm {

    @NotEmpty
    @Length(min = 6, message = "must be at least 6 characters")
    private String username;

    @NotEmpty
    @Length(min = 6, message = "must be at least 6 characters")
    private String password;

    @NotEmpty
    @Length(min = 6, message = "must be at least 6 characters")
    private String confirmedPassword;

    @NotEmpty
    @Size(min = 1, message = "must be at least 1 characters")
    private String firstName;

    @NotEmpty
    @Length(min = 1, message = "must be at least 1 characters")
    private String lastName;

    @NotEmpty
    @Length(min = 1, message = "must be at least 6 characters")
    private String captcha;

    @
    private String mobile;

//    @AssertTrue
    private boolean agreed;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Pattern(regexp = "(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)", message = "must be valid 15 or 18 characters")
    private String identifier;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isAgreed() {
        return agreed;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public static SignupForm fromProviderUser(UserProfile providerUser) {
        SignupForm form = new SignupForm();
        form.setFirstName(providerUser.getFirstName());
        form.setLastName(providerUser.getLastName());
        form.setUsername(providerUser.getUsername());
        form.setEmail(providerUser.getEmail());
        return form;
    }
}
