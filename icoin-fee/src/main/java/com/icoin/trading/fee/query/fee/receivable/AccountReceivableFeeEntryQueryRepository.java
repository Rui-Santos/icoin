package com.icoin.trading.fee.query.fee.receivable;

import com.homhon.base.domain.repository.GenericCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

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
    //today's total money for pay
    List<AccountReceivableFeeEntry> findConfirmedByUserAccountId(String userAccountId);
}
