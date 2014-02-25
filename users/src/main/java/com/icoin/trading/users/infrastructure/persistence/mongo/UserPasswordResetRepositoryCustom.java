package com.icoin.trading.users.infrastructure.persistence.mongo;

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.users.domain.model.function.UserPasswordReset;
import com.icoin.trading.users.domain.model.function.UserPasswordResetRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

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

    @Query(value = "{ 'email' : ?0 , " +
            "'ip' : ?1 , " +
            "'expirationDate' : {'$gte': ?2, '$lt': ?3} }, " +
            "Sort: { 'expirationDate' : -1 }")
    List<UserPasswordReset> findNotExpiredByEmail(String email, String ip, Date fromDate, Date currentDate);

    @Query(value = "{ 'email' : ?0 , " +
            "'expirationDate' : {'$gte': ?1, '$lt': ?2} }, " +
            "Sort: { 'expirationDate' : -1 }")
    List<UserPasswordReset> findNotExpiredByEmail(String email, Date fromDate, Date currentDate);
}