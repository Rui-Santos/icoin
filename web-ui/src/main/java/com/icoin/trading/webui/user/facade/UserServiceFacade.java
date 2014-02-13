package com.icoin.trading.webui.user.facade;

import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.users.domain.model.user.UserAccount;
import com.icoin.trading.users.query.UserEntry;

import java.util.Date;

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

    void changePassword(String previousPassword,
                        String newPassword,
                        String confirmedNewPassword,
                        String operatingIp);

    void resetPasswordWithToken(String token,
                                String password,
                                String confirmedPassword,
                                String operatingIp);

    boolean generateForgetPasswordToken(String email,
                                       String operatingIp,
                                       Date currentTime);

    void changeWithdrawPassword(String previousPassword,
                                String withdrawPassword,
                                String confirmedWithdrawPassword,
                                String operatingIp);

    UserEntry findByEmail(String email);

    int findPasswordResetCount(String username, String ip, Date currentDate);
}