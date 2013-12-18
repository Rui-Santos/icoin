package com.icoin.trading.webui.security;

import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.users.domain.UserAccount;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/17/13
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserServiceFacade {
    UserAccount currentUser();

    PortfolioEntry obtainPortfolioForUser();
}