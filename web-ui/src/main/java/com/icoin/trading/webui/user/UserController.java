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

import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import com.icoin.trading.webui.user.facade.UserServiceFacade;
import com.icoin.trading.webui.user.form.ChangePasswordForm;
import com.icoin.trading.webui.user.form.ChangeWithdrawPasswordForm;
import com.icoin.trading.webui.user.form.CreateWithdrawPasswordForm;
import com.icoin.trading.webui.user.form.ForgetPasswordForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
        model.addAttribute("changePassword", new ChangePasswordForm());

        if (userService.isWithdrawPasswordSet()) {
            model.addAttribute("changeWithdrawPasswordForm", new ChangeWithdrawPasswordForm());
            return "user/changePassword";
        }

        model.addAttribute("createWithdrawPasswordForm", new CreateWithdrawPasswordForm());
        return "user/changePassword";
    }

    @RequestMapping(value = "/createWithdrawPassword", method = RequestMethod.POST)
    public String createWithdrawPassword(@ModelAttribute("changePassword") @Valid CreateWithdrawPasswordForm createWithdrawPasswordForm,
                                 BindingResult bindingResult,
                                 HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            if (createWithdrawPasswordForm.getWithdrawPassword().equals(createWithdrawPasswordForm.getConfirmedWithdrawPassword())) {
                bindingResult.rejectValue("password", "error.user.changepassword.differentcomfirmedpassword", "The password is not the same as the confirmed password");
                return "changePassword";
            }

            boolean created = userService.createWithdrawPassword(createWithdrawPasswordForm.getWithdrawPassword(),
                    createWithdrawPasswordForm.getWithdrawPassword(),
                    request.getRemoteAddr(),
                    currentTime());

            if(created){
                return "dashboard/index";
            }


        }

        createWithdrawPasswordForm.setConfirmedWithdrawPassword(null);
        createWithdrawPasswordForm.setWithdrawPassword(null);

        return "changePassword";
    }

    @RequestMapping(value = "/changeWithdrawPassword", method = RequestMethod.POST)
    public String changeWithdrawPassword(@ModelAttribute("changeWithdrawPasswordForm") @Valid ChangeWithdrawPasswordForm changeWithdrawPasswordForm,
                                         BindingResult bindingResult,
                                         HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            if (changeWithdrawPasswordForm.getPreviousWithdrawPassword().equals(changeWithdrawPasswordForm.getWithdrawPassword())) {
                bindingResult.rejectValue("previousPassword", "error.user.changepassword.samepassword", "The previous password equals to new password");
                return "changePassword";
            }

            if (changeWithdrawPasswordForm.getConfirmedWithdrawPassword().equals(changeWithdrawPasswordForm.getWithdrawPassword())) {
                bindingResult.rejectValue("password", "error.user.changepassword.differentcomfirmedpassword", "The password is not the same as the confirmed password");
                return "changePassword";
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
    public String changePassword(@ModelAttribute("changePassword") @Valid ChangePasswordForm changePasswordForm,
                                 BindingResult bindingResult,
                                 HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            if (changePasswordForm.getPreviousPassword().equals(changePasswordForm.getNewPassword())) {
                bindingResult.rejectValue("previousPassword", "error.user.changepassword.samepassword", "The previous password equals to new password");
                return "changePassword";
            }

            if (changePasswordForm.getConfirmedNewPassword().equals(changePasswordForm.getNewPassword())) {
                bindingResult.rejectValue("password", "error.user.changepassword.differentcomfirmedpassword", "The password is not the same as the confirmed password");
                return "changePassword";
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

        return "changePassword";
    }

    @RequestMapping(value = "/forgetPassword", method = RequestMethod.GET)
    public String forgetPassword(Model model) {
        model.addAttribute("forgetPassword", new ForgetPasswordForm());

        return "forgetPassword";
    }

    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
    public String forgetPassword(@ModelAttribute("forgetPassword") @Valid ForgetPasswordForm forgetPasswordForm,
                                 HttpServletRequest request,
                                 BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            final UserEntry user = userService.findByEmail(forgetPasswordForm.getEmail());

            if (user == null) {
                bindingResult.rejectValue("email", "error.user.forgetpassword.emailnotexist", "Cannot find the email");
                return "forgetPassword";
            }

            final int resetCount = userService.findPasswordResetCount(user.getUsername(), request.getRemoteAddr(), currentTime());
            if (resetCount >= 3) {
                bindingResult.rejectValue("email", "error.user.forgetpassword.toomanyreset", "Too many reset within 24 hours");
                return "forgetPassword";
            }

            final boolean generated = userService.generateForgetPasswordToken(forgetPasswordForm.getEmail(), request.getRemoteAddr(), currentTime());
            if (!generated) {
                bindingResult.rejectValue("email", "error.user.forgetpassword.cannotgenerate", "Cannot generate the email!");
                return "forgetPassword";
            }
        }


        return "/forgetPasswordEmail";
    }

    @RequestMapping(value = "/passwordReset", method = RequestMethod.POST)
    public String passwordReset(@ModelAttribute("changePassword") @Valid ChangePasswordForm changePasswordForm,
                                BindingResult bindingResult,
                                HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            if (changePasswordForm.getPreviousPassword().equals(changePasswordForm.getNewPassword())) {
                bindingResult.rejectValue("previousPassword", "error.user.changepassword.samepassword", "The previous password equals to new password");
            }

            if (changePasswordForm.getConfirmedNewPassword().equals(changePasswordForm.getNewPassword())) {
                bindingResult.rejectValue("password", "error.user.changepassword.differentcomfirmedpassword", "The password is not the same as the confirmed password");
            }

            userService.changePassword(changePasswordForm.getPreviousPassword(),
                    changePasswordForm.getNewPassword(), changePasswordForm.getConfirmedNewPassword(),
                    request.getRemoteAddr(),
                    currentTime());

            return "dashboard/index";
        }

        changePasswordForm.setPreviousPassword(null);
        changePasswordForm.setNewPassword(null);
        changePasswordForm.setConfirmedNewPassword(null);

        return "user/changePassword";
    }
}
