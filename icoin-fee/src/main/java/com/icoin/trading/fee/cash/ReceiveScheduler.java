package com.icoin.trading.fee.cash;

import com.icoin.trading.fee.domain.cash.Cash;
import com.icoin.trading.fee.domain.cash.PendingCashRepository;
import com.icoin.trading.fee.domain.cash.ReceiveCash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.TimeUtils.currentTime;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM9:24
 * To change this template use File | Settings | File Templates.
 */
public abstract class ReceiveScheduler<T extends ReceiveCash> {
    private static Logger logger = LoggerFactory.getLogger(ReceiveScheduler.class);
    private ScheduledExecutorService scheduler;
    protected PendingCashRepository<T> pendingCashRepository;
    private int initialDelay = 10;
    private int period = 500;
    private AtomicBoolean stop = new AtomicBoolean(false);

    //    <bean class="com.icoin.trading.fee.application.command.ReceiveScheduler" init-method="start">
    public ReceiveScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleWithFixedDelay(
                new Worker(pendingCashRepository),
                initialDelay,
                period,
                TimeUnit.MILLISECONDS);
    }

    public void stop() {
        stop.set(true);
    }

    public void setPendingCashRepository(PendingCashRepository<T> pendingRepository) {
        this.pendingCashRepository = pendingRepository;
    }

    protected abstract BigDecimal getReceivedAmount(T entity, Date occurringTime);

    protected abstract void complete(T entity, BigDecimal received, Date occurringTime);


    private class Worker implements Runnable {
        private final PendingCashRepository repository;

        private Worker(PendingCashRepository repository) {
            notNull(repository);
            this.repository = repository;
        }

        @Override
        public void run() {
            if (stop.get()) {
                return;
            }
            Date currentTime = currentTime();
            List<T> pendingEntities = repository.findPending(currentTime, 0, 100);
            for (T entity : pendingEntities) {
                handle(currentTime, entity);
            }
        }

        private void handle(Date currentTime, T entity) {
            try {
                final BigDecimal received = getReceivedAmount(entity, currentTime);
                if (received != null && received.compareTo(BigDecimal.ZERO) > 0) {
                    complete(entity, received, currentTime);
                }
            } catch (Exception e) {
                logger.error("cannot handle entity {}: {}", entity, entity.describe(), e);
            }
        }
    }
}