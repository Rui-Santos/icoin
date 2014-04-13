package com.icoin.trading.fee.cash;

import com.icoin.trading.fee.domain.cash.CashStatus;
import com.icoin.trading.fee.domain.cash.PayCash;
import com.icoin.trading.fee.domain.cash.PendingCashRepository;
import com.icoin.trading.fee.domain.paid.PaidFee;
import org.axonframework.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.Strings.hasText;
import static com.homhon.util.TimeUtils.currentTime;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-9
 * Time: PM9:16
 * To change this template use File | Settings | File Templates.
 */
public abstract class PayScheduler<T extends PayCash> {
    private static Logger logger = LoggerFactory.getLogger(PayScheduler.class);
    private ScheduledExecutorService scheduler;
    protected PendingCashRepository<T> pendingCashRepository;
    protected Repository<PaidFee> paidFeeRepository;
    private int initialDelay = 10;
    private int period = 500;
    private AtomicBoolean stop = new AtomicBoolean(false);

    //    <bean class="com.icoin.trading.fee.application.command.Scheduler" init-method="start">
    public PayScheduler() {
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

    public void setPaidFeeRepository(Repository<PaidFee> paidFeeRepository) {
        this.paidFeeRepository = paidFeeRepository;
    }

    protected abstract String pay(T entity, Date occurringTime);

    protected void complete(T entity, String sequenceNumber, Date occurringTime) {
        PaidFee paidFee = paidFeeRepository.load(entity.getPrimaryKey());
        paidFee.confirm(sequenceNumber, occurringTime);

        entity.confirm(sequenceNumber, occurringTime);
        pendingCashRepository.save(entity);
    }


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
            List<T> pendingEntities = repository.findPending(currentTime(), 0, 100);
            Date currentTime = currentTime();
            for (T entity : pendingEntities) {
                if (entity.isApproved() && entity.getStatus() == CashStatus.CREATED) {
                    handle(currentTime, entity);
                }
            }
        }

        private void handle(Date currentTime, T entity) {
            try {
                PaidFee paidFee = paidFeeRepository.load(entity.getPrimaryKey());

                if (paidFee.isOffseted() && paidFee.isPending()) {
                    final String sequenceNumber = pay(entity, currentTime);
                    if (!hasText(sequenceNumber)) {
                        logger.error("Cannot get sequence number from pay at {} : {}", currentTime, entity.describe());
                        return;
                    }

                    complete(entity, sequenceNumber, currentTime);
                }
                if (paidFee.isOffseted() && paidFee.isConfirmed()) {
                    entity.confirm(paidFee.getSequenceNumber(), paidFee.getConfirmedDate());
                    pendingCashRepository.save(entity);
                }
            } catch (Exception e) {
                logger.error("cannot handle entity {}: {}", entity, entity.describe());
            }
        }
    }
}
