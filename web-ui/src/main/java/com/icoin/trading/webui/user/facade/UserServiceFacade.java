package com.icoin.trading.webui.user.facade;

import com.icoin.trading.tradeengine.query.portfolio.PortfolioEntry;
import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.api.users.domain.Identifier;
import com.icoin.trading.users.domain.model.user.UserAccount;
import com.icoin.trading.users.query.UserEntry;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 12/17/13
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserServiceFacade {
    UserAccount currentUser();

    UserEntry currentDetailUser();

    boolean isWithdrawPasswordSet();

    boolean isWithdrawPasswordMatched(String withdrawPassword);

    boolean canAuthWithNewPassword(String username, String newPassword);

    UserPasswordReset getToken(String token);

    PortfolioEntry obtainPortfolioForUser();

    UserEntry findByEmail(String email);

    int findPasswordResetCount(String username, String ip, Date currentDate);

    UserAccount resetPasswordWithToken(String token,
                                String password,
                                String confirmedPassword,
                                String operatingIp,
                                Date resetTime);

    boolean generateForgetPasswordToken(String email,
                                       String operatingIp,
                                       Date currentTime);

    boolean matchPreviousPassword(String previousPassword);

    void editDetails(String username,
                     String email,
                     String cellPhoneNumber,
                     String firstName,
                     String lastName);

    public void editAdminDetails(String username,
                                 String email,
                                 Identifier identifier,
                                 String cellPhoneNumber,
                                 String firstName,
                                 String lastName,
                                 List<String> roles);

    void updateNotificationSettings(boolean logonAlert,
                                    boolean withdrawMoneyAlert,
                                    boolean withdrawItemAlert,
                                    boolean executedAlert);

    void changePassword(String previousPassword,
                        String newPassword,
                        String confirmedNewPassword,
                        String operatingIp,
                        Date changedTime);

    boolean createWithdrawPassword(String withdrawPassword,
                                String confirmedWithdrawPassword,
                                String operatingIp,
                                Date changedTime);

    boolean matchPreviousWithdrawPassword(String previousPassword);

    void changeWithdrawPassword(String previousPassword,
                                String withdrawPassword,
                                String confirmedWithdrawPassword,
                                String operatingIp,
                                Date changedTime);
}