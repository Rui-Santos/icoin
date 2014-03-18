package com.icoin.trading.api.fee.domain.received;

import com.homhon.base.domain.model.ValueObjectSupport;

import static com.homhon.util.Asserts.hasLength;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 8/26/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReceivedSource extends ValueObjectSupport<ReceivedSource> {
    private final ReceivedSourceType receivedSourceType;
    private final String sourceSequenceNumber;

    public ReceivedSource(ReceivedSourceType receivedSourceType, String sourceSequenceNumber) {
        notNull(receivedSourceType);
        hasLength(sourceSequenceNumber);
        this.receivedSourceType = receivedSourceType;
        this.sourceSequenceNumber = sourceSequenceNumber;
    }

    public ReceivedSourceType getReceivedSourceType() {
        return receivedSourceType;
    }

    public String getSourceSequenceNumber() {
        return sourceSequenceNumber;
    }
}