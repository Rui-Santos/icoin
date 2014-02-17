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

package com.icoin.trading.webui.security;

import com.google.common.collect.Sets;
import com.icoin.trading.users.application.command.AuthenticateUserCommand;
import com.icoin.trading.users.domain.model.user.UserAccount;
import com.icoin.trading.webui.user.AuthUtils;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.StructuralCommandValidationFailedException;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.homhon.util.TimeUtils.currentTime;

/**
 * A custom spring security authentication provider that only supports {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}
 * authentications. This provider uses Axon's command bus to dispatch an authentication command. The main reason for
 * creating a custom authentication provider is that Spring's UserDetailsService model doesn't fit our authentication
 * model as the UserAccount doesn't hold the password (UserDetailsService expects the UserDetails object to hold the
 * password, which is then compared with the password provided by the {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}.
 *
 * @author Uri Boness
 * @author Jettro Coenradie
 */
@Component
public class TraderAuthenticationProvider implements AuthenticationProvider {

//    private final static Collection<GrantedAuthority> userAuthorities;

//    static {
//        userAuthorities = new HashSet<GrantedAuthority>();
//        userAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//    }

    private CommandGateway commandGateway;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }

        String ip = null;
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        if (token.getDetails() != null && WebAuthenticationDetails.class.isAssignableFrom(token.getDetails().getClass())) {
            ip = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }

        Date authTime = currentTime();
        String username = token.getName();
        String password = String.valueOf(token.getCredentials());
        FutureCallback<UserAccount> accountCallback = new FutureCallback<UserAccount>();
        AuthenticateUserCommand command = new AuthenticateUserCommand(username, password, ip, authTime);
        try {
            commandGateway.send(command, accountCallback);
            // the bean validating interceptor is defined as a dispatch interceptor, meaning it is executed before
            // the command is dispatched.
        } catch (StructuralCommandValidationFailedException e) {
            return null;
        }
        UserAccount account;
        try {
            account = accountCallback.get();
            if (account == null) {
                throw new BadCredentialsException("Invalid username and/or password");
            }
        } catch (InterruptedException e) {
            throw new AuthenticationServiceException("Credentials could not be verified", e);
        } catch (ExecutionException e) {
            throw new AuthenticationServiceException("Credentials could not be verified", e);
        }

        return AuthUtils.getAuthentication(account, authentication.getCredentials(), authentication.getDetails());
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
