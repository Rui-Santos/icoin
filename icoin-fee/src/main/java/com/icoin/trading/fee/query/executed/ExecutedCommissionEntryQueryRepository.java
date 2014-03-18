package com.icoin.trading.fee.query.executed;

import com.homhon.base.domain.repository.GenericCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM10:09
 * To change this template use File | Settings | File Templates.
 */
public interface ExecutedCommissionEntryQueryRepository
        extends GenericCrudRepository<ExecutedCommissionEntry, String>,
        PagingAndSortingRepository<ExecutedCommissionEntry, String> {

}
