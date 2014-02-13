package com.icoin.trading.users.domain.model.function;

import com.homhon.base.domain.repository.GenericCrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 1/28/14
 * Time: 6:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserPasswordResetRepository extends GenericCrudRepository<UserPasswordReset, String> {
    List<UserPasswordReset> findNotExpiredByUsername(String username, String ip, Date fromDate, Date currentDate);

    List<UserPasswordReset> findByUsername(String username);

    UserPasswordReset findByToken(String token);
}
