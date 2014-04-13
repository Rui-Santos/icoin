package com.icoin.trading.fee.query.fee.received;

import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeCreatedEvent;
import com.icoin.trading.fee.query.fee.FeeEntry;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 9/10/13
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
//@CompoundIndexes({
//        @CompoundIndex(name = "portfolioActivity_user_type", def = "{'username': 1, 'type': 1}", unique = true),
//        @CompoundIndex(name = "portfolioActivity_portfolio_type", def = "{'portfolioId': 1, 'type': 1}", unique = true)
//})
public class ReceivedFeeEntry extends FeeEntry<ReceivedFeeEntry> {
    private ReceivedSource receivedSource;

    public void copy(ReceivedFeeCreatedEvent event) {
        super.copy(event);
        receivedSource = event.getReceivedSource();
    }

    public ReceivedSource getReceivedSource() {
        return receivedSource;
    }

    public void setReceivedSource(ReceivedSource receivedSource) {
        this.receivedSource = receivedSource;
    }
}

