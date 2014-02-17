package com.icoin.trading.webui.user;

import com.google.common.collect.Sets;
import com.icoin.trading.users.domain.model.user.UserAccount;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Set;

import static com.homhon.util.Collections.isEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-16
 * Time: PM1:52
 * To change this template use File | Settings | File Templates.
 */
public abstract class AuthUtils {
    public static Set<GrantedAuthority> getAuthorities(UserAccount<UserAccount> account) {
        if (account == null || isEmpty(account.getRoles())) {
            return Collections.EMPTY_SET;
        }

        final Set<GrantedAuthority> authorities = Sets.newHashSetWithExpectedSize(account.getRoles().size());
        //AuthUtils.java
        for (String role : account.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return authorities;
    }

    public static Authentication getAuthentication(UserAccount account,Object credentials, Object details) {
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(account, credentials, getAuthorities(account));
        token.setDetails(details);
        return token;
    }
}
