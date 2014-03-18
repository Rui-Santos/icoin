package com.icoin.trading.fee.query.offset;

import com.icoin.trading.api.fee.domain.offset.OffsetStatus;
import com.icoin.trading.api.fee.events.offset.FeesOffsetedEvent;
import com.icoin.trading.api.fee.events.offset.OffsetCancelledEvent;
import com.icoin.trading.api.fee.events.offset.OffsetCreatedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/18/14
 * Time: 6:36 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OffsetListener {
    private static Logger logger = LoggerFactory.getLogger(OffsetListener.class);
    private OffsetEntryQueryRepository repository;

    @EventHandler
    public void handleCreated(OffsetCreatedEvent event) {
        OffsetEntry entry = new OffsetEntry();
        entry.setPrimaryKey(event.getOffsetId().toString());
        entry.setOffsetType(event.getOffsetType());
        entry.setAccountId(event.getAccountId());
        entry.setArapList(event.getArapList());
        entry.setReceivedPaidList(event.getReceivedPaidList());
        entry.setOffsetAmount(event.getOffsetAmount());

        repository.save(entry);
    }

    @EventHandler
    public void handleOffseted(FeesOffsetedEvent event) {
        final OffsetEntry entry = repository.findOne(event.getOffsetId().toString());

        if (entry == null) {
            logger.error("Entry not found with id {}", event.getOffsetId());
        }

        entry.setOffsetDate(event.getOffsetedDate());
        entry.setOffsetStatus(OffsetStatus.OFFSETED);
        repository.save(entry);
    }

    @EventHandler
    public void handleCancelled(OffsetCancelledEvent event) {
        final OffsetEntry entry = repository.findOne(event.getOffsetId().toString());

        if (entry == null) {
            logger.error("Entry not found with id {}", event.getOffsetId());
        }

        entry.setCancelledDate(event.getCancelledDate());
        entry.setOffsetStatus(OffsetStatus.CANCELLED);
        repository.save(entry);
    }


    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public void setRepository(OffsetEntryQueryRepository repository) {
        this.repository = repository;
    }
}