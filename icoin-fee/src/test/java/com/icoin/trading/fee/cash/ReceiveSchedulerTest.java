package com.icoin.trading.fee.cash;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.fee.domain.cash.PendingCashRepository;
import com.icoin.trading.fee.domain.cash.ReceiveCash;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-7
 * Time: PM1:11
 * To change this template use File | Settings | File Templates.
 */
public class ReceiveSchedulerTest {
    private static int completeCount = 0;
    private static int receivedCount = 0;

    @Test
    public void testStart() throws Exception {
        List<BigDecimal> rates =
                Arrays.asList(
                        BigDecimal.valueOf(44.4),
                        BigDecimal.valueOf(1),
                        BigDecimal.valueOf(10),
                        null,
                        BigDecimal.valueOf(600),
                        null,
                        BigDecimal.valueOf(-0.1),
                        BigDecimal.valueOf(-0.1));

        TestReceiveScheduler scheduler = new TestReceiveScheduler(rates);
        PendingCashRepository cashRepository = mock(PendingCashRepository.class);
        ReceiveCash cash = mock(ReceiveCash.class);
        when(cashRepository.findPending(any(Date.class), anyInt(), anyInt())).thenReturn(ImmutableList.of(cash, cash, cash, cash));

        scheduler.setPendingCashRepository(cashRepository);


        scheduler.start();

        TimeUnit.SECONDS.sleep(5);

        scheduler.stop();

        assertThat(completeCount, greaterThan(0));
        assertThat(receivedCount, greaterThan(completeCount));
        assertThat(receivedCount, greaterThan(completeCount));
        assertThat(receivedCount / 8, anyOf(is(completeCount / 4), is(completeCount / 4 - 1)));
    }

    private static class TestReceiveScheduler extends ReceiveScheduler {
        private List<BigDecimal> rates;

        private TestReceiveScheduler(List<BigDecimal> rates) {
            this.rates = rates;
        }

        @Override
        protected BigDecimal getReceivedAmount(ReceiveCash entity, Date occurringTime) {
            BigDecimal amount = rates.get(receivedCount % rates.size());
            receivedCount++;
            return amount;
        }

        @Override
        protected void complete(ReceiveCash entity, BigDecimal received, Date occurringTime) {
            completeCount++;
        }
    }
}
