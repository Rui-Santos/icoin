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
package com.icoin.trading.webui.user;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.users.application.command.CreateUserCommand;
import com.icoin.trading.users.domain.model.user.Identifier;
import com.icoin.trading.users.domain.model.user.UserId;
import com.icoin.trading.users.domain.model.user.UsernameAlreadyInUseException;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import com.icoin.trading.webui.user.form.SignupForm;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@Controller
public class SignupController {
    private static Logger logger = LoggerFactory.getLogger(SignupController.class);

    private final UserQueryRepository userQueryRepository;

    @Inject
    private CommandGateway gateway;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Inject
    public SignupController(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupForm(WebRequest request, Model model) {
        Connection<?> connection = ProviderSignInUtils.getConnection(request);
        if (connection != null) {
            request.setAttribute("message", new Message(MessageType.INFO, "Your " + StringUtils.capitalize(connection.getKey().getProviderId()) + " account is not associated with a Spring Social Showcase account. If you're new, please sign up."), WebRequest.SCOPE_REQUEST);
            final SignupForm signup = SignupForm.fromProviderUser(connection.fetchUserProfile());
            model.addAttribute("signupForm", signup);
        } else {
            model.addAttribute("signupForm", new SignupForm());
        }

        return "signup/signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signup(@Valid SignupForm form,
                         BindingResult formBinding,
                         WebRequest request,
                         HttpServletRequest httpRequest) {
        if (formBinding.hasErrors()) {
            return "signup/signup";
        }

        if(!form.isAgreed()){
            formBinding.rejectValue("agreed", "error.signup.agreed.notchecked","You must agree with the terms.");
            return "signup/signup";
        }

        final UserEntry byEmail = userQueryRepository.findByEmail(form.getEmail());
        if (byEmail != null) {
            formBinding.rejectValue("email", "error.signup.email.duplicated","Email has already been used.");
            return "signup/signup";
        }
        final UserEntry byUsername = userQueryRepository.findByUsername(form.getUsername());
        if (byUsername != null) {
            formBinding.rejectValue("email", "error.signup.username.duplicated","Username has already been used.");
            return "signup/signup";
        }
        UserId userId = createAccount(form, formBinding);
        if (userId != null) {
            final UserEntry created = userQueryRepository.findOne(userId.toString());
            if (created == null) {
                return "signup/signup";
            }
            logger.info("successfully created user " + form.getUsername());

            //todo add credential
            //new SimpleGrantedAuthority("ROLE_USER")
            final Authentication authentication = AuthUtils.getAuthentication(created, new WebAuthenticationDetails(httpRequest), null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            ProviderSignInUtils.handlePostSignUp(created.getUsername(), request);
            return "redirect:/";
        }
        return "signup/signup";
    }

    // internal helpers

    private UserId createAccount(final SignupForm form, BindingResult formBinding) {
        try {
            UserId userId = new UserId();
            CreateUserCommand createUser =
                    new CreateUserCommand(userId,
                            form.getUsername(),
                            form.getFirstName(),
                            form.getLastName(),
                            new Identifier(Identifier.Type.IDENTITY_CARD, form.getIdentifier()),
                            form.getEmail(),
                            form.getPassword(),
                            form.getConfirmedPassword(),
                            Constants.DEFAULT_ROLES);

            gateway.sendAndWait(createUser, 60, TimeUnit.SECONDS);
            return userId;
        } catch (CommandExecutionException e) {
            if (e.getCause() instanceof UsernameAlreadyInUseException) {
                formBinding.rejectValue("username", "user.duplicateUsername", "already in use");
                return null;
            }
            formBinding.rejectValue("username", "user.notcreated", "cannot create username");
            return null;
        }
    }

}
