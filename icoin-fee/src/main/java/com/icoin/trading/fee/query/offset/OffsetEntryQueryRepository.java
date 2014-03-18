package com.icoin.trading.fee.query.offset;

import com.homhon.base.domain.repository.GenericCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM10:03
 * To change this template use File | Settings | File Templates.
 */
public interface OffsetEntryQueryRepository
        extends GenericCrudRepository<OffsetEntry, String>,
        PagingAndSortingRepository<OffsetEntry, String> {

}
