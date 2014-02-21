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

package com.icoin.trading.webui.user;

import com.homhon.util.Strings;
import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.users.domain.model.user.UserAccount;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import com.icoin.trading.webui.global.ResourceNotFoundException;
import com.icoin.trading.webui.user.facade.UserServiceFacade;
import com.icoin.trading.webui.user.form.ChangePasswordForm;
import com.icoin.trading.webui.user.form.ChangeWithdrawPasswordForm;
import com.icoin.trading.webui.user.form.CreateWithdrawPasswordForm;
import com.icoin.trading.webui.user.form.ForgetPasswordForm;
import com.icoin.trading.webui.user.form.ResetPasswordForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.homhon.util.TimeUtils.currentTime;

/**
 * @author Jettro Coenradie
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private UserQueryRepository userRepository;
    private UserServiceFacade userService;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public UserController(UserQueryRepository userRepository, UserServiceFacade userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showUsers(Model model) {
        model.addAttribute("items", userRepository.findAll());
        return "user/list";
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET)
    public String detail(@PathVariable("identifier") String userIdentifier, Model model) {
        model.addAttribute("item", userRepository.findOne(userIdentifier));
        return "user/detail";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePassword(Model model) {
        model.addAttribute("changePasswordForm", new ChangePasswordForm());

        if (userService.isWithdrawPasswordSet()) {
            model.addAttribute("changeWithdrawPasswordForm", new ChangeWithdrawPasswordForm());
            return "user/changePassword";
        }

        model.addAttribute("createWithdrawPasswordForm", new CreateWithdrawPasswordForm());
        return "user/changePassword";
    }

    @RequestMapping(value = "/createWithdrawPassword", method = RequestMethod.POST)
    public String createWithdrawPassword(@ModelAttribute("createWithdrawPasswordForm") @Valid CreateWithdrawPasswordForm createWithdrawPasswordForm,
                                         BindingResult bindingResult,
                                         HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            if (!createWithdrawPasswordForm.getWithdrawPassword().equals(createWithdrawPasswordForm.getConfirmedWithdrawPassword())) {
                bindingResult.rejectValue("withdrawPassword", "error.user.changepassword.differentcomfirmedpassword", "The password is not the same as the confirmed password");
                return "user/changePassword";
            }

            boolean created = userService.createWithdrawPassword(createWithdrawPasswordForm.getWithdrawPassword(),
                    createWithdrawPasswordForm.getWithdrawPassword(),
                    request.getRemoteAddr(),
                    currentTime());

            if (created) {
                return "dashboard/index";
            }


        }

        createWithdrawPasswordForm.setConfirmedWithdrawPassword(null);
        createWithdrawPasswordForm.setWithdrawPassword(null);

        return "user/changePassword";
    }

    @RequestMapping(value = "/changeWithdrawPassword", method = RequestMethod.POST)
    public String changeWithdrawPassword(@ModelAttribute("changeWithdrawPasswordForm") @Valid ChangeWithdrawPasswordForm changeWithdrawPasswordForm,
                                         BindingResult bindingResult,
                                         HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            if (changeWithdrawPasswordForm.getPreviousWithdrawPassword().equals(changeWithdrawPasswordForm.getWithdrawPassword())) {
                bindingResult.rejectValue("withdrawPassword", "error.user.changepassword.samepassword", "The previous password equals to new password");
                return "user/changePassword";
            }

            if (!changeWithdrawPasswordForm.getConfirmedWithdrawPassword().equals(changeWithdrawPasswordForm.getWithdrawPassword())) {
                bindingResult.rejectValue("confirmedWithdrawPassword", "error.user.changepassword.differentcomfirmedpassword", "The password does not match the confirmed password");
                return "user/changePassword";
            }

            if (!userService.matchPreviousWithdrawPassword(changeWithdrawPasswordForm.getPreviousWithdrawPassword())) {
                bindingResult.rejectValue("withdrawPassword", "error.user.changepassword.previousWithdrawPasswordUnmatched", "The previous withdrawal password is wrong");
                return "user/changePassword";
            }

            userService.changeWithdrawPassword(changeWithdrawPasswordForm.getPreviousWithdrawPassword(),
                    changeWithdrawPasswordForm.getWithdrawPassword(),
                    changeWithdrawPasswordForm.getConfirmedWithdrawPassword(),
                    request.getRemoteAddr(),
                    currentTime());

            return "dashboard/index";
//
//
//            if (changeWithdrawPasswordForm.getConfirmedWithdrawPassword().equals(changeWithdrawPasswordForm.getWithdrawPassword())) {
//                bindingResult.rejectValue("password", "error.user.changepassword.cannotchange", "The password cannot be changed");
//            }
        }

        changeWithdrawPasswordForm.setPreviousWithdrawPassword(null);
        changeWithdrawPasswordForm.setWithdrawPassword(null);
        changeWithdrawPasswordForm.setConfirmedWithdrawPassword(null);

        return "user/changePassword";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(@ModelAttribute("changePasswordForm") @Valid ChangePasswordForm changePasswordForm,
                                 BindingResult bindingResult,
                                 HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            if (changePasswordForm.getPreviousPassword().equals(changePasswordForm.getNewPassword())) {
                bindingResult.rejectValue("newPassword", "error.user.changepassword.samepassword", "The previous password equals to new password");
                return "user/changePassword";
            }

            if (!changePasswordForm.getConfirmedNewPassword().equals(changePasswordForm.getNewPassword())) {
                bindingResult.rejectValue("confirmedNewPassword", "error.user.changepassword.differentcomfirmedpassword", "The password does not match the confirmed password");
                return "user/changePassword";
            }

            if (!userService.matchPreviousPassword(changePasswordForm.getPreviousPassword())) {
                bindingResult.rejectValue("previousPassword", "error.user.changepassword.previousWithdrawPasswordUnmatched", "The previous password is wrong");
                return "user/changePassword";
            }

            userService.changePassword(changePasswordForm.getPreviousPassword(),
                    changePasswordForm.getNewPassword(),
                    changePasswordForm.getConfirmedNewPassword(),
                    request.getRemoteAddr(),
                    currentTime());

            return "dashboard/index";
        }

        changePasswordForm.setPreviousPassword(null);
        changePasswordForm.setNewPassword(null);
        changePasswordForm.setConfirmedNewPassword(null);

        return "user/changepassword";
    }

    @RequestMapping(value = "/forgetPassword", method = RequestMethod.GET)
    public String forgetPassword(Model model) {
        model.addAttribute("forgetPassword", new ForgetPasswordForm());

        return "user/forgetpassword";
    }

    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
    public String forgetPassword(@ModelAttribute("forgetPassword") @Valid ForgetPasswordForm forgetPasswordForm,
                                 HttpServletRequest request,
                                 BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            final UserEntry user = userService.findByEmail(forgetPasswordForm.getEmail());

            if (user == null) {
                bindingResult.rejectValue("email", "error.user.forgetpassword.emailnotexist", "Cannot find the email");
                return "user/forgetpassword";
            }

            final int resetCount = userService.findPasswordResetCount(user.getUsername(), request.getRemoteAddr(), currentTime());
            if (resetCount >= 3) {
                bindingResult.rejectValue("email", "error.user.forgetpassword.toomanyreset", "Too many reset within 24 hours");
                return "user/forgetpassword";
            }

            final boolean generated = userService.generateForgetPasswordToken(forgetPasswordForm.getEmail(), request.getRemoteAddr(), currentTime());
            if (!generated) {
                bindingResult.rejectValue("email", "error.user.forgetpassword.cannotgenerate", "Cannot generate the email!");
                return "user/forgetpassword";
            }
        }

        return "user/forgetpasswordsent";
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public String resetPassword(@RequestParam("token") String token,
                                Model model) {
        if (!Strings.hasText(token)) {
            throw new ResourceNotFoundException();
        }

        final UserPasswordReset reset = userService.getToken(token);

        if (reset == null || !reset.isValid(currentTime())) {
            throw new ResourceNotFoundException();
        }


        final ResetPasswordForm form = new ResetPasswordForm();
        form.setToken(token);
        model.addAttribute("resetPasswordForm", form);
        return "user/resetpassword";
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public String resetPassword(@ModelAttribute("resetPasswordForm") @Valid ResetPasswordForm resetPasswordForm,
                                BindingResult bindingResult,
                                HttpServletRequest httpRequest,
                                WebRequest request) {
        if (!bindingResult.hasErrors()) {
            if (!resetPasswordForm.isValid()) {
                bindingResult.rejectValue("newPassword", "error.user.changepassword.differentcomfirmedpassword", "The password is not the same as the confirmed password");
                return "user/resetpassword";
            }

            final UserPasswordReset token = userService.getToken(resetPasswordForm.getToken());

            if (token == null || !token.isValid(currentTime())) {
                throw new ResourceNotFoundException();
            }

            final boolean authed = userService.canAuthWithNewPassword(token.getUsername(), resetPasswordForm.getNewPassword());
            if (authed) {
                bindingResult.rejectValue("newPassword", "error.user.changepassword.differentcomfirmedpassword", "The password is the same as the previous one");
                return "user/resetpassword";
            }


            final UserAccount userAccount = userService.resetPasswordWithToken(resetPasswordForm.getToken(),
                    resetPasswordForm.getNewPassword(),
                    resetPasswordForm.getConfirmedNewPassword(),
                    httpRequest.getRemoteAddr(),
                    currentTime());

            if (userAccount != null) {
                final Authentication authentication = AuthUtils.getAuthentication(userAccount, new WebAuthenticationDetails(httpRequest), null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                ProviderSignInUtils.handlePostSignUp(userAccount.getUsername(), request);
                return "dashboard/index";
            }
        }

        return "user/resetpassword";
    }
}
