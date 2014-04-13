package com.icoin.trading.fee.domain.received;

import com.homhon.base.domain.repository.GenericCrudRepository;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-9-21
 * Time: PM5:49
 * To change this template use File | Settings | File Templates.
 */
public interface ReceivedEntryRepository extends GenericCrudRepository<ReceivedFee, String> {
    ReceivedFee findReceivedEntry(BusinessType businessType,
                                  String businessReferenceId,
                                  ReceivedSource receivedSource);

    List<ReceivedFee> findReceivedForOrder(String orderId);

    void deleteReceivedForOrder(String orderId);
}