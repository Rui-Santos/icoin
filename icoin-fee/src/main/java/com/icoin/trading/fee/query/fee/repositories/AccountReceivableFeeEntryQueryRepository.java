package com.icoin.trading.fee.query.fee.repositories;

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.fee.query.fee.AccountReceivableFeeEntry;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-9-21
 * Time: PM5:49
 * To change this template use File | Settings | File Templates.
 */
public interface AccountReceivableFeeEntryQueryRepository
        extends GenericCrudRepository<AccountReceivableFeeEntry, String>,
        PagingAndSortingRepository<AccountReceivableFeeEntry, String> {

}
