/*
 * Copyright 2013 the original author or authors.
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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.weibo.api.Weibo;
import org.springframework.social.weibo.api.WeiboProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.security.Principal;

@Controller
public class WeiboProfileController {
    private static Logger logger = LoggerFactory.getLogger(WeiboProfileController.class);

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Inject
    private ConnectionRepository connectionRepository;

    @RequestMapping(value = "/weibo", method = RequestMethod.GET)
    public String home(Principal currentUser, Model model) {
        Connection<Weibo> connection = connectionRepository.findPrimaryConnection(Weibo.class);
        if (connection == null) {
            return "redirect:/signin/weibo";
        }
        logger.warn("weibo currentUser: {}", ToStringBuilder.reflectionToString(currentUser, ToStringStyle.MULTI_LINE_STYLE));
        logger.warn("weibo connection: {}", ToStringBuilder.reflectionToString(connection, ToStringStyle.MULTI_LINE_STYLE));

//        final WeiboProfile profile = connection.getApi().userOperations().getUserProfileByScreenName("co0der");
        final WeiboProfile profile = connection.getApi().userOperations().getUserProfile();


        model.addAttribute("profile", profile);
        logger.warn("weibo profile: {}", ToStringBuilder.reflectionToString(profile, ToStringStyle.MULTI_LINE_STYLE));
        logger.warn("weibo status: {}", ToStringBuilder.reflectionToString(profile.getStatus(), ToStringStyle.MULTI_LINE_STYLE));
        return "weibo/profile";
    }

}
