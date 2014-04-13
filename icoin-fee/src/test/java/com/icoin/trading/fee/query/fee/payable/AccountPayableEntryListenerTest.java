package com.icoin.trading.fee.query.fee.payable;

import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeOffsetedEvent;
import org.joda.money.BigMoney;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-13
 * Time: PM5:34
 * To change this template use File | Settings | File Templates.
 */
public class AccountPayableEntryListenerTest {
    @Test
    public void testHandleCreated() throws Exception {
        FeeId feeId = new FeeId();
        FeeStatus feeStatus = FeeStatus.PENDING;
        BigMoney amount = BigMoney.parse("BTC 30");
        FeeType feeType = FeeType.PAY_COIN;
        Date dueDate = new Date();
        Date businessCreationTime = new Date();
        String portfolioId = "portfolioId";
        String userId = "userId";
        BusinessType businessType = BusinessType.CHARGE_COIN_COMMISSION;
        String businessReferenceId = "businessReferenceId";
        AccountPayableFeeEntryQueryRepository repository = mock(AccountPayableFeeEntryQueryRepository.class);

        AccountPayableFeeCreatedEvent event =
                new AccountPayableFeeCreatedEvent(feeId, feeStatus, amount, feeType, dueDate, businessCreationTime, portfolioId, userId, businessType, businessReferenceId);

        AccountPayableEntryListener listener = new AccountPayableEntryListener();
        listener.setRepository(repository);

        listener.handleCreated(event);

        ArgumentCaptor<AccountPayableFeeEntry> captor = ArgumentCaptor.forClass(AccountPayableFeeEntry.class);
        verify(repository).save(captor.capture());

//        AccountPayableFeeEntry entity = captor.getValue();
        AccountPayableFeeEntry entity = captor.getValue();

        assertThat(entity.getPrimaryKey(), equalTo(feeId.toString()));
        assertThat(entity.getFeeStatus(), is(feeStatus));
        assertThat(entity.getAmount(), equalTo(amount));
        assertThat(entity.getFeeType(), equalTo(feeType));
        assertThat(entity.getDueDate(), equalTo(dueDate));
        assertThat(entity.getBusinessCreationTime(), equalTo(businessCreationTime));
        assertThat(entity.getPortfolioId(), equalTo(portfolioId));
        assertThat(entity.getUserId(), equalTo(userId));
        assertThat(entity.getBusinessType(), equalTo(businessType));
        assertThat(entity.getBusinessReferenceId(), equalTo(businessReferenceId));
    }

    @Test
    public void testHandleConfirmed() throws Exception {
        FeeId feeId = new FeeId("primaryKey");
        Date confirmedDate = new Date();

        AccountPayableEntryListener listener = new AccountPayableEntryListener();

        AccountPayableFeeEntryQueryRepository repository = mock(AccountPayableFeeEntryQueryRepository.class);
        when(repository.findOne(eq("primaryKey"))).thenReturn(new AccountPayableFeeEntry());

        listener.setRepository(repository);

        AccountPayableFeeConfirmedEvent event = new AccountPayableFeeConfirmedEvent(feeId, confirmedDate);
        listener.handleConfirmed(event);

        ArgumentCaptor<AccountPayableFeeEntry> captor = ArgumentCaptor.forClass(AccountPayableFeeEntry.class);
        verify(repository).save(captor.capture());

        AccountPayableFeeEntry entity = captor.getValue();

        assertThat(entity.getConfirmedDate(), equalTo(confirmedDate));
        assertThat(entity.getFeeStatus(), is(FeeStatus.CONFIRMED));
    }

    @Test
    public void testHandleOffseted() throws Exception {
        FeeId feeId = new FeeId("primaryKey");
        OffsetId offsetId = new OffsetId();
        Date offsetDate = new Date();

        AccountPayableEntryListener listener = new AccountPayableEntryListener();

        AccountPayableFeeEntryQueryRepository repository = mock(AccountPayableFeeEntryQueryRepository.class);
        when(repository.findOne(eq("primaryKey"))).thenReturn(new AccountPayableFeeEntry());

        listener.setRepository(repository);

        AccountPayableFeeOffsetedEvent event = new AccountPayableFeeOffsetedEvent(feeId, offsetId, offsetDate);
        listener.handleOffseted(event);

        ArgumentCaptor<AccountPayableFeeEntry> captor = ArgumentCaptor.forClass(AccountPayableFeeEntry.class);
        verify(repository).save(captor.capture());

//        AccountPayableFeeEntry entity = captor.getValue();
        AccountPayableFeeEntry entity = captor.getValue();

        assertThat(entity.getOffsetDate(), equalTo(offsetDate));
        assertThat(entity.isOffseted(), is(true));
        assertThat(entity.getOffsetId(), equalTo(offsetId.toString()));
    }

    @Test
    public void testHandleCancelled() throws Exception {
        FeeId feeId = new FeeId("primaryKey");
        CancelledReason cancelledReason = CancelledReason.INVALID_ADDRESS;
        Date cancelledDate = new Date();

        AccountPayableEntryListener listener = new AccountPayableEntryListener();

        AccountPayableFeeEntryQueryRepository repository = mock(AccountPayableFeeEntryQueryRepository.class);
        when(repository.findOne(eq("primaryKey"))).thenReturn(new AccountPayableFeeEntry());

        listener.setRepository(repository);

        AccountPayableFeeCancelledEvent event = new AccountPayableFeeCancelledEvent(feeId, cancelledReason, cancelledDate);
        listener.handleCancelled(event);

        ArgumentCaptor<AccountPayableFeeEntry> captor = ArgumentCaptor.forClass(AccountPayableFeeEntry.class);
        verify(repository).save(captor.capture());

//        AccountPayableFeeEntry entity = captor.getValue();
        AccountPayableFeeEntry entity = captor.getValue();

        assertThat(entity.getCancelledDate(), equalTo(cancelledDate));
        assertThat(entity.getCancelledReason(), is(cancelledReason));
        assertThat(entity.getFeeStatus(), is(FeeStatus.CANCELLED));
    }
}
