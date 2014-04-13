package com.icoin.trading.users.infrastructure.persistence.mongo;

import com.icoin.trading.users.domain.model.function.UserPasswordReset;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-6-20
 * Time: PM8:53
 * To change this template use File | Settings | File Templates.
 */
public interface UserPasswordResetRepositoryCustom {


    List<UserPasswordReset> findNotExpiredByEmail(String email, String ip, Date fromDate, Date currentDate);


    List<UserPasswordReset> findNotExpiredByEmail(String email, Date fromDate, Date currentDate);
}
