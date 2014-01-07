package com.icoin.trading.webui.security;

import org.springframework.social.weibo.api.Weibo;
import org.springframework.social.weibo.api.WeiboProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

@Controller
public class WeiboConnectionsController {

    private Weibo weibo;

    @Inject
    public WeiboConnectionsController(Weibo weibo) {
        this.weibo = weibo;
    }

    @RequestMapping(value = "/weibo/connections", method = RequestMethod.GET)
    public String connections(Model model) {
        long uid = 0l;
        final List<WeiboProfile> activeFollowers = weibo.friendOperations().getActiveFollowers(uid);

        HashMap<String, String> followers = new HashMap<String, String>();

        for (WeiboProfile activeFollower : activeFollowers) {
            followers.put(activeFollower.getName(), activeFollower.getScreenName());
        }

        model.addAttribute("followers", followers);
        return "weibo/connections";
    }

}
