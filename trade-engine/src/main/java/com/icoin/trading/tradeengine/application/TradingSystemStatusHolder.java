package com.icoin.trading.tradeengine.application;

import com.icoin.trading.api.tradeengine.domain.ChangedReason;
import com.icoin.trading.tradeengine.domain.TradingSystemService;
import com.icoin.trading.tradeengine.domain.model.admin.TradingSystemStatus;
import com.icoin.trading.tradeengine.domain.model.admin.TradingSystemStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-7
 * Time: AM12:08
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TradingSystemStatusHolder implements TradingSystemService {
    private ReadWriteLock rw = new ReentrantReadWriteLock();
    private TradingSystemStatusRepository repository;
    private Lock readLock = rw.readLock();
    private Lock writeLock = rw.writeLock();
    private TradingSystemStatus status;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public TradingSystemStatusHolder(TradingSystemStatusRepository repository) {
        this.repository = repository;
        init();
    }

    public void init() {
        try {
            writeLock.lock();
            Iterable<TradingSystemStatus> all = repository.findAll();

            if (all == null || !all.iterator().hasNext()) {
                status = new TradingSystemStatus();
                repository.save(status);
                return;
            }

            status = all.iterator().next();

        } finally {
            writeLock.unlock();
        }
    }

    //
    public void reviveTrading(String changedBy, Date changedTime, ChangedReason reason){
        try {
            writeLock.lock();
            status.reviveTrading(changedBy, changedTime, reason);
            repository.save(status);
        } finally {
            writeLock.unlock();
        }
    }

    public void disableTrading(Date allowedToTradeStartTime, String changedBy, Date changedTime, ChangedReason reason){
        try {
            writeLock.lock();
            status.disableTrading(allowedToTradeStartTime, changedBy, changedTime, reason);
            repository.save(status);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public TradingSystemStatus currentStatus() {
        try {
            readLock.lock();
            return status;
        } finally {
            readLock.unlock();
        }
    }
}