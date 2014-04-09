package com.icoin.trading.fee.cash.scheduler;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.bitcoin.client.BitcoinRpcOperations;
import com.icoin.trading.bitcoin.client.response.BigDecimalResponse;
import com.icoin.trading.fee.cash.CashValidator;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.domain.DueDateService;
import com.icoin.trading.fee.domain.address.Address;
import com.icoin.trading.fee.domain.cash.CoinReceiveCash;
import com.icoin.trading.fee.domain.cash.PendingCashRepository;
import com.icoin.trading.fee.domain.transaction.CoinTransferringInTransaction;
import com.icoin.trading.users.domain.model.user.UserAccount;
import com.icoin.trading.users.query.UserEntry;
import com.icoin.trading.users.query.repositories.UserQueryRepository;
import org.axonframework.repository.Repository;
import org.joda.money.BigMoney;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-7
 * Time: PM1:15
 * To change this template use File | Settings | File Templates.
 */
public class ReceivedCoinSchedulerTest {
    private static int ansawredCount = 0;
    private static int nullRepsonseCount = 0;
    private static int nullBigDecimalCount = 0;
    private static int zeroBigDecimalCount = 0;
    private static int nagativeBigDecimalCount = 0;
    private static int failedCodeCount = 0;
    private static int successfulCodeCount = 0;

    private static Random random = new Random();

    @Test
    public void test() throws Exception {
        final int minConfirmations = 4;

        BitcoinRpcOperations operations = mock(BitcoinRpcOperations.class);
        when(operations.getReceivedByAddress(anyString(), eq(4))).thenAnswer(
                new Answer<BigDecimalResponse>() {
                    @Override
                    public BigDecimalResponse answer(InvocationOnMock invocation) throws Throwable {
                        int count = random.nextInt(6);
                        ansawredCount++;
                        switch (count) {
                            case 0:
                                nullRepsonseCount++;
                                return null;
                            case 1:
                                nullBigDecimalCount++;
                                return new BigDecimalResponse(null, null, null);
                            case 2:
                                zeroBigDecimalCount++;
                                return new BigDecimalResponse(BigDecimal.ZERO, null, null);
                            case 3:
                                nagativeBigDecimalCount++;
                                return new BigDecimalResponse(BigDecimal.valueOf(-3.14), null, null);
                            default:
                                return new BigDecimalResponse(BigDecimal.valueOf(count), null, null);
                        }
                    }
                }
        );


        CashValidator validator = mock(CashValidator.class);
        when(validator.canCreate(any(UserAccount.class), anyString(), any(BigMoney.class), any(Date.class)))
                .thenAnswer(new Answer<ValidationCode>() {
                    @Override
                    public ValidationCode answer(InvocationOnMock invocation) throws Throwable {
                        int count = random.nextInt(6);
                        switch (count) {
                            case 0:
                                failedCodeCount++;
                                return ValidationCode.EXECUTION_ERROR;
                            case 1:
                                failedCodeCount++;
                                return ValidationCode.SYSTEM_DISALLOWED;
                            case 2:
                                successfulCodeCount++;
                                return ValidationCode.SUCCESSFUL;
                            default:
                                successfulCodeCount++;
                                return null;
                        }
                    }
                });


        Repository<CoinTransferringInTransaction> repository = mock(Repository.class);
        UserQueryRepository userQueryRepository = mock(UserQueryRepository.class);
        final UserEntry user = new UserEntry();
        final DueDateService dueDateService = mock(DueDateService.class);
        when(userQueryRepository.findOne(eq("john"))).thenReturn(user);

        PendingCashRepository cashRepository = mock(PendingCashRepository.class);
        CoinReceiveCash cash = new CoinReceiveCash();
        cash.setAddress(new Address("1JEiV9CiJmhfYhE7MzeSdmH82xRYrbYrtb"));
        when(cashRepository.findPending(any(Date.class), anyInt(), anyInt()))
                .thenReturn(ImmutableList.of(cash, cash, cash, cash));

        ReceivedCoinScheduler scheduler = new ReceivedCoinScheduler();
        scheduler.setMinConfirmations(minConfirmations);
        scheduler.setOperations(operations);
        scheduler.setCashValidator(validator);
        scheduler.setPendingCashRepository(cashRepository);
        scheduler.setRepository(repository);
        scheduler.setUserQueryRepository(userQueryRepository);
        scheduler.setDueDateService(dueDateService);

        scheduler.start();

        TimeUnit.SECONDS.sleep(5);

        scheduler.stop();

        int called = ansawredCount -
                (nullRepsonseCount + nullBigDecimalCount + zeroBigDecimalCount + nagativeBigDecimalCount
                        + failedCodeCount);

        assertThat(ansawredCount, greaterThan(0));
        assertThat(successfulCodeCount, is(called));

        verify(validator, times(called + failedCodeCount)).canCreate(any(UserAccount.class), anyString(), any(BigMoney.class), any(Date.class));
        verify(repository, times(called)).add(any(CoinTransferringInTransaction.class));
    }
}
