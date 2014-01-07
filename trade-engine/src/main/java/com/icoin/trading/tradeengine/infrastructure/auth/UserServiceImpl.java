package com.icoin.trading.tradeengine.infrastructure.auth;


import com.homhon.base.domain.service.UserService;
import com.icoin.trading.users.domain.model.user.UserAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-17
 * Time: PM9:36
 * To change this template use File | Settings | File Templates.
 */
public class UserServiceImpl implements UserService {

    @Override
    public UserAccount getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (UserAccount.class.isAssignableFrom(principal.getClass())) {
            return ((UserAccount) principal);
        }

        return null;
    }
}