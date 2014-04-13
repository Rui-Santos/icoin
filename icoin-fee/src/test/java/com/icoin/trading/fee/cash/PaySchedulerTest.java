package com.icoin.trading.fee.cash;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.fee.domain.cash.Cash;
import com.icoin.trading.fee.domain.cash.CashStatus;
import com.icoin.trading.fee.domain.cash.PayCash;
import com.icoin.trading.fee.domain.cash.PendingCashRepository;
import com.icoin.trading.fee.domain.paid.PaidFee;
import org.axonframework.repository.Repository;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-10
 * Time: PM9:35
 * To change this template use File | Settings | File Templates.
 */
public class PaySchedulerTest {
    private static int CREATED_STATUS_COUNT;
    private static int APPROVED_STATUS_COUNT;
    private static int CONFIRMED_COUNT;

    @Test
    public void testStart() throws Exception {
        TestPayScheduler scheduler = new TestPayScheduler();

        final Random random = new Random();

        PendingCashRepository cashRepository = mock(PendingCashRepository.class);
        PayCash cash = mock(PayCash.class);
        when(cashRepository.findPending(any(Date.class), anyInt(), anyInt()))
                .thenReturn(ImmutableList.of(cash, cash, cash, cash));

        when(cash.isApproved()).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {

                int rand = random.nextInt(2);

                switch (rand) {
                    case 0:
                        APPROVED_STATUS_COUNT++;
                        return true;
                    default:
                        return false;
                }
            }
        });

        when(cash.getStatus()).thenAnswer(new Answer<CashStatus>() {
            @Override
            public CashStatus answer(InvocationOnMock invocation) throws Throwable {
                int rand = random.nextInt(2);

                switch (rand) {
                    case 0:
                        return CashStatus.CANCELLED;
                    default:
                        CREATED_STATUS_COUNT++;
                        return CashStatus.CREATED;
                }
            }
        });

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CONFIRMED_COUNT++;
                return null;
            }
        }).when(cash).confirm(eq("paid123"), any(Date.class));


        Repository<PaidFee> paidFeeRepository = mock(Repository.class);
        PaidFee fee = mock(PaidFee.class);
        when(fee.isOffseted()).thenReturn(true);
        when(fee.isPending()).thenReturn(true);


        when(paidFeeRepository.load(anyString())).thenReturn(fee);

        scheduler.setPendingCashRepository(cashRepository);
        scheduler.setPaidFeeRepository(paidFeeRepository);


        scheduler.start();
        TimeUnit.SECONDS.sleep(5);

        scheduler.stop();

        assertThat(CONFIRMED_COUNT, lessThan(APPROVED_STATUS_COUNT));
        assertThat(CONFIRMED_COUNT, lessThan(CREATED_STATUS_COUNT));


        verify(cashRepository, times(CONFIRMED_COUNT)).save(any(Cash.class));
        verify(fee, times(CONFIRMED_COUNT)).confirm(eq("paid123"), any(Date.class));
    }


    @Test
    public void testStartWithAllConfirmed() throws Exception {
        TestPayScheduler scheduler = new TestPayScheduler();

        final Random random = new Random();

        PendingCashRepository cashRepository = mock(PendingCashRepository.class);
        PayCash cash = mock(PayCash.class);
        when(cashRepository.findPending(any(Date.class), anyInt(), anyInt()))
                .thenReturn(ImmutableList.of(cash, cash, cash, cash));

        when(cash.isApproved()).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {

                int rand = random.nextInt(2);

                switch (rand) {
                    case 0:
                        APPROVED_STATUS_COUNT++;
                        return true;
                    default:
                        return false;
                }
            }
        });

        when(cash.getStatus()).thenAnswer(new Answer<CashStatus>() {
            @Override
            public CashStatus answer(InvocationOnMock invocation) throws Throwable {
                int rand = random.nextInt(2);

                switch (rand) {
                    case 0:
                        return CashStatus.CANCELLED;
                    default:
                        CREATED_STATUS_COUNT++;
                        return CashStatus.CREATED;
                }
            }
        });

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CONFIRMED_COUNT++;
                return null;
            }
        }).when(cash).confirm(eq("paid123"), any(Date.class));


        Repository<PaidFee> paidFeeRepository = mock(Repository.class);
        PaidFee fee = mock(PaidFee.class);
        when(fee.isOffseted()).thenReturn(true);
        when(fee.isPending()).thenReturn(false);
        when(fee.isConfirmed()).thenReturn(true);

        when(paidFeeRepository.load(anyString())).thenReturn(fee);

        scheduler.setPendingCashRepository(cashRepository);
        scheduler.setPaidFeeRepository(paidFeeRepository);

        scheduler.start();
        TimeUnit.SECONDS.sleep(5);

        scheduler.stop();

        assertThat(CONFIRMED_COUNT, lessThan(APPROVED_STATUS_COUNT));
        assertThat(CONFIRMED_COUNT, lessThan(CREATED_STATUS_COUNT));

        verify(cashRepository, times(CONFIRMED_COUNT)).save(any(Cash.class));
        verify(fee, times(CONFIRMED_COUNT)).isConfirmed();
    }

    private static class TestPayScheduler extends PayScheduler {

        @Override
        protected String pay(PayCash entity, Date occurringTime) {
            return "paid123";
        }
    }
}
