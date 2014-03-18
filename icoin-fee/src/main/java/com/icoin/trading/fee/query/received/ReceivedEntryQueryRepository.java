package com.icoin.trading.fee.query.received;

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM10:09
 * To change this template use File | Settings | File Templates.
 */
public interface ReceivedEntryQueryRepository
        extends GenericCrudRepository<ReceivedEntry, String>,
        PagingAndSortingRepository<ReceivedEntry, String> {
    List<ReceivedEntry> findReceivedForOrder(String orderId);

    void deleteReceivedForOrder(String orderId);

    ReceivedEntry findReceivedEntry(BusinessType businessType, String businessReferenceId, ReceivedSource receivedSource);
}