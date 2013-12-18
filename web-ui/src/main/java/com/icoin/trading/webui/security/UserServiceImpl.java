package com.icoin.trading.webui.security;

import com.homhon.base.domain.model.user.User;
import com.homhon.base.domain.service.UserService;
import com.icoin.trading.users.domain.UserAccount;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserQueryRepository userQueryRepository;


    @Override
    public UserAccount getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!authentication.isAuthenticated()){
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (UserAccount.class.isAssignableFrom(principal.getClass()) ) {
            return ((UserAccount) principal);
        }

        return null;
    }

    @Override
    public UserAccount getUserById(String id) {
        return userQueryRepository.findOne(id);
    }

    @Autowired
    public void setUserQueryRepository(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }
}