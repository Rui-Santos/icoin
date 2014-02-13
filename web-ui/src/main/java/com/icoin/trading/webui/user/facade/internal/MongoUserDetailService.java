package com.icoin.trading.webui.user.facade.internal;

import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.homhon.util.Strings.hasLength;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-1-16
 * Time: AM8:55
 * To change this template use File | Settings | File Templates.
 */
public class MongoUserDetailService implements UserDetailsService {
    private UserQueryRepository userQueryRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    public MongoUserDetailService(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }

//    @Autowired
//    public void setUserQueryRepository(UserQueryRepository userQueryRepository) {
//        this.userQueryRepository = userQueryRepository;
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!hasLength(username)) {
            return null;
        }

        final UserEntry user = userQueryRepository.findByUsername(username);

        return new SocialUser(user.getUsername(),
                user.getPassword(),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
