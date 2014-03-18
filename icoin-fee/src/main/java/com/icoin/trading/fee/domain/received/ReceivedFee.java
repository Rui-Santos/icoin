package com.icoin.trading.fee.domain.received;

import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.fee.domain.fee.FeeAggregateRoot;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 4/10/13
 * Time: 3:14 PM
 */
public class ReceivedFee extends FeeAggregateRoot<ReceivedFee> {
    private ReceivedSource receivedSource;


}