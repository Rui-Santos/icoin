package com.icoin.trading.fee.domain.cash;

import com.homhon.base.domain.repository.GenericCrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM10:15
 * To change this template use File | Settings | File Templates.
 */
public interface CashRepository<T extends Cash> extends GenericCrudRepository<T, String> {
    List<T> findByUserId(String userId);
    List<T> findByUserId(String userId, Date date);
}
