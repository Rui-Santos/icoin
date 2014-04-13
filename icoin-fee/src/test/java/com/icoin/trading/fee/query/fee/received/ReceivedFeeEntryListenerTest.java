package com.icoin.trading.fee.query.fee.received;

import com.icoin.trading.api.fee.domain.fee.BusinessType;
import com.icoin.trading.api.fee.domain.fee.CancelledReason;
import com.icoin.trading.api.fee.domain.fee.FeeId;
import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.domain.fee.FeeType;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.fee.domain.received.ReceivedSource;
import com.icoin.trading.api.fee.domain.received.ReceivedSourceType;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeOffsetedEvent;
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
public class ReceivedFeeEntryListenerTest {
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
        String businessReferenceId = "businessReferenceId";
        BusinessType businessType = BusinessType.CHARGE_COIN_COMMISSION;

        ReceivedFeeEntryListener listener = new ReceivedFeeEntryListener();

        ReceivedFeeEntryQueryRepository repository = mock(ReceivedFeeEntryQueryRepository.class);
        listener.setRepository(repository);

        ReceivedFeeCreatedEvent event =
                new ReceivedFeeCreatedEvent(feeId, feeStatus, amount, feeType, dueDate, businessCreationTime, portfolioId, userId, businessType,
                        businessReferenceId, new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, businessReferenceId));
        listener.handleCreated(event);

        ArgumentCaptor<ReceivedFeeEntry> captor = ArgumentCaptor.forClass(ReceivedFeeEntry.class);
        verify(repository).save(captor.capture());

//        ReceivedFeeEntry entity = captor.getValue(); 
        ReceivedFeeEntry entity = captor.getValue();

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
        assertThat(entity.getReceivedSource(), equalTo(new ReceivedSource(ReceivedSourceType.INTERNAL_ACCOUNT, businessReferenceId)));
    }

    @Test
    public void testHandleConfirmed() throws Exception {
        FeeId feeId = new FeeId("primaryKey");
        Date confirmedDate = new Date();

        ReceivedFeeEntryListener listener = new ReceivedFeeEntryListener();

        ReceivedFeeEntryQueryRepository repository = mock(ReceivedFeeEntryQueryRepository.class);
        when(repository.findOne(eq("primaryKey"))).thenReturn(new ReceivedFeeEntry());

        listener.setRepository(repository);

        ReceivedFeeConfirmedEvent event = new ReceivedFeeConfirmedEvent(feeId, BigMoney.parse("BTC 540.098"), confirmedDate);
        listener.handleConfirmed(event);

        ArgumentCaptor<ReceivedFeeEntry> captor = ArgumentCaptor.forClass(ReceivedFeeEntry.class);
        verify(repository).save(captor.capture());

//        ReceivedFeeEntry entity = captor.getValue(); 
        ReceivedFeeEntry entity = captor.getValue();

        assertThat(entity.getConfirmedDate(), equalTo(confirmedDate));
        assertThat(entity.getFeeStatus(), is(FeeStatus.CONFIRMED));
        assertThat(entity.getAmount(), equalTo(BigMoney.parse("BTC 540.098")));
    }

    @Test
    public void testHandleOffseted() throws Exception {
        FeeId feeId = new FeeId("primaryKey");
        OffsetId offsetId = new OffsetId();
        Date offsetDate = new Date();

        ReceivedFeeEntryListener listener = new ReceivedFeeEntryListener();

        ReceivedFeeEntryQueryRepository repository = mock(ReceivedFeeEntryQueryRepository.class);
        when(repository.findOne(eq("primaryKey"))).thenReturn(new ReceivedFeeEntry());

        listener.setRepository(repository);

        ReceivedFeeOffsetedEvent event = new ReceivedFeeOffsetedEvent(feeId, offsetId, offsetDate);
        listener.handleOffseted(event);

        ArgumentCaptor<ReceivedFeeEntry> captor = ArgumentCaptor.forClass(ReceivedFeeEntry.class);
        verify(repository).save(captor.capture());

//        ReceivedFeeEntry entity = captor.getValue(); 
        ReceivedFeeEntry entity = captor.getValue();

        assertThat(entity.getOffsetDate(), equalTo(offsetDate));
        assertThat(entity.isOffseted(), is(true));
        assertThat(entity.getOffsetId(), equalTo(offsetId.toString()));
    }

    @Test
    public void testHandleCancelled() throws Exception {
        FeeId feeId = new FeeId("primaryKey");
        CancelledReason cancelledReason = CancelledReason.INVALID_ADDRESS;
        Date cancelledDate = new Date();

        ReceivedFeeEntryListener listener = new ReceivedFeeEntryListener();

        ReceivedFeeEntryQueryRepository repository = mock(ReceivedFeeEntryQueryRepository.class);
        when(repository.findOne(eq("primaryKey"))).thenReturn(new ReceivedFeeEntry());

        listener.setRepository(repository);

        ReceivedFeeCancelledEvent event = new ReceivedFeeCancelledEvent(feeId, cancelledReason, cancelledDate);
        listener.handleCancelled(event);

        ArgumentCaptor<ReceivedFeeEntry> captor = ArgumentCaptor.forClass(ReceivedFeeEntry.class);
        verify(repository).save(captor.capture());

//        ReceivedFeeEntry entity = captor.getValue(); 
        ReceivedFeeEntry entity = captor.getValue();

        assertThat(entity.getCancelledDate(), equalTo(cancelledDate));
        assertThat(entity.getCancelledReason(), is(cancelledReason));
        assertThat(entity.getFeeStatus(), is(FeeStatus.CANCELLED));
    }
}
