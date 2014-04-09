package com.icoin.trading.fee.domain.cash;

import com.homhon.base.domain.repository.GenericCrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM9:37
 * To change this template use File | Settings | File Templates.
 */
public interface PendingCashRepository<T extends Cash> extends GenericCrudRepository<T, String> {
    List<Cash> findPending(Date toDate, int start, int size);
}
