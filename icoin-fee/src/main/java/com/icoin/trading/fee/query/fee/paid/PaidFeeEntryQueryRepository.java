package com.icoin.trading.fee.query.fee.paid;

import com.homhon.base.domain.repository.GenericCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-9-21
 * Time: PM5:49
 * To change this template use File | Settings | File Templates.
 */
public interface PaidFeeEntryQueryRepository
        extends GenericCrudRepository<PaidFeeEntry, String>,
        PagingAndSortingRepository<PaidFeeEntry, String> {
    List<PaidFeeEntry> findOffsetPending(Date dueDate);
}
