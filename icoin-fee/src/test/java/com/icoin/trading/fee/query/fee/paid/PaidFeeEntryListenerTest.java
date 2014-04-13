package com.icoin.trading.fee.query.fee.paid;

import com.icoin.trading.api.fee.domain.PaidMode;
import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeOffsetedEvent;
import org.joda.money.BigMoney;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-14
 * Time: AM12:56
 * To change this template use File | Settings | File Templates.
 */
public class PaidFeeEntryListenerTest {
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

        PaidFeeEntryListener listener = new PaidFeeEntryListener();

        PaidFeeEntryQueryRepository repository = mock(PaidFeeEntryQueryRepository.class);
        listener.setRepository(repository);

        PaidFeeCreatedEvent event =
                new PaidFeeCreatedEvent(feeId, feeStatus, amount, feeType, dueDate, businessCreationTime, portfolioId, userId, businessType, businessReferenceId, PaidMode.INTERNAL);
        listener.handleCreated(event);

        ArgumentCaptor<PaidFeeEntry> captor = ArgumentCaptor.forClass(PaidFeeEntry.class);
        verify(repository).save(captor.capture());

//        PaidFeeEntry entity = captor.getValue(); 
        PaidFeeEntry entity = captor.getValue();

        assertThat(entity.getPrimaryKey(), equalTo(feeId.toString()));
        assertThat(entity.getFeeStatus(), is(FeeStatus.PENDING));
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

        PaidFeeEntryQueryRepository repository = mock(PaidFeeEntryQueryRepository.class);
        when(repository.findOne(eq("primaryKey"))).thenReturn(new PaidFeeEntry());

        PaidFeeEntryListener listener = new PaidFeeEntryListener();
        listener.setRepository(repository);

        PaidFeeConfirmedEvent event = new PaidFeeConfirmedEvent(feeId, "sequenceNumber", confirmedDate);
        listener.handleConfirmed(event);

        ArgumentCaptor<PaidFeeEntry> captor = ArgumentCaptor.forClass(PaidFeeEntry.class);
        verify(repository).save(captor.capture());

//        PaidFeeEntry entity = captor.getValue(); 
        PaidFeeEntry entity = captor.getValue();

        assertThat(entity.getConfirmedDate(), equalTo(confirmedDate));
        assertThat(entity.getFeeStatus(), is(FeeStatus.CONFIRMED));
        assertThat(entity.getSequenceNumber(), equalTo("sequenceNumber"));
    }

    @Test
    public void testHandleOffseted() throws Exception {
        FeeId feeId = new FeeId("primaryKey");
        OffsetId offsetId = new OffsetId();
        Date offsetDate = new Date();

        PaidFeeEntryListener listener = new PaidFeeEntryListener();

        PaidFeeEntryQueryRepository repository = mock(PaidFeeEntryQueryRepository.class);
        when(repository.findOne(eq("primaryKey"))).thenReturn(new PaidFeeEntry());

        listener.setRepository(repository);

        PaidFeeOffsetedEvent event = new PaidFeeOffsetedEvent(feeId, offsetId, offsetDate);
        listener.handleOffseted(event);

        ArgumentCaptor<PaidFeeEntry> captor = ArgumentCaptor.forClass(PaidFeeEntry.class);
        verify(repository).save(captor.capture());

//        PaidFeeEntry entity = captor.getValue(); 
        PaidFeeEntry entity = captor.getValue();

        assertThat(entity.getOffsetDate(), equalTo(offsetDate));
        assertThat(entity.isOffseted(), is(true));
        assertThat(entity.getOffsetId(), equalTo(offsetId.toString()));
    }

    @Test
    public void testHandleCancelled() throws Exception {
        FeeId feeId = new FeeId("primaryKey");
        CancelledReason cancelledReason = CancelledReason.INVALID_ADDRESS;
        Date cancelledDate = new Date();

        PaidFeeEntryListener listener = new PaidFeeEntryListener();

        PaidFeeEntryQueryRepository repository = mock(PaidFeeEntryQueryRepository.class);
        when(repository.findOne(eq("primaryKey"))).thenReturn(new PaidFeeEntry());

        listener.setRepository(repository);

        PaidFeeCancelledEvent event = new PaidFeeCancelledEvent(feeId, cancelledReason, cancelledDate);
        listener.handleCancelled(event);

        ArgumentCaptor<PaidFeeEntry> captor = ArgumentCaptor.forClass(PaidFeeEntry.class);
        verify(repository).save(captor.capture());

//        PaidFeeEntry entity = captor.getValue(); 
        PaidFeeEntry entity = captor.getValue();

        assertThat(entity.getCancelledDate(), equalTo(cancelledDate));
        assertThat(entity.getCancelledReason(), is(cancelledReason));
        assertThat(entity.getFeeStatus(), is(FeeStatus.CANCELLED));
    }
}
