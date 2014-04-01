package com.icoin.trading.fee.cash;

import com.icoin.trading.fee.domain.cash.Cash;
import com.icoin.trading.fee.domain.cash.PendingCashRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.TimeUtils.currentTime;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-1
 * Time: PM9:24
 * To change this template use File | Settings | File Templates.
 */
public abstract class Scheduler<T extends Cash> {
    private static Logger logger = LoggerFactory.getLogger(Scheduler.class);
    private ScheduledExecutorService scheduler;
    protected PendingCashRepository<T> pendingCashRepository;
    private int initialDelay = 10;
    private int period = 500;

    //    <bean class="com.icoin.trading.fee.application.command.Scheduler" init-method="start">
    public Scheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleWithFixedDelay(
                new Worker(pendingCashRepository),
                initialDelay,
                period,
                TimeUnit.MILLISECONDS);
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
            List<T> pendingEntities = repository.findPending(currentTime(), 0, 100);
            Date currentTime = currentTime();
            for (T entity : pendingEntities) {
                try {
                    final BigDecimal received = getReceivedAmount(entity, currentTime);
                    if (received != null && received.compareTo(BigDecimal.ZERO) > 0) {
                        complete(entity, received, currentTime);
                    }
                } catch (Exception e) {
                    logger.error("cannot handle entity {}: {}", entity, entity.describe());
                }
            }
        }
    }
}