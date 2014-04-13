package com.icoin.trading.fee.query.fee.payable;

import com.homhon.base.domain.repository.GenericCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-7
 * Time: PM1:31
 * To change this template use File | Settings | File Templates.
 */
public interface AccountPayableFeeEntryQueryRepository
        extends GenericCrudRepository<AccountPayableFeeEntry, String>,
        PagingAndSortingRepository<AccountPayableFeeEntry, String> {
    //today's total money for pay
    List<AccountPayableFeeEntry> findConfirmedByUserAccountId(String userAccountId);
}
