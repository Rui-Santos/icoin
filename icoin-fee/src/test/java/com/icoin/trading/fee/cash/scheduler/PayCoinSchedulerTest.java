package com.icoin.trading.fee.cash.scheduler;

import com.google.common.collect.ImmutableList;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.bitcoin.client.BitcoinRpcOperations;
import com.icoin.trading.bitcoin.client.response.StringResponse;
import com.icoin.trading.bitcoin.client.response.ValidateAddressResponse;
import com.icoin.trading.bitcoin.client.response.ValidateAddressResult;
import com.icoin.trading.fee.cash.CashValidator;
import com.icoin.trading.fee.cash.ValidationCode;
import com.icoin.trading.fee.domain.cash.Cash;
import com.icoin.trading.fee.domain.cash.CashStatus;
import com.icoin.trading.fee.domain.cash.CoinPayCash;
import com.icoin.trading.fee.domain.cash.PendingCashRepository;
import com.icoin.trading.fee.domain.paid.PaidFee;
import com.icoin.trading.users.domain.model.user.UserAccount;
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
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-10
 * Time: PM9:21
 * To change this template use File | Settings | File Templates.
 */
public class PayCoinSchedulerTest {
    private static int CREATED_STATUS_COUNT;
    private static int APPROVED_STATUS_COUNT;
    private static int CONFIRMED_COUNT;
    private static int FAILED_CODE_COUNT = 0;
    private static int SUCCESSFUL_CODE_COUNT = 0;

    @Test
    public void testStartWithInvalidAddress() throws Exception {
        PayCoinScheduler scheduler = new PayCoinScheduler();

        final Random random = new Random();

        PendingCashRepository cashRepository = mock(PendingCashRepository.class);
        CoinPayCash cash = mock(CoinPayCash.class);
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


        BitcoinRpcOperations operations = mock(BitcoinRpcOperations.class);
        final ValidateAddressResponse response = createResponse(false);

        when(operations.validateAddress(anyString())).thenReturn(response);


        scheduler.setPendingCashRepository(cashRepository);
        scheduler.setPaidFeeRepository(paidFeeRepository);
        scheduler.setOperations(operations);

        scheduler.start();

        TimeUnit.SECONDS.sleep(5);
        scheduler.stop();

        assertThat(CONFIRMED_COUNT, lessThan(APPROVED_STATUS_COUNT));
        assertThat(CONFIRMED_COUNT, lessThan(CREATED_STATUS_COUNT));

        verify(cashRepository, never()).save(any(Cash.class));
        verify(fee, never()).confirm(anyString(), any(Date.class));
        verify(fee).cancel(eq(CancelledReason.INVALID_ADDRESS), any(Date.class));
        verify(cash, never()).confirm(anyString(), any(Date.class));
    }

    private ValidateAddressResponse createResponse(boolean valid) {
        final ValidateAddressResponse response = mock(ValidateAddressResponse.class);
        final ValidateAddressResult result = mock(ValidateAddressResult.class);
        when(result.getValid()).thenReturn(valid);
        when(response.getResult()).thenReturn(result);
        return response;
    }


    @Test
    public void testStartWithValidAddress() throws Exception {
        final String username = "Bjorn";
        final String commentTo = "sending from iCoin";
        PayCoinScheduler scheduler = new PayCoinScheduler();

        final Random random = new Random();

        PendingCashRepository cashRepository = mock(PendingCashRepository.class);
        CoinPayCash cash = mock(CoinPayCash.class);
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

        CashValidator validator = mock(CashValidator.class);
        when(validator.canCreate(any(UserAccount.class), anyString(), any(BigMoney.class), any(Date.class)))
                .thenAnswer(new Answer<ValidationCode>() {
                    @Override
                    public ValidationCode answer(InvocationOnMock invocation) throws Throwable {
                        int count = random.nextInt(6);
                        switch (count) {
                            case 0:
                                FAILED_CODE_COUNT++;
                                return ValidationCode.EXECUTION_ERROR;
                            case 1:
                                FAILED_CODE_COUNT++;
                                return ValidationCode.SYSTEM_DISALLOWED;
                            case 2:
                                SUCCESSFUL_CODE_COUNT++;
                                return ValidationCode.SUCCESSFUL;
                            default:
                                SUCCESSFUL_CODE_COUNT++;
                                return null;
                        }
                    }
                });


        BitcoinRpcOperations operations = mock(BitcoinRpcOperations.class);
        when(operations.validateAddress(anyString())).thenReturn(createResponse(true));
        final StringResponse stringResponse = mock(StringResponse.class);
        when(stringResponse.getResult()).thenReturn("txid");
        when(operations.sendToAddress(anyString(), any(BigDecimal.class), eq("apy for " + username), eq(commentTo)))
                .thenReturn(stringResponse);

        scheduler.setPendingCashRepository(cashRepository);
        scheduler.setPaidFeeRepository(paidFeeRepository);
        scheduler.setOperations(operations);

        scheduler.start();
        TimeUnit.SECONDS.sleep(5);

        scheduler.stop();

        assertThat(CONFIRMED_COUNT, lessThan(APPROVED_STATUS_COUNT));
        assertThat(CONFIRMED_COUNT, lessThan(CREATED_STATUS_COUNT));

        verify(cashRepository, times(SUCCESSFUL_CODE_COUNT)).save(any(Cash.class));
        verify(fee, times(SUCCESSFUL_CODE_COUNT)).confirm(eq("paid123"), any(Date.class));
        verify(fee, never()).cancel(eq(CancelledReason.INVALID_ADDRESS), any(Date.class));
        verify(cash, times(SUCCESSFUL_CODE_COUNT)).confirm(eq("txid"), any(Date.class));
        verify(cashRepository, times(SUCCESSFUL_CODE_COUNT)).save(any(Cash.class));
    }
}
