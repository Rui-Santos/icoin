package com.icoin.trading.webui.security;

import com.homhon.base.domain.service.UserService;
import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.tradeengine.query.portfolio.repositories.PortfolioQueryRepository;
import com.icoin.trading.users.domain.UserAccount;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/17/13
 * Time: 6:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class UserServiceFacadeImpl implements UserServiceFacade {
    private UserService userService;
//    private UserQueryRepository userRepository;
    private PortfolioQueryRepository portfolioQueryRepository;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

//    @Autowired
//    public void setUserRepository(UserQueryRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @Autowired
    public void setPortfolioQueryRepository(PortfolioQueryRepository portfolioQueryRepository) {
        this.portfolioQueryRepository = portfolioQueryRepository;
    }

    @Override
    public UserAccount currentUser() {
        return (UserAccount)userService.getCurrentUser();
    }

    /**
     * For now we work with only one portfolio per user. This might change in the future.
     *
     * @return The found portfolio for the logged in user.
     */
    public PortfolioEntry obtainPortfolioForUser() {
        UserAccount userAccount = (UserAccount)userService.getCurrentUser();

        if (userAccount == null) {
            return null;
        }

//        UserEntry username = userRepository.findByUsername(userAccount.getUserName());
        return portfolioQueryRepository.findByUserIdentifier(userAccount.getPrimaryKey());
    }
}